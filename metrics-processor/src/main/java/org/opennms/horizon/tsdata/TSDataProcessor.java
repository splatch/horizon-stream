/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2022 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2022 The OpenNMS Group, Inc.
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

package org.opennms.horizon.tsdata;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.opennms.horizon.tsdata.metrics.MetricsPushAdapter;
import org.opennms.taskset.contract.DetectorResponse;
import org.opennms.taskset.contract.MonitorResponse;
import org.opennms.taskset.contract.TaskResult;
import org.opennms.taskset.contract.TaskSetResults;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.google.protobuf.InvalidProtocolBufferException;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@PropertySource("classpath:application.yml")
public class TSDataProcessor {
    private static final String MONITOR_METRICS_NAME = "response_time";
    private static final String METRICS_RESPONSE_UNIT = "msec";
    private static final String METRICS_LABEL_INSTANCE  = "instance";
    private static final String METRICS_LABEL_LOCATION  = "location";
    private static final String METRICS_LABEL_SYSTEM_ID = "system_id";
    private final MetricsPushAdapter pushAdapter;

    public TSDataProcessor(MetricsPushAdapter pushAdapter) {
        this.pushAdapter = pushAdapter;
    }

    @KafkaListener(topics = "${kafka.topics}", concurrency = "1")
    public void consume(byte[] data) {
        try {
            TaskSetResults results = TaskSetResults.parseFrom(data);
            results.getResultsList().forEach(result -> CompletableFuture.supplyAsync(()->{
                try {
                    if (result != null) {
                        log.info("Processing task set result {}", result);
                        if (result.hasMonitorResponse()) {
                            processMonitorResponse(result);
                        } else if (result.hasDetectorResponse()) {
                            DetectorResponse detectorResponse = result.getDetectorResponse();
                            // TBD: how to process?
                            log.info("Have detector response: task-id={}; detected={}", result.getId(), detectorResponse.getDetected());
                        }
                    } else {
                        log.warn("Task result appears to be missing the echo response details with results {}", results);
                    }
                } catch (Exception exc) {
                    // TODO: throttle
                    log.warn("Error processing task result", exc);
                }
                return null;
            }));
        } catch (InvalidProtocolBufferException e) {
            log.error("Invalid data from kafka", e);
        }
    }

    private void processMonitorResponse(TaskResult result) {
        final CollectorRegistry collectorRegistry = new CollectorRegistry();
        MonitorResponse response = result.getMonitorResponse();
        Map<String, String> labels = Map.of(METRICS_LABEL_INSTANCE, response.getIpAddress(), METRICS_LABEL_LOCATION,
            result.getLocation(), METRICS_LABEL_SYSTEM_ID, result.getSystemId());
        Gauge gauge = Gauge.build()
            .name(response.getMonitorType() + "_" + MONITOR_METRICS_NAME + "_" + METRICS_RESPONSE_UNIT)
            .help(response.getMonitorType().name() + " round trip response Time")
            .unit(METRICS_RESPONSE_UNIT)
            .labelNames(labels.keySet().toArray(new String[0]))
            .register(collectorRegistry);
        gauge.labels(labels.values().toArray(new String[0])).set(response.getResponseTimeMs());

        if(response.getMetricsMap()!=null) {
            response.getMetricsMap().forEach((k, v)-> {
                Gauge extGauge = Gauge.build()
                    .name(k)
                    .labelNames(labels.keySet().toArray(new String[0]))
                    .register(collectorRegistry);
                extGauge.labels(labels.values().toArray(new String[0])).set(v);
            });
        }
        pushAdapter.pushMetrics(collectorRegistry, labels);
    }
}
