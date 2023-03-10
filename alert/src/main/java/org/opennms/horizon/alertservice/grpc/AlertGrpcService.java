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

package org.opennms.horizon.alertservice.grpc;

import com.google.protobuf.BoolValue;
import com.google.protobuf.UInt64Value;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.opennms.horizon.alerts.proto.Alert;
import org.opennms.horizon.alerts.proto.AlertServiceGrpc;
import org.opennms.horizon.alerts.proto.ListAlertsRequest;
import org.opennms.horizon.alerts.proto.ListAlertsResponse;
import org.opennms.horizon.alertservice.api.AlertService;
import org.opennms.horizon.alertservice.db.repository.AlertRepository;
import org.opennms.horizon.alertservice.service.AlertMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AlertGrpcService extends AlertServiceGrpc.AlertServiceImplBase {
    private final AlertMapper alertMapper;
    private final AlertRepository alertRepository;
    private final AlertService alertService;

    @Override
    public void listAlerts(ListAlertsRequest request, StreamObserver<ListAlertsResponse> responseObserver) {
        List<Alert> alerts = alertRepository.findAll().stream()
            .map(alertMapper::toProto)
            .toList();

        ListAlertsResponse response = ListAlertsResponse.newBuilder()
            .addAllAlerts(alerts)
            .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteAlert(UInt64Value request, StreamObserver<BoolValue> responseObserver) {
        alertService.deleteAlertById(request.getValue());
        responseObserver.onNext(BoolValue.of(true));
        responseObserver.onCompleted();
    }

    @Override
    public void acknowledgeAlert(UInt64Value request, StreamObserver<Alert> responseObserver) {
        responseObserver.onNext(alertService.acknowledgeAlertById(request.getValue()).orElse(null));
        responseObserver.onCompleted();
    }

    @Override
    public void unacknowledgeAlert(UInt64Value request, StreamObserver<Alert> responseObserver) {
        responseObserver.onNext(alertService.unacknowledgeAlertById(request.getValue()).orElse(null));
        responseObserver.onCompleted();
    }

}
