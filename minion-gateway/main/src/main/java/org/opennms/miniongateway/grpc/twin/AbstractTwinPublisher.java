/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2021 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2021 The OpenNMS Group, Inc.
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

package org.opennms.miniongateway.grpc.twin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.fge.jsonpatch.diff.JsonDiff;
import com.google.common.base.Strings;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.UUID;
import java.util.function.BiConsumer;
import javax.cache.Cache;
import javax.cache.Cache.Entry;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.lang.IgniteBiPredicate;
import org.opennms.cloud.grpc.minion.TwinRequestProto;
import org.opennms.cloud.grpc.minion.TwinResponseProto;
import org.opennms.horizon.shared.protobuf.marshalling.ProtoBufJsonSerializer;
import org.opennms.miniongateway.grpc.server.model.TenantKey;
import org.opennms.taskset.contract.TaskSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.MDC.MDCCloseable;

public abstract class AbstractTwinPublisher implements TwinPublisher, TwinProvider {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractTwinPublisher.class);
    public static final String TWIN_TRACKER_CACHE_NAME = "twinCache";

    protected final IgniteCache<SessionKey, TwinTracker> twinTrackerMap;
    protected final ObjectMapper objectMapper = new ObjectMapper();

    public AbstractTwinPublisher(Ignite ignite) {
        //TODO: Should probably pass a var args of classes from the impl ctor?
        configureProtobufJson(TaskSet.class);

        twinTrackerMap = ignite.cache(TWIN_TRACKER_CACHE_NAME);
    }

    /**
     * @param sinkUpdate Handle sink Update from @{@link AbstractTwinPublisher}.
     */
    protected abstract void handleSinkUpdate(TwinUpdate sinkUpdate);

    @Override
    public <T> Session<T> register(String key, Class<T> clazz, String tenantId, String location) throws IOException {
        try (MDCCloseable mdc = MDC.putCloseable("prefix", TwinConstants.LOG_PREFIX)) {
            SessionKey sessionKey = new SessionKey(key, tenantId, location);
            LOG.info("Registered a session with key {}", sessionKey);
            return new SessionImpl<>(sessionKey);
        }
    }

    @Override
    public TwinResponseProto getTwinResponse(String tenantId, TwinRequestProto twinRequest) {
        return mapTwinResponse(getTwin(tenantId, twinRequest));
    }

    protected TwinUpdate getTwin(String tenantId, TwinRequestProto twinRequest) {
        TwinTracker twinTracker = getTwinTracker(twinRequest.getConsumerKey(), tenantId, twinRequest.getLocation());
        TwinUpdate twinUpdate;
        if (twinTracker == null) {
            // No twin object exists for this key yet, return with null object.
            twinUpdate = new TwinUpdate(twinRequest.getConsumerKey(), tenantId, twinRequest.getLocation(), null);
        } else {
            // Fill TwinUpdate fields from TwinTracker.
            twinUpdate = new TwinUpdate(twinRequest.getConsumerKey(), tenantId, twinRequest.getLocation(), twinTracker.getObj());
            twinUpdate.setPatch(false);
            twinUpdate.setVersion(twinTracker.getVersion());
            twinUpdate.setSessionId(twinTracker.getSessionId());
        }
        return twinUpdate;
    }

    private synchronized TwinTracker getTwinTracker(String key, String tenantId, String location) {
        // Check if we have a session key specific to location else check session key without location.
        TwinTracker twinTracker = twinTrackerMap.get(new SessionKey(key, tenantId, location));
        if (twinTracker == null) {
            twinTracker = twinTrackerMap.get(new SessionKey(key, tenantId, null));
        }
        return twinTracker;
    }

    protected TwinResponseProto mapTwinResponse(TwinUpdate twinUpdate) {
        TwinResponseProto.Builder builder = TwinResponseProto.newBuilder();
        if (!Strings.isNullOrEmpty(twinUpdate.getLocation())) {
            builder.setLocation(twinUpdate.getLocation());
        }
        if(!Strings.isNullOrEmpty(twinUpdate.getSessionId())) {
            builder.setSessionId(twinUpdate.getSessionId());
        }
        builder.setConsumerKey(twinUpdate.getKey());
        if (twinUpdate.getObject() != null) {
            builder.setTwinObject(ByteString.copyFrom(twinUpdate.getObject()));
        }
        builder.setIsPatchObject(twinUpdate.isPatch());
        builder.setVersion(twinUpdate.getVersion());
        return builder.build();
    }

    protected TwinRequest mapTwinRequestProto(byte[] twinRequestBytes) {
        TwinRequest twinRequest = new TwinRequest();
        try {
            TwinRequestProto twinRequestProto = TwinRequestProto.parseFrom(twinRequestBytes);
            twinRequest.setKey(twinRequestProto.getConsumerKey());
            if (!Strings.isNullOrEmpty(twinRequestProto.getLocation())) {
                twinRequest.setLocation(twinRequestProto.getLocation());
            }
        } catch (InvalidProtocolBufferException e) {
            LOG.warn("Failed to parse protobuf for the request", e);
            throw new RuntimeException(e);
        }
        return twinRequest;
    }

    public static String generateTracingOperationKey(String location, String key) {
        return location != null ? key + "@" + location : key;
    }

    private synchronized TwinUpdate getResponseFromUpdatedObj(byte[] updatedObj, SessionKey sessionKey) {
        TwinTracker twinTracker = getTwinTracker(sessionKey.key, sessionKey.tenantId, sessionKey.location);
        if (twinTracker == null || !Arrays.equals(twinTracker.getObj(), updatedObj)) {
            TwinUpdate twinUpdate = new TwinUpdate(sessionKey.key, sessionKey.tenantId, sessionKey.location, updatedObj);
            if (twinTracker == null) {
                twinTracker = new TwinTracker(updatedObj);
            } else {
                // Generate patch and update response with patch.
                byte[] patchValue = getPatchValue(twinTracker.getObj(), updatedObj, sessionKey);
                if (patchValue != null) {
                    twinUpdate.setObject(patchValue);
                    twinUpdate.setPatch(true);
                }
                // Update Twin tracker with updated obj.
                twinTracker.update(updatedObj);
            }
            twinTrackerMap.put(sessionKey, twinTracker);
            twinUpdate.setVersion(twinTracker.getVersion());
            twinUpdate.setSessionId(twinTracker.getSessionId());
            return twinUpdate;
        }
        return null;
    }

    private byte[] getPatchValue(byte[] originalObj, byte[] updatedObj, SessionKey sessionKey) {
        try {
            JsonNode sourceNode = objectMapper.readTree(originalObj);
            JsonNode targetNode = objectMapper.readTree(updatedObj);
            JsonNode diffNode = JsonDiff.asJson(sourceNode, targetNode);
            return diffNode.toString().getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            LOG.error("Unable to generate patch for SessionKey {}", sessionKey, e);
        }
        return null;
    }

    private synchronized void removeSessionKey(SessionKey sessionKey) {
        twinTrackerMap.remove(sessionKey);
    }

    public synchronized void forEachSession(String tenantId, BiConsumer<SessionKey, TwinTracker> consumer) {
        ScanQuery<SessionKey, TwinTracker> query = new ScanQuery<>();
        query.setFilter(new IgniteBiPredicate<SessionKey, TwinTracker>() {
            @Override
            public boolean apply(SessionKey sessionKey, TwinTracker twinTracker) {
                return tenantId.equals(sessionKey.tenantId);
            }
        });
        QueryCursor<Entry<SessionKey, TwinTracker>> cursor = twinTrackerMap.query(query);
        cursor.forEach(entry -> consumer.accept(entry.getKey(), entry.getValue()));
    }

    private class SessionImpl<T> implements Session<T> {

        private final SessionKey sessionKey;

        public SessionImpl(SessionKey sessionKey) {
            this.sessionKey = sessionKey;
        }

        @Override
        public void publish(String tenantId, T obj) throws IOException {
            try (MDCCloseable mdc = MDC.putCloseable("prefix", TwinConstants.LOG_PREFIX)) {
                LOG.info("Published an object update for the session with key {}", sessionKey.toString());
                byte[] objInBytes = objectMapper.writeValueAsBytes(obj);
                TwinUpdate twinUpdate = getResponseFromUpdatedObj(objInBytes, sessionKey);
                if (twinUpdate != null) {
                    handleSinkUpdate(twinUpdate);
                }
            }
        }

        @Override
        public void close() throws IOException {
            removeSessionKey(sessionKey);
            LOG.info("Closed session with key {} ", sessionKey);
        }
    }

    public static class SessionKey implements Serializable {

        public final String key;
        public final String tenantId;
        public final String location;

        private SessionKey(String key, String tenantId, String location) {
            this.key = key;
            this.tenantId = tenantId;
            this.location = location;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SessionKey that = (SessionKey) o;
            return Objects.equals(key, that.key) && Objects.equals(tenantId, that.tenantId) &&
                Objects.equals(location, that.location);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, tenantId, location);
        }

        @Override
        public String toString() {
            return "SessionKey{" +
                    "key='" + key + '\'' +
                    ", tenant-id='" + tenantId + '\'' +
                    ", location='" + location + '\'' +
                    '}';
        }
    }


//========================================
// Internals
//----------------------------------------

    private void configureProtobufJson(Class<? extends Message>... protobufClasses) {
        SimpleModule simpleModule = new SimpleModule();

        Arrays.stream(protobufClasses).forEach(clazz -> simpleModule.addSerializer(new ProtoBufJsonSerializer<>(clazz)));
//        simpleModule.addSerializer(new ProtoBufJsonSerializer<>(TaskSet.class));

        objectMapper.registerModule(simpleModule);
    }
}
