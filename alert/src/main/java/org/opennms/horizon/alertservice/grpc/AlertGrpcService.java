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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.opennms.horizon.alerts.proto.Alert;
import org.opennms.horizon.alerts.proto.AlertServiceGrpc;
import org.opennms.horizon.alerts.proto.ListAlertsRequest;
import org.opennms.horizon.alerts.proto.ListAlertsResponse;
import org.opennms.horizon.alertservice.api.AlertService;
import org.opennms.horizon.alertservice.db.repository.AlertRepository;
import org.opennms.horizon.alertservice.service.AlertMapper;
import org.opennms.horizon.model.common.proto.Severity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.google.protobuf.BoolValue;
import com.google.protobuf.UInt64Value;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class AlertGrpcService extends AlertServiceGrpc.AlertServiceImplBase {
    public static final int PAGE_SIZE_DEFAULT = 10;
    public static final String PAGE_DEFAULT = "0";
    public static final String SORT_BY_DEFAULT = "alertId";
    private final AlertMapper alertMapper;
    private final AlertRepository alertRepository;
    private final AlertService alertService;

    @Override
    public void listAlerts(ListAlertsRequest request, StreamObserver<ListAlertsResponse> responseObserver) {
        // Extract the page size and next page token values from the request
        int pageSize = request.getPageSize() != 0 ? request.getPageSize() : PAGE_SIZE_DEFAULT;
        String nextPageToken = !request.getNextPageToken().isEmpty() ? request.getNextPageToken() : PAGE_DEFAULT;

        String filter = !request.getFilter().isEmpty() ? request.getFilter() : "";
        List<String> filterValue = !request.getFilterValuesList().isEmpty() ? request.getFilterValuesList() : List.of();
        String sortBy = !request.getSortBy().isEmpty() ? request.getSortBy() : SORT_BY_DEFAULT;
        boolean sortAscending = request.getSortAscending();

        // Create a PageRequest object based on the page size, next page token, filter, and sort parameters
        Sort.Direction sortDirection = sortAscending ? Sort.Direction.ASC : Sort.Direction.DESC;
        PageRequest pageRequest = PageRequest.of(Integer.parseInt(nextPageToken), pageSize, Sort.by(sortDirection, sortBy));
        Date[] timeRange;
        log.info("pageRequest: {}", pageRequest);
        Page<org.opennms.horizon.alertservice.db.entity.Alert> alertPage =
            switch (filter) {
                case "severity":
                    yield alertRepository.findBySeverityIn(filterValue.stream().map(Severity::valueOf).toList(), pageRequest);
                case "time":
                    timeRange = getTimeRange(filterValue);
                    log.info("Start Date: {}", timeRange[0]);
                    log.info("End Date: {}", timeRange[1]);
                    yield alertRepository.findByLastEventTimeBetween(timeRange[0], timeRange[1], pageRequest);
                case "severityAndTime":
                    timeRange = getTimeRange(filterValue);
                    log.info("Start Date: {}", timeRange[0]);
                    log.info("End Date: {}", timeRange[1]);
                    yield alertRepository.findBySeverityInAndLastEventTimeBetween(filterValue.stream().limit(filterValue.size() - 1).map(Severity::valueOf).toList(), timeRange[0], timeRange[1], pageRequest);
                default:
                    // Fetch a page of alerts without applying any filters
                    yield alertRepository.findAll(pageRequest);
            };

        List<Alert> alerts = alertPage.getContent().stream()
            .map(alertMapper::toProto)
            .toList();

        ListAlertsResponse.Builder responseBuilder = ListAlertsResponse.newBuilder()
            .addAllAlerts(alerts);

        // If there is a next page, add the page number to the response's next_page_token field
        if (alertPage.hasNext()) {
            responseBuilder.setNextPageToken(String.valueOf(alertPage.nextPageable().getPageNumber()));
        }

        // Set last_page_token
        responseBuilder.setLastPageToken(String.valueOf(alertPage.getTotalPages() - 1));

        // Build the final ListAlertsResponse object and send it to the client using the responseObserver
        ListAlertsResponse response = responseBuilder.build();
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

    private Date[] getTimeRange(List<String> filterValue) {
        Date startDate, endDate;
        switch (filterValue.get(filterValue.size() - 1)) {
            case "7d" -> {
                startDate = new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7));
                endDate = new Date();
            }
            case "24h" -> {
                startDate = new Date(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(24));
                endDate = new Date();
            }
            case "today" -> {
                LocalDate currentDate = LocalDate.now();
                LocalDateTime currentDateTime = LocalDateTime.of(currentDate, LocalTime.MIDNIGHT);
                startDate = Date.from(currentDateTime.atZone(ZoneId.systemDefault()).toInstant());
                endDate = new Date();
            }
            default -> {
                // default last 14 days
                startDate = new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(14));
                endDate = new Date();
            }
        }
        return new Date[]{startDate, endDate};
    }
}
