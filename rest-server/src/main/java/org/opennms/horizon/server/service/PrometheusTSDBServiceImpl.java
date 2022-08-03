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

import java.util.Date;
import java.util.List;

import org.opennms.horizon.server.model.TimeSerialsData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
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
    public  TimeSerialsData getMinionUpTime(String minionId) {
        return getLatestTSData(METRIC_MINION_UPTIME, minionId);
    }

    @GraphQLQuery
    @Override
    public TimeSerialsData getMinionResponseTime(String minionId) {
        return getLatestTSData(METRIC_MINION_RESPONSE, minionId);
    }

    @GraphQLQuery
    @Override
    public TimeSerialsData getICMPRoundTripTime(String ipAddress) {
        return getLatestTSData(ICMP_ROUND_TRIP, ipAddress);
    }

    @GraphQLQuery
    @Override
    public TimeSerialsData getSNMPUPTime(String ipAddress) {
        return getLatestTSData(SNMP_UPTIME, ipAddress);
    }

    private TimeSerialsData getLatestTSData(String metricName, String instance) {
        String requestStr = String.format(QUERY_TEMPLATE, metricName, instance);
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        ResponseEntity<JsonNode> response = restTemplate.exchange(tsdbURL+requestStr, HttpMethod.GET, new HttpEntity<>(headers), JsonNode.class);
        TimeSerialsData data = new TimeSerialsData();
        if(response.getStatusCode().equals(HttpStatus.OK)) {
            data.setMetricName(metricName);
            data.setInstance(instance);
            populateDataWithJsonNode(data, response.getBody());
        }
        return data;
    }

    private void populateDataWithJsonNode(TimeSerialsData data, JsonNode jsonNode) {
        JsonNode result = jsonNode.get("data").get("result");
        if(result != null && result.size()> 0) {
            for(JsonNode node: (ArrayNode)result) {
                if(node.get("metric").get("instance").asText().equals(data.getInstance())) {
                    ArrayNode value = (ArrayNode) result.get(0).get("value");
                    data.setTime(new Date((long) (value.get(0).asDouble() * 1000)));
                    data.setValue(value.get(1).asLong());
                    break;
                }
            }
        }
    }
}
