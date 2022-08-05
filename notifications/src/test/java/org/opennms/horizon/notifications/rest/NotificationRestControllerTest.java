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

package org.opennms.horizon.notifications.rest;

<<<<<<< HEAD
<<<<<<< HEAD
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.opennms.horizon.notifications.service.NotificationService;
=======
=======
import static org.mockito.ArgumentMatchers.any;
>>>>>>> edf80691 (HS-201: Call PagerDuty from notifications service)
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.opennms.horizon.notifications.api.dto.PagerDutyConfigDTO;
import org.opennms.horizon.notifications.dto.NotificationDTO;
import org.opennms.horizon.notifications.service.NotificationService;
>>>>>>> ca199ed0 (changed the interface name to keep consistency)
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest
public class NotificationRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationsService;

    @Test
    public void testGetPagerDutyKey() throws Exception {
        String key = "xyz";
        Mockito.when(notificationsService.getPagerDutyKey()).thenReturn(key);
        mockMvc.perform(get("/notifications/pagerDutyKey"))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    public void testPostNotification() throws Exception {
        String key = "xyz";
        String content = getNotification();
        Mockito.when(notificationsService.postNotification(any())).thenReturn(key);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(post("/notifications").headers(headers).content(content))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    public void testInitConfig() throws Exception {
        String content = getConfig();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(post("/notifications/config").headers(headers).content(content))
            .andDo(print())
            .andExpect(status().isOk());
    }

    private String getNotification() throws JsonProcessingException {
        NotificationDTO dto = new NotificationDTO();
        dto.setMessage("Message");
        dto.setDedupKey("dedup");

        ObjectMapper om = new ObjectMapper();
        return om.writeValueAsString(dto);
    }

    private String getConfig() throws JsonProcessingException {
        PagerDutyConfigDTO dto = new PagerDutyConfigDTO("token", "integration");

        ObjectMapper om = new ObjectMapper();
        return om.writeValueAsString(dto);
    }
}
