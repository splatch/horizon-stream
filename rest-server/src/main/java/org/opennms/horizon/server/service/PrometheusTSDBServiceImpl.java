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

package org.opennms.horizon.server.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.opennms.horizon.server.model.TimeSeriesData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;

@GraphQLApi
@Service
public class PrometheusTSDBServiceImpl implements TSDBService {

    @Value("${tsdb.url}")
    private String tsdbURL;

    private static final String QUERY_TEMPLATE = "?query=%s&instance=%s";
    private static final String METRIC_MINION_UPTIME = "minion_uptime_sec";
    private static final String METRIC_MINION_RESPONSE = "minion_response_time_msec";
    private static final String SNMP_UPTIME = "snmp_uptime_sec";
    private static final String ICMP_ROUND_TRIP = "icmp_round_trip_time_msec";

    private final RestTemplate restTemplate = new RestTemplate();

    @GraphQLQuery
    @Override
    public TimeSeriesData getMetric(String name, String instance, Map<String, String> labels) {
        String requestStr = String.format(QUERY_TEMPLATE, name, instance);
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        ResponseEntity<JsonNode> response = restTemplate.exchange(tsdbURL+requestStr, HttpMethod.GET, new HttpEntity<>(headers), JsonNode.class);
        TimeSeriesData data = new TimeSeriesData();
        if(response.getStatusCode().equals(HttpStatus.OK)) {
            data.setMetricName(name);
            data.setInstance(instance);
            if(response.hasBody()) {
                populateDataWithJsonNode(data, response.getBody(), labels);
            }
        }
        return data;
    }

    private void populateDataWithJsonNode(TimeSeriesData data, JsonNode jsonNode, Map<String, String> labels) {
        ArrayNode results = (ArrayNode) jsonNode.path("data").path("result");
        List<JsonNode> filteredList = new ArrayList<>();
        for(JsonNode result: results) {
            JsonNode metric = result.path("metric");
            if(metric.get("instance").asText().equals(data.getInstance())&& labelMatched(metric, labels)) {
                filteredList.add(result);
            }
        }
        if(filteredList.size()>0) { //todo is there cases more than one result node found?
            JsonNode metricResult = filteredList.get(0);
            JsonNode valueNode = metricResult.path("value");
            data.setTime(new Date((long)valueNode.get(0).asDouble()*1000));
            data.setValue(valueNode.get(1).asDouble());
        }
    }
    private boolean labelMatched(JsonNode metric, Map<String, String> labels) {
        boolean matched = true;
        if(labels != null && labels.size() > 0) {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> metricKeyValues = mapper.convertValue(metric, new TypeReference<Map<String, String>>() {
            });

            for(Map.Entry<String, String> label: labels.entrySet()) {
                if(!metricKeyValues.containsKey(label.getKey()) || !metricKeyValues.get(label.getKey()).equals(label.getValue())) {
                    matched = false;
                    break;
                }
            }
        }
        return matched;
    }
}
