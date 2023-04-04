/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2019 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2019 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.horizon.minion.grpc;

import static org.opennms.horizon.minion.grpc.GrpcClientConstants.CLIENT_CERTIFICATE_FILE_PATH;
import static org.opennms.horizon.minion.grpc.GrpcClientConstants.CLIENT_PRIVATE_KEY_FILE_PATH;
import static org.opennms.horizon.minion.grpc.GrpcClientConstants.CLIENT_PRIVATE_KEY_PASSWORD;
import static org.opennms.horizon.minion.grpc.GrpcClientConstants.DEFAULT_GRPC_HOST;
import static org.opennms.horizon.minion.grpc.GrpcClientConstants.DEFAULT_GRPC_PORT;
import static org.opennms.horizon.minion.grpc.GrpcClientConstants.DEFAULT_MESSAGE_SIZE;
import static org.opennms.horizon.minion.grpc.GrpcClientConstants.GRPC_CLIENT_PID;
import static org.opennms.horizon.minion.grpc.GrpcClientConstants.GRPC_HOST;
import static org.opennms.horizon.minion.grpc.GrpcClientConstants.GRPC_MAX_INBOUND_SIZE;
import static org.opennms.horizon.minion.grpc.GrpcClientConstants.GRPC_PORT;
import static org.opennms.horizon.minion.grpc.GrpcClientConstants.OVERRIDE_AUTHORITY;
import static org.opennms.horizon.minion.grpc.GrpcClientConstants.TLS_ENABLED;
import static org.opennms.horizon.minion.grpc.GrpcClientConstants.TRUST_CERTIFICATE_FILE_PATH;
import static org.opennms.horizon.shared.ipc.rpc.api.RpcModule.MINION_HEADERS_MODULE;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.opennms.cloud.grpc.minion.CloudServiceGrpc;
import org.opennms.cloud.grpc.minion.CloudServiceGrpc.CloudServiceStub;
import org.opennms.cloud.grpc.minion.CloudToMinionMessage;
import org.opennms.cloud.grpc.minion.Identity;
import org.opennms.cloud.grpc.minion.MinionToCloudMessage;
import org.opennms.cloud.grpc.minion.RpcRequestProto;
import org.opennms.cloud.grpc.minion.RpcResponseProto;
import org.opennms.cloud.grpc.minion.SinkMessage;
import org.opennms.horizon.minion.grpc.rpc.RpcRequestHandler;
import org.opennms.horizon.shared.ipc.rpc.IpcIdentity;
import org.opennms.horizon.shared.ipc.rpc.api.minion.ClientRequestDispatcher;
import org.opennms.horizon.shared.ipc.sink.api.MessageConsumerManager;
import org.opennms.horizon.shared.ipc.sink.api.SinkModule;
import org.opennms.horizon.shared.ipc.sink.common.AbstractMessageDispatcherFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.MDC.MDCCloseable;

import com.codahale.metrics.MetricRegistry;
import com.google.protobuf.ByteString;
import com.google.protobuf.Message;

import io.grpc.ConnectivityState;
import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NegotiationType;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContextBuilder;
import io.grpc.stub.StreamObserver;
import io.opentracing.Tracer;

/**
 * Minion GRPC client runs both RPC/Sink together.
 * gRPC runs in a typical web server/client mode, so gRPC client runs on each minion and gRPC server runs on OpenNMS.
 * Minion GRPC Client tries to get two stubs/streams (RPC/Sink) from OpenNMS server when it is in active state.
 * It also initializes RPC observer/handler to receive requests from OpenNMS.
 * <p>
 * RPC : RPC runs in bi-directional streaming mode. Minion gets each request and it handles each request in a separate
 * thread. Once the request is executed, the response sender call is synchronized as writing to observer is not thread-safe.
 * Minion also sends it's headers (SystemId/location) to OpenNMS whenever the stub is initialized.
 * <p>
 * Sink: Sink runs in uni-directional streaming mode. If the sink module is async and OpenNMS Server is not active, the
 * messages are buffered and blocked till minion is able to connect to OpenNMS.
 */
public class MinionGrpcClient extends AbstractMessageDispatcherFactory<String> implements ClientRequestDispatcher {

    static final String SINK_METRIC_PRODUCER_DOMAIN = "org.opennms.core.ipc.sink.producer";

