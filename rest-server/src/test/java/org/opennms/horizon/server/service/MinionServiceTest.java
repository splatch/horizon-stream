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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opennms.horizon.shared.dto.minion.MinionCollectionDTO;
import org.opennms.horizon.shared.dto.minion.MinionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;

import io.leangen.graphql.execution.ResolutionEnvironment;

@SpringBootTest
public class MinionServiceTest {
    @MockBean
    private PlatformGateway mockGateway;
    @MockBean
    private ResolutionEnvironment mockEnv;
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private MinionService minionService;
    private MinionCollectionDTO collectionDTO;
    private MinionDTO dto1;
    private MinionDTO dto2;
    private String authHeader = "Authorization: abdcd";

    @BeforeEach
    public void setup() {
        dto1 = new MinionDTO();
        dto1.setId("test-minion-1");
        dto2 = new MinionDTO();
        dto2.setId("test-minion-2");
        collectionDTO = new MinionCollectionDTO(Arrays.asList(dto1, dto2));
        doReturn(authHeader).when(mockGateway).getAuthHeader(mockEnv);
    }

    @Test
    public void testListMinions() {
        ResponseEntity response = ResponseEntity.ok(collectionDTO);
        doReturn(response).when(mockGateway).get(PlatformGateway.URL_PATH_MINIONS, authHeader, MinionCollectionDTO.class);
        MinionCollectionDTO result = minionService.listMinions(mockEnv);
        assertEquals(collectionDTO, result);
        MinionCollectionDTO result2 = minionService.listMinions(mockEnv);
        assertEquals(collectionDTO, result2);
        verify(mockGateway, times(2)).getAuthHeader(mockEnv);
        //now cache for list minions
        verify(mockGateway, times(2)).get(PlatformGateway.URL_PATH_MINIONS, authHeader, MinionCollectionDTO.class);
        verifyNoMoreInteractions(mockGateway);
    }

    @Test
    public void testGetByIDAfterList() {
        ResponseEntity listResponse = ResponseEntity.ok(collectionDTO);
        doReturn(listResponse).when(mockGateway).get(PlatformGateway.URL_PATH_MINIONS, authHeader, MinionCollectionDTO.class);
        MinionCollectionDTO listResult = minionService.listMinions(mockEnv);
        assertEquals(collectionDTO, listResult);

        MinionDTO minionDTO = minionService.getMinionById(dto1.getId(), mockEnv);
        assertEquals(dto1, minionDTO);
        verify(mockGateway).getAuthHeader(mockEnv);
        verify(mockGateway).get(PlatformGateway.URL_PATH_MINIONS, authHeader, MinionCollectionDTO.class);
        verifyNoMoreInteractions(mockGateway); //get by id from cache
    }

    @Test
    public void testGetByID() {
        ResponseEntity response = ResponseEntity.ok(dto1);
        doReturn(response).when(mockGateway).get(String.format(PlatformGateway.URL_PATH_MINIONS_ID, dto1.getId()), authHeader, MinionDTO.class);
        MinionDTO result1 = minionService.getMinionById(dto1.getId(), mockEnv);
        assertEquals(dto1, result1);
        MinionDTO result2 = minionService.getMinionById(dto1.getId(), mockEnv);
        assertEquals(dto1, result2);
        verify(mockGateway).getAuthHeader(mockEnv);
        verify(mockGateway).get(String.format(PlatformGateway.URL_PATH_MINIONS_ID, dto1.getId()), authHeader, MinionDTO.class);
        verifyNoMoreInteractions(mockGateway);
    }
}
