package org.opennms.horizon.minion.snmp.ipc.internal;

import com.google.protobuf.InvalidProtocolBufferException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.stream.Collectors;
import org.opennms.cloud.grpc.minion.RpcRequestProto;
import org.opennms.horizon.grpc.snmp.contract.SnmpGetRequest;
import org.opennms.horizon.grpc.snmp.contract.SnmpMultiResponse;
import org.opennms.horizon.grpc.snmp.contract.SnmpRequest;
import org.opennms.horizon.grpc.snmp.contract.SnmpResponse;
import org.opennms.horizon.grpc.snmp.contract.SnmpResult.Builder;
import org.opennms.horizon.grpc.snmp.contract.SnmpWalkRequest;
import org.opennms.horizon.shared.ipc.rpc.api.client.RpcHandler;
import org.opennms.horizon.shared.snmp.AggregateTracker;
import org.opennms.horizon.shared.snmp.Collectable;
import org.opennms.horizon.shared.snmp.CollectionTracker;
import org.opennms.horizon.shared.snmp.ColumnTracker;
import org.opennms.horizon.shared.snmp.SingleInstanceTracker;
import org.opennms.horizon.shared.snmp.SnmpAgentConfig;
import org.opennms.horizon.shared.snmp.SnmpInstId;
import org.opennms.horizon.shared.snmp.SnmpObjId;
import org.opennms.horizon.shared.snmp.SnmpResult;
import org.opennms.horizon.shared.snmp.SnmpUtils;
import org.opennms.horizon.shared.snmp.SnmpValue;
import org.opennms.horizon.shared.snmp.SnmpWalkCallback;
import org.opennms.horizon.shared.snmp.SnmpWalker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SnmpRpcHandler implements RpcHandler<SnmpRequest, SnmpMultiResponse> {

    public static final String RPC_MODULE_ID = "SNMP";

    private static final ExecutorService REAPER_EXECUTOR = Executors.newCachedThreadPool(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "SNMP-Proxy-RPC-Session-Reaper");
        }
    });

    private final Logger logger = LoggerFactory.getLogger(SnmpRpcHandler.class);

    @Override
    public CompletableFuture<SnmpMultiResponse> execute(SnmpRequest request) {
        CompletableFuture<List<SnmpResponse>> combinedFuture = CompletableFuture.completedFuture(new ArrayList<>());
        for (SnmpGetRequest getRequest : request.getGetsList()) {
            CompletableFuture<SnmpResponse> future = get(request, getRequest);
            combinedFuture = combinedFuture.thenCombine(future, (m, s) -> {
                m.add(s);
                return m;
            });
        }
        if (request.getWalksCount() > 0) {
            CompletableFuture<List<SnmpResponse>> future = walk(request, request.getWalksList());
            combinedFuture = combinedFuture.thenCombine(future, (m, s) -> {
                m.addAll(s);
                return m;
            });
        }

        return combinedFuture.thenApply(responseList -> {
            SnmpMultiResponse.Builder builder = SnmpMultiResponse.newBuilder();
            responseList.forEach(builder::addResponses);
            return builder.build();
        });
    }

    @Override
    public String getId() {
        return RPC_MODULE_ID;
    }

    @Override
    public SnmpRequest unmarshal(RpcRequestProto request) {
        try {
            return request.getPayload().unpack(SnmpRequest.class);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
    }

    private CompletableFuture<List<SnmpResponse>> walk(SnmpRequest request, List<SnmpWalkRequest> walks) {
        final CompletableFuture<List<SnmpResponse>> future = new CompletableFuture<>();
        final Map<String, SnmpResponse.Builder> responsesByCorrelationId = new LinkedHashMap<>();

        final List<Collectable> trackers = new ArrayList<>(walks.size());
        for (final SnmpWalkRequest walk : walks) {
            CollectionTracker tracker;
            if (walk.hasInstance()) {
                if (walk.getOidsCount() != 1) {
                    future.completeExceptionally(new IllegalArgumentException("Single instance requests must have a single OID."));
                    return future;
                }
                final SnmpObjId oid = SnmpObjId.get(walk.getOids(0));
                tracker = new SingleInstanceTracker(oid, new SnmpInstId(walk.getInstance())) {
                    @Override
                    protected void storeResult(SnmpResult res) {
                        addResult(res, walk.getCorrelationId(), responsesByCorrelationId);
                    }
                };
            } else {
                final Collection<Collectable> columnTrackers = walk.getOidsList().stream()
                    .map(SnmpObjId::get)
                    .map(ColumnTracker::new)
                    .collect(Collectors.toList());
                tracker = new AggregateTracker(columnTrackers) {
                    @Override
                    protected void storeResult(SnmpResult res) {
                        addResult(res, walk.getCorrelationId(), responsesByCorrelationId);
                    }
                };
            }
            if (walk.hasMaxRepetitions()) {
                tracker.setMaxRepetitions(walk.getMaxRepetitions());
            }
            trackers.add(tracker);
        }

        AggregateTracker aggregate = new AggregateTracker(trackers);
        final SnmpWalker walker = SnmpUtils.createWalker(mapAgent(request.getAgent()), request.getDescription(), aggregate);
        walker.setCallback(new SnmpWalkCallback() {
            @Override
            public void complete(SnmpWalker tracker, Throwable t) {
                try {
                    if (t != null) {
                        future.completeExceptionally(t);
                    } else {
                        List<SnmpResponse> responseList = responsesByCorrelationId.entrySet().stream()
                            .map(entry -> entry.getValue().build())
                            .collect(Collectors.toList());
                        future.complete(responseList);
                    }
                } finally {
                    // Close the tracker using a separate thread
                    // This allows the SnmpWalker to clean up properly instead
                    // of interrupting execution as it's executing the callback
                    REAPER_EXECUTOR.submit(new Runnable() {
                        @Override
                        public void run() {
                            tracker.close();
                        }
                    });
                }
            }
        });
        walker.start();
        return future;
    }

    private SnmpAgentConfig mapAgent(String agent) {
        return null;
    }

    private static final void addResult(SnmpResult result, String correlationId, Map<String, SnmpResponse.Builder> responsesByCorrelationId) {
        SnmpResponse.Builder response = responsesByCorrelationId.get(correlationId);
        if (response == null) {
            response = SnmpResponse.newBuilder()
                .setCorrelationId(correlationId);
            responsesByCorrelationId.put(correlationId, response);
        }
        response.addResults(mapResult(result));
    }

    private static org.opennms.horizon.grpc.snmp.contract.SnmpResult mapResult(SnmpResult result) {
        return org.opennms.horizon.grpc.snmp.contract.SnmpResult.newBuilder()
            .setBase(result.getBase().toString())
            .setInstance(result.getInstance().toString())
            // TODO make sure value encoding is honored
            .setValue(result.getValue().toString())
            .build();
    }

    private CompletableFuture<SnmpResponse> get(SnmpRequest request, SnmpGetRequest get) {
        final SnmpObjId[] oids = get.getOidsList().toArray(new SnmpObjId[get.getOidsCount()]);
        final CompletableFuture<SnmpValue[]> future = SnmpUtils.getAsync(mapAgent(request.getAgent()), oids);

        return future.thenApply(values -> {
            SnmpResponse.Builder response = SnmpResponse.newBuilder().setCorrelationId(get.getCorrelationId());
            final List<org.opennms.horizon.grpc.snmp.contract.SnmpResult> results = new ArrayList<>(oids.length);
            if (values.length < oids.length) {
                // Should never reach here, should have thrown exception in SnmpUtils.
                logger.warn("Received error response from SNMP for the agent {} for oids = {}", request.getAgent(), oids);
            } else {
                for (int i = 0; i < oids.length; i++) {
                    Builder valueBuilder = org.opennms.horizon.grpc.snmp.contract.SnmpResult.newBuilder()
                        .setBase(oids[i].toString())
                        .setValue(values[i].toString());
                    response.addResults(valueBuilder.build());
                }
            }
            return response.build();
        });
    }
}