    private static final Logger LOG = LoggerFactory.getLogger(MinionGrpcClient.class);
    private static final long SINK_BLOCKING_TIMEOUT = 1000;
    private static final int SINK_BLOCKING_THREAD_POOL_SIZE = 100;
    private ManagedChannel channel;
    private CloudServiceStub asyncStub;
    private Properties properties;
    private BundleContext bundleContext;
    private IpcIdentity ipcIdentity;
    private MetricRegistry metricRegistry;
    private StreamObserver<RpcResponseProto> rpcStream;
    private StreamObserver<MinionToCloudMessage> sinkStream;
    private ConnectivityState currentChannelState;

    private final AtomicLong counter = new AtomicLong();
    private final ThreadFactory blockingSinkMessageThreadFactory = (runnable) -> new Thread(runnable, "blocking-sink-message-" + counter.incrementAndGet());
    // Each request is handled in a new thread which unmarshals and executes the request.
    // This maintains a blocking thread for each dispatch module when OpenNMS is not in active state.
    private final ScheduledExecutorService blockingSinkMessageScheduler = Executors.newScheduledThreadPool(SINK_BLOCKING_THREAD_POOL_SIZE,
            blockingSinkMessageThreadFactory);
    private final Map<SinkMessage, ScheduledFuture<?>> pendingMessages = new ConcurrentHashMap<>(2000);
    private ReconnectStrategy reconnectStrategy;
    private Tracer tracer;
    private RpcRequestHandler rpcRequestHandler;
    private CloudMessageHandler cloudMessageHandler;

    public MinionGrpcClient(IpcIdentity ipcIdentity, ConfigurationAdmin configAdmin) {
        this(ipcIdentity, ConfigUtils.getPropertiesFromConfig(configAdmin, GRPC_CLIENT_PID), new MetricRegistry(), null);
    }

    public MinionGrpcClient(IpcIdentity ipcIdentity, ConfigurationAdmin configAdmin, MetricRegistry metricRegistry, Tracer tracer) {
        this(ipcIdentity, ConfigUtils.getPropertiesFromConfig(configAdmin, GRPC_CLIENT_PID), metricRegistry, tracer);
    }

    public MinionGrpcClient(IpcIdentity ipcIdentity, Properties properties) {
        this(ipcIdentity, properties, new MetricRegistry(), null);
    }

    public MinionGrpcClient(IpcIdentity ipcIdentity, Properties properties, MetricRegistry metricRegistry, Tracer tracer) {
        this.ipcIdentity = ipcIdentity;
        this.properties = properties;
        this.metricRegistry = metricRegistry;
        this.tracer = tracer;
    }

    public void start() throws IOException {
        String host = PropertiesUtils.getProperty(properties, GRPC_HOST, DEFAULT_GRPC_HOST);
        int port = PropertiesUtils.getProperty(properties, GRPC_PORT, DEFAULT_GRPC_PORT);
        boolean tlsEnabled = PropertiesUtils.getProperty(properties, TLS_ENABLED, false);
        int maxInboundMessageSize = PropertiesUtils.getProperty(properties, GRPC_MAX_INBOUND_SIZE, DEFAULT_MESSAGE_SIZE);
        String overrideAuthority = properties.getProperty(OVERRIDE_AUTHORITY);

        NettyChannelBuilder channelBuilder = NettyChannelBuilder.forAddress(host, port)
                .keepAliveWithoutCalls(true)
                .maxInboundMessageSize(maxInboundMessageSize);

        // If an override authority was specified, configure it now.  Setting the override authority to match the CN
        //  of the server certificate prevents hostname verification errors of the server certificate when the CN does
        //  not match the hostname used to connect the server.
        if (overrideAuthority != null) {
            channelBuilder.overrideAuthority(overrideAuthority);
        }

        if (tlsEnabled) {
            channel = channelBuilder
                    .negotiationType(NegotiationType.TLS)
                    .sslContext(buildSslContext().build())
                    .build();
            LOG.info("TLS enabled for gRPC");
        } else {
            channel = channelBuilder.usePlaintext().build();
        }
        asyncStub = CloudServiceGrpc.newStub(channel);

        reconnectStrategy = new SimpleReconnectStrategy(channel, () -> {
            initializeRpcStub();
            initializeSinkStub();
            initializeCloudReceiver();
        }, () -> {
            rpcStream = null;
            sinkStream = null;
        });
        reconnectStrategy.activate();

        LOG.info("Minion at location {} with systemId {} started", ipcIdentity.getLocation(), ipcIdentity.getId());

    }

