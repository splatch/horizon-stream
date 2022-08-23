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

package org.opennms.horizon.notifications.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.opennms.horizon.notifications.api.dto.*;
import org.opennms.horizon.notifications.exceptions.*;
import org.opennms.horizon.shared.dto.event.AlarmDTO;
import org.opennms.horizon.shared.dto.event.EventDTO;
import org.opennms.horizon.shared.dto.event.EventParameterDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PagerDutyAPIImpl implements PagerDutyAPI {
    private static final Logger LOG = LoggerFactory.getLogger(PagerDutyAPI.class);

    @Autowired
    PagerDutyDao pagerDutyDao;

    @Autowired
    RestTemplate restTemplate;

    @Override
    public void postNotification(AlarmDTO alarm) throws NotificationException {
        try {
            String event = getEvent(alarm);

            String baseUrl = "https://events.pagerduty.com/v2/enqueue";
            URI uri = new URI(baseUrl);
            String token = getAuthToken();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Token token="+token);
            headers.set("Accept", "application/vnd.pagerduty+json;version=2");
            headers.set("Content-Type", "application/json");

            HttpEntity<String> requestEntity = new HttpEntity<>(event, headers);

            LOG.info("Posting alarm:"+alarm.getId()+" to Pager Duty");
            restTemplate.exchange(uri, HttpMethod.POST, requestEntity, String.class);
        } catch (URISyntaxException e) {
            LOG.error("Bad pager duty url");
            throw new NotificationInternalException(e);
        } catch (JsonProcessingException e) {
            LOG.error("JSON error processing alarmDTO");
            throw new NotificationBadDataException(e);
        } catch (RestClientException e) {
            LOG.error("Pager Duty API exception:" + e);
            throw new NotificationAPIException(e);
        }
    }

    @Override
    public void initConfig(PagerDutyConfigDTO config) {
        pagerDutyDao.initConfig(config);
    }

    @Override
    public void validateConfig(PagerDutyConfigDTO config) throws NotificationException {
        callGetService(config);
    }

    private void callGetService(PagerDutyConfigDTO config) throws NotificationException {
        try {
            String baseUrl = "https://api.pagerduty.com/services?include%5B%5D=integrations";
            URI uri = new URI(baseUrl);
            String token = config.getToken();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Token token="+token);
            headers.set("Accept", "application/vnd.pagerduty+json;version=2");
            headers.set("Content-Type", "application/json");

            HttpEntity<String> requestEntity = new HttpEntity<>(headers);

            restTemplate.exchange(uri, HttpMethod.GET, requestEntity, String.class);
        } catch (URISyntaxException e) {
            LOG.error("Bad pager duty validation url");
            throw new NotificationInternalException(e);
        } catch (HttpClientErrorException e) {
            LOG.info("Invalid pager duty token");
            throw new NotificationBadDataException("Invalid pager duty token");
        }
    }

    private String getPagerDutyIntegrationKey() throws NotificationConfigUninitializedException {
        PagerDutyConfigDTO config = pagerDutyDao.getConfig();
        return config.getIntegrationkey();
    }

    private String getAuthToken() throws NotificationConfigUninitializedException {
        PagerDutyConfigDTO config = pagerDutyDao.getConfig();
        return config.getToken();
    }

    private String getEvent(AlarmDTO alarm) throws NotificationConfigUninitializedException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Instant now = Instant.now();

        PagerDutyEventDTO event = new PagerDutyEventDTO();
        PagerDutyPayloadDTO payload = new PagerDutyPayloadDTO();

        payload.setSummary(alarm.getLogMessage().trim());
        payload.setTimestamp(now.toString());
        AlarmSeverity alarmSeverity = AlarmSeverity.get(alarm.getSeverity());
        payload.setSeverity(PagerDutySeverity.fromAlarmSeverity(alarmSeverity).toString());

        if (alarm.getNodeLabel() != null) {
            payload.setSource(alarm.getNodeLabel());
        } else {
            payload.setSource("unknown");
        }
        String managedObjectType = alarm.getManagedObjectType();
        String managedObjectInstance = alarm.getManagedObjectInstance();
        if (managedObjectType != null && managedObjectType.length() > 0 && managedObjectInstance != null && managedObjectInstance.length() > 0) {
            // Use the MO type/instance if set
            payload.setComponent(String.format("%s - %s", alarm.getManagedObjectType(), alarm.getManagedObjectInstance()));
        }
        payload.setGroup("todo");
        payload.setClazz("class");

        event.setRouting_key(getPagerDutyIntegrationKey());
        event.setDedup_key(alarm.getReductionKey());

        if (AlarmSeverity.CLEARED.equals(alarmSeverity) || AlarmType.RESOLUTION.equals(alarm.getType())) {
            event.setEvent_action("resolve");
        } else if (alarm.getAckUser() != null && alarm.getAckUser().length() > 0) {
            event.setEvent_action("acknowledge");
        } else {
            event.setEvent_action("trigger");
        }

        //TODO: Add in alarm id into url
        event.setClient("OpenNMS");
        event.setClient_url("http://opennms.com");

        EventDTO lastEvent = alarm.getLastEvent();

        if (lastEvent != null) {
            Map<String, Object> customDetails = eparmsToMap(lastEvent.getParameters());
            payload.setCustomDetails(customDetails);
        }
        event.setPayload(payload);

        String bodyJson = objectMapper.writeValueAsString(event);
        return bodyJson;
    }

    protected static Map<String, Object> eparmsToMap(List<EventParameterDTO> eparms) {
        final Map<String, Object> map = new LinkedHashMap<>();
        if (eparms == null) {
            return map;
        }
        eparms.forEach(p -> map.put(p.getName(), p.getValue()));
        return map;
    }
}
