/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2016 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2016 The OpenNMS Group, Inc.
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

package org.opennms.horizon.minion.icmp.ipc.client;

import com.google.common.base.Preconditions;
import com.google.protobuf.InvalidProtocolBufferException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.opennms.horizon.grpc.ping.contract.PingRequest;
import org.opennms.horizon.grpc.ping.contract.PingResponse;
import org.opennms.netmgt.icmp.PingConstants;
import org.opennms.netmgt.icmp.proxy.PingRequestBuilder;
import org.opennms.netmgt.icmp.proxy.PingSequence;
import org.opennms.netmgt.icmp.proxy.PingSummary;

public class PingRequestBuilderImpl implements PingRequestBuilder {

    private final LocationAwarePingClientImpl client;
    private long timeout = PingConstants.DEFAULT_TIMEOUT;
    private int packetSize = PingConstants.DEFAULT_PACKET_SIZE;
    private int retries = PingConstants.DEFAULT_RETRIES;
    private int numberOfRequests = 1;
    private InetAddress inetAddress;
    private String location;
    private String systemId;
    private Callback callback;

    public PingRequestBuilderImpl(LocationAwarePingClientImpl client) {
        this.client = client;
    }

    @Override
    public PingRequestBuilder withTimeout(long timeout, TimeUnit unit) {
        Preconditions.checkArgument(timeout > 0, "timeout must be > 0");
        Objects.requireNonNull(unit);
        this.timeout = TimeUnit.MILLISECONDS.convert(timeout, unit);
        return this;
    }

    @Override
    public PingRequestBuilder withPacketSize(int packetSize) {
        Preconditions.checkArgument(packetSize > 0, "packetSize must be > 0");
        this.packetSize = packetSize;
        return this;
    }

    @Override
    public PingRequestBuilder withRetries(int retries) {
        Preconditions.checkArgument(retries >= 0, "retries must be >= 0");
        this.retries = retries;
        return this;
    }

    @Override
    public PingRequestBuilder withInetAddress(InetAddress inetAddress) {
        this.inetAddress = Objects.requireNonNull(inetAddress);
        return this;
    }

    @Override
    public PingRequestBuilder withLocation(String location) {
        this.location = location;
        return this;
    }

    @Override
    public PingRequestBuilder withSystemId(String systemId) {
        this.systemId = systemId;
        return this;
    }

    @Override
    public PingRequestBuilder withNumberOfRequests(int numberOfRequests) {
        Preconditions.checkArgument(numberOfRequests >= 1, "number of requests must be >= 1");
        this.numberOfRequests = numberOfRequests;
        return this;
    }

    @Override
    public PingRequestBuilder withProgressCallback(Callback callback) {
        this.callback = Objects.requireNonNull(callback);
        return this;
    }

    @Override
    public CompletableFuture<PingSummary> execute() {
        PingRequest request = PingRequest.newBuilder()
            .setInetAddress(inetAddress.toString())
            .setPacketSize(packetSize)
            .setTimeout(timeout)
            .setRetries(retries)
            .build();


        if (numberOfRequests > 1) {
            return CompletableFuture.failedFuture(new UnsupportedOperationException("Multiple requests are not supported for now."));
        }

        return client.execute(systemId, location, request).thenApply(response -> {
            try {
                return response.getPayload().unpack(PingResponse.class);
            } catch (InvalidProtocolBufferException e) {
                throw new RuntimeException(e);
            }
        }).thenApply(response -> {
            PingSummary summary = new PingSummary(wrapRequest(request), 1);
            summary.addSequence(new PingSequence(1, wrapResponse(response)));
            return summary;
        });
    }

    // TODO remove these - brought in just to keep basic bytecode compatibility
    private org.opennms.netmgt.icmp.proxy.PingRequest wrapRequest(PingRequest request) {
        org.opennms.netmgt.icmp.proxy.PingRequest pingRequest = new org.opennms.netmgt.icmp.proxy.PingRequest();
        try {
            pingRequest.setInetAddress(InetAddress.getByName(request.getInetAddress()));
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        return pingRequest;
    }

    private org.opennms.netmgt.icmp.proxy.PingResponse wrapResponse(PingResponse response) {
        org.opennms.netmgt.icmp.proxy.PingResponse pingResponse = new org.opennms.netmgt.icmp.proxy.PingResponse();
        pingResponse.setRtt(response.getRtt());
        return pingResponse;
    }
}