    private SslContextBuilder buildSslContext() {
        SslContextBuilder builder = GrpcSslContexts.forClient();
        String clientCertChainFilePath = properties.getProperty(CLIENT_CERTIFICATE_FILE_PATH);
        String clientPrivateKeyFilePath = properties.getProperty(CLIENT_PRIVATE_KEY_FILE_PATH);
        String clientPrivateKeyPassword = properties.getProperty(CLIENT_PRIVATE_KEY_PASSWORD);
        String trustCertCollectionFilePath = properties.getProperty(TRUST_CERTIFICATE_FILE_PATH);

        if (trustCertCollectionFilePath != null) {
            builder.trustManager(new File(trustCertCollectionFilePath));
        }
        if (clientCertChainFilePath != null && clientPrivateKeyFilePath != null) {
            builder.keyManager(new File(clientCertChainFilePath), new File(clientPrivateKeyFilePath), clientPrivateKeyPassword);
        }
        return builder;
    }

    private void initializeRpcStub() {
        rpcStream = asyncStub.cloudToMinionRPC(new RpcMessageHandler());
        // Need to send minion headers to gRPC server in order to register.
        sendMinionHeaders();
        LOG.info("Initialized RPC stream");
    }

    private void initializeSinkStub() {
        sinkStream = asyncStub.minionToCloudMessages(new EmptyMessageReceiver());
        LOG.info("Initialized Sink stream");
    }

    private void initializeCloudReceiver() {
        Identity identity = Identity.newBuilder()
            .setLocation(ipcIdentity.getLocation())
            .setSystemId(ipcIdentity.getId())
            .build();
        asyncStub.cloudToMinionMessages(identity, new CloudMessageObserver(cloudMessageHandler));
        LOG.info("Initialized cloud receiver stream");
    }

    public void setCloudMessageHandler(CloudMessageHandler cloudMessageHandler) {
        this.cloudMessageHandler = cloudMessageHandler;
    }


    private boolean hasChangedToReadyState() {
        ConnectivityState prevState = currentChannelState;
        return !prevState.equals(ConnectivityState.READY) && getChannelState().equals(ConnectivityState.READY);
    }

    public void shutdown() {
        blockingSinkMessageScheduler.shutdown();
        if (rpcStream != null) {
            rpcStream.onCompleted();
        }
        channel.shutdown();
        LOG.info("Minion at location {} with systemId {} stopped", ipcIdentity.getLocation(), ipcIdentity.getId());
    }

    public void setRpcRequestHandler(RpcRequestHandler rpcRequestHandler) {
        this.rpcRequestHandler = rpcRequestHandler;
    }

    @Override
    public String getMetricDomain() {
        return SINK_METRIC_PRODUCER_DOMAIN;
    }

    @Override
    public BundleContext getBundleContext() {
        return bundleContext;
    }

    @Override
    public Tracer getTracer() {
        return tracer;
    }

    @Override
    public MetricRegistry getMetrics() {
        return metricRegistry;
    }

    ConnectivityState getChannelState() {
        return channel.getState(true);
    }

    @Override
    // public <S extends org.opennms.horizon.ipc.sink.api.Message, T extends org.opennms.horizon.ipc.sink.api.Message> void dispatch(SinkModule<S, T> module, String metadata, T message) {
    public <S extends Message, T extends Message> void dispatch(SinkModule<S, T> module, String metadata, T message) {

        try (MDCCloseable mdc = MDC.putCloseable("prefix", MessageConsumerManager.LOG_PREFIX)) {
            byte[] sinkMessageContent = module.marshal(message);
            String messageId = UUID.randomUUID().toString();
            SinkMessage.Builder sinkMessageBuilder = SinkMessage.newBuilder()
                    .setMessageId(messageId)
                    .setLocation(ipcIdentity.getLocation())
                    .setModuleId(module.getId())
                    .setContent(ByteString.copyFrom(sinkMessageContent));

            // If module has asyncpolicy, keep attempting to send message.
            if (module.getAsyncPolicy() != null) {
                sendBlockingSinkMessage(sinkMessageBuilder.build());
            } else {
                sendSinkMessage(sinkMessageBuilder.build());
            }
        }
    }

    private void sendBlockingSinkMessage(SinkMessage sinkMessage) {
        boolean succeeded = sendSinkMessage(sinkMessage);
        if (succeeded) {
            return;
        }
        //Recursively try to send sink message until it succeeds.
        scheduleSinkMessageAfterDelay(sinkMessage);
    }

