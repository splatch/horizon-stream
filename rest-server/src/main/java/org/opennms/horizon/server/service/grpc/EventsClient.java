/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2023 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2023 The OpenNMS Group, Inc.
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
package org.opennms.horizon.server.service.grpc;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.opennms.horizon.events.proto.Event;
import org.opennms.horizon.events.proto.EventServiceGrpc;
import org.opennms.horizon.shared.constants.GrpcConstants;

import com.google.protobuf.Empty;
import com.google.protobuf.UInt64Value;

import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EventsClient {
    private final ManagedChannel channel;
    private final long deadline;

    private EventServiceGrpc.EventServiceBlockingStub eventsStub;

    protected void initialStubs() {
        eventsStub = EventServiceGrpc.newBlockingStub(channel);
    }

    public void shutdown() {
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown();
        }
    }

    public List<Event> listEvents(String accessToken) {
        Metadata metadata = new Metadata();
        metadata.put(GrpcConstants.AUTHORIZATION_METADATA_KEY, accessToken);
        return eventsStub.withInterceptors(MetadataUtils.newAttachHeadersInterceptor(metadata)).withDeadlineAfter(deadline, TimeUnit.MILLISECONDS).listEvents(Empty.newBuilder().build()).getEventsList();
    }

    public List<Event> getEventsByNodeId(long nodeId, String accessToken) {
        Metadata metadata = new Metadata();
        metadata.put(GrpcConstants.AUTHORIZATION_METADATA_KEY, accessToken);
        return eventsStub.withInterceptors(MetadataUtils.newAttachHeadersInterceptor(metadata)).withDeadlineAfter(deadline, TimeUnit.MILLISECONDS).getEventsByNodeId(UInt64Value.of(nodeId)).getEventsList();
    }
}
