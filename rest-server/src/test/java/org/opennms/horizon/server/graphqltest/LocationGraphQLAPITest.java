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

package org.opennms.horizon.server.graphqltest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Arrays;
import java.util.Optional;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opennms.horizon.server.dao.MonitoringLocationRepository;
import org.opennms.horizon.server.model.entity.MonitoringLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
@AutoConfigureWebTestClient
public class LocationGraphQLAPITest {
    @Autowired
    private WebTestClient webClient;
    @MockBean
    private MonitoringLocationRepository mockRepo;
    private static final String URL_PATH = "/graphql";
    private final Long locationID1 = 1l;
    private final Long locationID2 = 2L;
    private final String location1 = "test_location1";
    private final String location2 = "test_location2";
    private final String tag1 = "test_tag1";
    private final String tag2 = "test_tag2";

    private MonitoringLocation locationEntity1;
    private MonitoringLocation locationEntity2;

    @BeforeEach
    public void setUp() {
        locationEntity1= new MonitoringLocation();
        locationEntity1.setId(locationID1);
        locationEntity1.setLocation(location1);
        locationEntity1.addTag(tag1);
        locationEntity1.addTag(tag2);

        locationEntity2 = new MonitoringLocation();
        locationEntity2.setLocation(location2);
        locationEntity2.setId(locationID2);
    }

    @Test
    public void testListAll() throws JSONException {
        String findAllQuery = "{getAllLocations{id location }}";
        doReturn(Arrays.asList(locationEntity1, locationEntity2)).when(mockRepo).findAll();
        webClient.post().uri(URL_PATH).contentType(MediaType.APPLICATION_JSON).bodyValue(toJson(findAllQuery)).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.data.getAllLocations").isNotEmpty()
                .jsonPath("$.data.getAllLocations.size()").isEqualTo(2)
                .jsonPath("$.data.getAllLocations[0].id").isEqualTo(locationID1)
                .jsonPath("$.data.getAllLocations[0].location").isEqualTo(location1)
                .jsonPath("$.data.getAllLocations[1].id").isEqualTo(locationID2)
                .jsonPath("$.data.getAllLocations[1].location").isEqualTo(location2);
        verify(mockRepo).findAll();
        verifyNoMoreInteractions(mockRepo);
    }

    @Test
    public void testFindById() throws JSONException {
        String findById = String.format("{getLocationById(id: %d){id location tags}}", locationID1);
        doReturn(Optional.of(locationEntity1)).when(mockRepo).findById(locationID1);
        webClient.post().uri(URL_PATH).contentType(MediaType.APPLICATION_JSON).bodyValue(toJson(findById)).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.data.getLocationById").isNotEmpty()
                .jsonPath("$.data.getLocationById.id").isEqualTo(locationID1)
                .jsonPath("$.data.getLocationById.location").isEqualTo(location1)
                .jsonPath("$.data.getLocationById.tags.size()").isEqualTo(2)
                .jsonPath("$.data.getLocationById.tags[0]").isEqualTo(tag1)
                .jsonPath("$.data.getLocationById.tags[1]").isEqualTo(tag2);
        verify(mockRepo).findById(locationID1);
    }

    @Test
    public void testCreateLocation() throws JSONException {
        String testArea = "test_area";
        String createLocation = String.format("mutation {addLocation(input: {location: \"%s\" monitoringArea: " +
                "\"%s\"}){id location monitoringArea tags}}", location1, testArea);
        locationEntity1.setMonitoringArea(testArea);
        doReturn(locationEntity1).when(mockRepo).save(any(MonitoringLocation.class));
        WebTestClient.BodyContentSpec result = webClient.post().uri(URL_PATH).contentType(MediaType.APPLICATION_JSON).bodyValue(toJson(createLocation)).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.data.addLocation").isNotEmpty()
                .jsonPath("$.data.addLocation.id").isEqualTo(locationID1)
                .jsonPath("$.data.addLocation.location").isEqualTo(location1)
                .jsonPath("$.data.addLocation.monitoringArea").isEqualTo(testArea)
                .jsonPath("$.data.addLocation.tags.size()").isEqualTo(2)
                .jsonPath("$.data.addLocation.tags.[0]").isEqualTo(tag1)
                .jsonPath("$.data.addLocation.tags.[1]").isEqualTo(tag2);
        verify(mockRepo).save(any(MonitoringLocation.class));
        verifyNoMoreInteractions(mockRepo);
    }

    @Test
    public void testUpdate() throws JSONException {
        String testArea = "test_area";
        String updateLocation = String.format("mutation {updateLocation(input: {location: \"%s\" monitoringArea: " +
                "\"%s\" tags: [\"%s\", \"%s\"]} id: %d ){id location monitoringArea tags}}", location2+"_updated", testArea, tag1, tag2, locationID2);
        doReturn(Optional.of(locationEntity2)).when(mockRepo).findById(locationID2);
        doReturn(locationEntity2).when(mockRepo).save(locationEntity2);
        WebTestClient.BodyContentSpec result = webClient.post().uri(URL_PATH).contentType(MediaType.APPLICATION_JSON).bodyValue(toJson(updateLocation)).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.data.updateLocation").isNotEmpty()
                .jsonPath("$.data.updateLocation.id").isEqualTo(locationID2)
                .jsonPath("$.data.updateLocation.location").isEqualTo(location2+"_updated")
                .jsonPath("$.data.updateLocation.monitoringArea").isEqualTo(testArea)
                .jsonPath("$.data.updateLocation.tags.size()").isEqualTo(2)
                .jsonPath("$.data.updateLocation.tags[0]").isEqualTo(tag1)
                .jsonPath("$.data.updateLocation.tags[1]").isEqualTo(tag2);
        verify(mockRepo).findById(locationID2);
        verify(mockRepo).save(locationEntity2);
        verifyNoMoreInteractions(mockRepo);
    }

    @Test
    public void testDelete() throws JSONException {
        String deleteRequest = String.format("mutation {deleteLocation(id: %d)}", locationID1);
        doReturn(Optional.of(locationEntity1)).when(mockRepo).findById(locationID1);
        doNothing().when(mockRepo).deleteById(locationID1);
        webClient.post().uri(URL_PATH).contentType(MediaType.APPLICATION_JSON).bodyValue(toJson(deleteRequest)).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.data.deleteLocation").isEqualTo(true);
        verify(mockRepo).findById(locationID1);
        verify(mockRepo).deleteById(locationID1);
        verifyNoMoreInteractions(mockRepo);
    }

    private String toJson(String query) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("query", query);
        return jsonObject.toString();
    }
}