    private void scheduleSinkMessageAfterDelay(SinkMessage sinkMessage) {
        // try until we get a successful send
        ScheduledFuture<?> future = blockingSinkMessageScheduler.scheduleWithFixedDelay(() -> {
            if (sendSinkMessage(sinkMessage)) {
                // canceling myself?
                pendingMessages.remove(sinkMessage).cancel(false);
            }
        }, SINK_BLOCKING_TIMEOUT, SINK_BLOCKING_TIMEOUT, TimeUnit.MILLISECONDS);
        pendingMessages.put(sinkMessage, future);
    }


    private synchronized boolean sendSinkMessage(SinkMessage sinkMessage) {
        if (sinkStream != null) {
            try {
                sinkStream.onNext(MinionToCloudMessage.newBuilder()
                    .setSinkMessage(sinkMessage)
                    .build()
                );
                return true;
            } catch (Throwable e) {
                LOG.error("Exception while sending sinkMessage to gRPC IPC server", e);
            }
        }
        return false;
    }


    private void sendMinionHeaders() {
        RpcResponseProto rpcHeader = RpcResponseProto.newBuilder()
                .setLocation(ipcIdentity.getLocation())
                .setSystemId(ipcIdentity.getId())
                .setModuleId(MINION_HEADERS_MODULE)
                .setRpcId(ipcIdentity.getId())
                .build();
        sendRpcResponse(rpcHeader);
        LOG.info("Sending Minion Headers from SystemId {} to gRPC server", ipcIdentity.getId());
    }

    private void processRpcRequest(RpcRequestProto requestProto) {
        if (rpcRequestHandler != null) {
            rpcRequestHandler.handle(requestProto).whenComplete((response, error) -> {
                if (error != null) {
                    LOG.warn("Failed to handle request {}", requestProto, error);
                    rpcStream.onError(error);
                    return;
                }
                sendRpcResponse(response);
            });
            return;
        }
        LOG.warn("Ignored RPC request {}, client has no rpc request handler set", requestProto);
    }

    private synchronized void sendRpcResponse(RpcResponseProto rpcResponseProto) {
        if (rpcStream != null) {
            try {
                rpcStream.onNext(rpcResponseProto);
            } catch (Exception e) {
                LOG.error("Exception while sending RPC response : {}", rpcResponseProto);
            }
        } else {
            //throw new RuntimeException("RPC response handler not found");
            LOG.warn("gRPC IPC server is not in ready state");
        }
    }

    @Override
    public CompletableFuture<RpcResponseProto> call(RpcRequestProto requestProto) {
        CompletableFuture<RpcResponseProto> future = new CompletableFuture<>();
        asyncStub.minionToCloudRPC(requestProto, new StreamObserver<>() {
            @Override
            public void onNext(RpcResponseProto value) {
                future.complete(value);
            }

            @Override
            public void onError(Throwable throwable) {
                future.completeExceptionally(throwable);
            }

            @Override
            public void onCompleted() {
                if (!future.isDone() && !future.isCancelled()) {
                    future.cancel(false);
                }
            }
        });
        return future;
    }

    private class RpcMessageHandler implements StreamObserver<RpcRequestProto> {

        @Override
        public void onNext(RpcRequestProto rpcRequestProto) {
            processRpcRequest(rpcRequestProto);
        }

        @Override
        public void onError(Throwable throwable) {
            LOG.error("Error in RPC streaming", throwable);
            reconnectStrategy.activate();
        }

        @Override
        public void onCompleted() {
            LOG.error("Closing RPC message handler");
            reconnectStrategy.activate();
        }

    }

    private class CloudMessageObserver implements StreamObserver<CloudToMinionMessage> {

        private final CloudMessageHandler cloudRequestHandler;

        public CloudMessageObserver(CloudMessageHandler cloudRequestHandler) {
            this.cloudRequestHandler = cloudRequestHandler;
        }

        @Override
        public void onNext(CloudToMinionMessage value) {
            cloudRequestHandler.handle(value);
        }

        @Override
        public void onError(Throwable throwable) {
            LOG.error("Error in cloud message receiver", throwable);
            reconnectStrategy.activate();
        }

        @Override
        public void onCompleted() {
            LOG.error("Closing cloud message receiver");
            reconnectStrategy.activate();
        }
    }

}
