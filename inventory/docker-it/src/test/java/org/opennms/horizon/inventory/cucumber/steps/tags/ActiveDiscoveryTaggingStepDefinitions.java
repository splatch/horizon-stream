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

package org.opennms.horizon.inventory.cucumber.steps.tags;

import com.google.protobuf.Empty;
import com.google.protobuf.Int64Value;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.lang3.NotImplementedException;
import org.opennms.horizon.inventory.cucumber.InventoryBackgroundHelper;
import org.opennms.horizon.inventory.discovery.IcmpActiveDiscoveryCreateDTO;
import org.opennms.horizon.inventory.discovery.IcmpActiveDiscoveryDTO;
import org.opennms.horizon.inventory.discovery.SNMPConfigDTO;
import org.opennms.horizon.inventory.dto.ActiveDiscoveryDTO;
import org.opennms.horizon.inventory.dto.DeleteTagsDTO;
import org.opennms.horizon.inventory.dto.ListAllTagsParamsDTO;
import org.opennms.horizon.inventory.dto.ListTagsByEntityIdParamsDTO;
import org.opennms.horizon.inventory.dto.TagCreateDTO;
import org.opennms.horizon.inventory.dto.TagCreateListDTO;
import org.opennms.horizon.inventory.dto.TagDTO;
import org.opennms.horizon.inventory.dto.TagEntityIdDTO;
import org.opennms.horizon.inventory.dto.TagListDTO;
import org.opennms.horizon.inventory.dto.TagListParamsDTO;
import org.opennms.horizon.inventory.dto.TagRemoveListDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


// using icmp active discovery here but can be any subclass of ActiveDiscovery
public class ActiveDiscoveryTaggingStepDefinitions {
    private final InventoryBackgroundHelper backgroundHelper;

    private IcmpActiveDiscoveryDTO activeDiscovery1;
    private IcmpActiveDiscoveryDTO activeDiscovery2;
    private TagListDTO addedTagList;
    private TagListDTO fetchedTagList;

    public ActiveDiscoveryTaggingStepDefinitions(InventoryBackgroundHelper backgroundHelper) {
        this.backgroundHelper = backgroundHelper;
    }

    /*
     * BACKGROUND GIVEN
     * *********************************************************************************
     */
    @Given("[ActiveDiscovery] External GRPC Port in system property {string}")
    public void externalGRPCPortInSystemProperty(String propertyName) {
        backgroundHelper.externalGRPCPortInSystemProperty(propertyName);
    }

    @Given("[ActiveDiscovery] Kafka Bootstrap URL in system property {string}")
    public void kafkaBootstrapURLInSystemProperty(String systemPropertyName) {
        backgroundHelper.kafkaBootstrapURLInSystemProperty(systemPropertyName);
    }

    @Given("[ActiveDiscovery] Grpc TenantId {string}")
    public void grpcTenantId(String tenantId) {
        backgroundHelper.grpcTenantId(tenantId);
    }

    @Given("[ActiveDiscovery] Create Grpc Connection for Inventory")
    public void createGrpcConnectionForInventory() {
        backgroundHelper.createGrpcConnectionForInventory();
    }

    /*
     * SCENARIO GIVEN
     * *********************************************************************************
     */
    @Given("A new active discovery for location named {string}")
    public void aNewActiveDiscovery(String location) {
        deleteAllTags();
        deleteAllActiveDiscovery();

        var activeDiscoveryServiceBlockingStub = backgroundHelper.getIcmpActiveDiscoveryServiceBlockingStub();
        activeDiscovery1 = activeDiscoveryServiceBlockingStub.createDiscovery(IcmpActiveDiscoveryCreateDTO.newBuilder()
            .setName("discovery-name").setLocationId(backgroundHelper.findLocationId(location))
                .addIpAddresses("127.0.0.1").setSnmpConfig(SNMPConfigDTO.newBuilder().addPorts(161)
                .addReadCommunity("public").build()).build());
    }

    @Given("2 new active discovery in {string}")
    public void twoNewActiveDiscovery(String location) {
        deleteAllTags();
        deleteAllActiveDiscovery();

        var activeDiscoveryServiceBlockingStub = backgroundHelper.getIcmpActiveDiscoveryServiceBlockingStub();
        activeDiscovery1 = activeDiscoveryServiceBlockingStub.createDiscovery(IcmpActiveDiscoveryCreateDTO.newBuilder()
            .setName("discovery-name-1").setLocationId(backgroundHelper.findLocationId(location))
                .addIpAddresses("127.0.0.1").setSnmpConfig(SNMPConfigDTO.newBuilder().addPorts(161)
                .addReadCommunity("public").build()).build());
        activeDiscovery2 = activeDiscoveryServiceBlockingStub.createDiscovery(IcmpActiveDiscoveryCreateDTO.newBuilder()
            .setName("discovery-name-2").setLocationId(backgroundHelper.findLocationId(location))
                .addIpAddresses("127.0.0.2").setSnmpConfig(SNMPConfigDTO.newBuilder().addPorts(161)
                .addReadCommunity("public").build()).build());
    }

    @Given("A new active discovery with tags {string} in {string}")
    public void aNewActiveDiscoveryWithTags(String tags, String location) {
        deleteAllTags();
        deleteAllActiveDiscovery();

        var activeDiscoveryServiceBlockingStub = backgroundHelper.getIcmpActiveDiscoveryServiceBlockingStub();
        activeDiscovery1 = activeDiscoveryServiceBlockingStub.createDiscovery(IcmpActiveDiscoveryCreateDTO.newBuilder()
            .setName("discovery-name-1").setLocationId(backgroundHelper.findLocationId(location))
            .addIpAddresses("127.0.0.1").setSnmpConfig(SNMPConfigDTO.newBuilder().addPorts(161)
                .addReadCommunity("public").build()).build());
        String[] tagArray = tags.split(",");
        var tagServiceBlockingStub = backgroundHelper.getTagServiceBlockingStub();
        List<TagCreateDTO> tagCreateList = getTagCreateList(tagArray);
        addedTagList = tagServiceBlockingStub.addTags(TagCreateListDTO.newBuilder()
            .addAllTags(tagCreateList)
            .addEntityIds(TagEntityIdDTO.newBuilder()
                .setActiveDiscoveryId(activeDiscovery1.getId())).build());
    }

    /*
     * SCENARIO WHEN
     * *********************************************************************************
     */
    @When("A GRPC request to create tags {string} for active discovery")
    public void aGRPCRequestToCreateTagsForActiveDiscovery(String tags) {
        String[] tagArray = tags.split(",");
        var tagServiceBlockingStub = backgroundHelper.getTagServiceBlockingStub();
        List<TagCreateDTO> tagCreateList = getTagCreateList(tagArray);
        fetchedTagList = tagServiceBlockingStub.addTags(TagCreateListDTO.newBuilder()
            .addAllTags(tagCreateList)
            .addEntityIds(TagEntityIdDTO.newBuilder()
                .setActiveDiscoveryId(activeDiscovery1.getId())).build());
    }

    @When("A GRPC request to create tags {string} for both active discovery")
    public void aGRPCRequestToCreateTagsForBothActiveDiscovery(String tags) {
        String[] tagArray = tags.split(",");
        var tagServiceBlockingStub = backgroundHelper.getTagServiceBlockingStub();
        List<TagCreateDTO> tagCreateList = getTagCreateList(tagArray);

        List<TagEntityIdDTO> tagEntityList = new ArrayList<>();
        tagEntityList.add(TagEntityIdDTO.newBuilder().setActiveDiscoveryId(activeDiscovery1.getId()).build());
        tagEntityList.add(TagEntityIdDTO.newBuilder().setActiveDiscoveryId(activeDiscovery2.getId()).build());

        fetchedTagList = tagServiceBlockingStub.addTags(TagCreateListDTO.newBuilder()
            .addAllTags(tagCreateList)
            .addAllEntityIds(tagEntityList).build());
    }

    @When("A GRPC request to fetch tags for active discovery")
    public void aGRPCRequestToFetchTagsForActiveDiscovery() {
        var tagServiceBlockingStub = backgroundHelper.getTagServiceBlockingStub();
        ListTagsByEntityIdParamsDTO params = ListTagsByEntityIdParamsDTO.newBuilder()
            .setEntityId(TagEntityIdDTO.newBuilder()
                .setActiveDiscoveryId(activeDiscovery1.getId()))
            .setParams(TagListParamsDTO.newBuilder().build()).build();
        fetchedTagList = tagServiceBlockingStub.getTagsByEntityId(params);
    }

    @When("A GRPC request to remove tag {string} for active discovery")
    public void aGRPCRequestToRemoveTagForActiveDiscovery(String tag) {
        var tagServiceBlockingStub = backgroundHelper.getTagServiceBlockingStub();
        for (TagDTO tagDTO : addedTagList.getTagsList()) {
            if (tagDTO.getName().equals(tag)) {
                tagServiceBlockingStub.removeTags(TagRemoveListDTO.newBuilder()
                    .addAllTagIds(Collections.singletonList(Int64Value.newBuilder()
                        .setValue(tagDTO.getId()).build()))
                    .addEntityIds(TagEntityIdDTO.newBuilder()
                        .setActiveDiscoveryId(activeDiscovery1.getId())).build());
                break;
            }
        }
        ListTagsByEntityIdParamsDTO params = ListTagsByEntityIdParamsDTO.newBuilder()
            .setEntityId(TagEntityIdDTO.newBuilder()
                .setActiveDiscoveryId(activeDiscovery1.getId()))
            .setParams(TagListParamsDTO.newBuilder().build()).build();
        fetchedTagList = tagServiceBlockingStub.getTagsByEntityId(params);
    }

    @When("A GRPC request to fetch all tags for active discovery with name like {string}")
    public void aGRPCRequestToFetchAllTagsForActiveDiscoveryWithNameLike(String searchTerm) {
        var tagServiceBlockingStub = backgroundHelper.getTagServiceBlockingStub();
        ListTagsByEntityIdParamsDTO params = ListTagsByEntityIdParamsDTO.newBuilder()
            .setEntityId(TagEntityIdDTO.newBuilder()
                .setActiveDiscoveryId(activeDiscovery1.getId()))
            .setParams(TagListParamsDTO.newBuilder().setSearchTerm(searchTerm).build()).build();
        fetchedTagList = tagServiceBlockingStub.getTagsByEntityId(params);
    }

    /*
     * SCENARIO THEN
     * *********************************************************************************
     */

    @Then("The active discovery tag response should contain only tags {string}")
    public void theActiveDiscoveryTagResponseShouldContainOnlyTags(String tags) {
        String[] tagArray = tags.split(",");

        assertNotNull(fetchedTagList);
        assertEquals(tagArray.length, fetchedTagList.getTagsCount());

        List<String> tagArraySorted = Arrays.stream(tagArray).sorted().toList();
        List<TagDTO> fetchedTagListSorted = fetchedTagList.getTagsList().stream()
            .sorted(Comparator.comparing(TagDTO::getName)).toList();

        for (int index = 0; index < tagArraySorted.size(); index++) {
            assertEquals(tagArraySorted.get(index), fetchedTagListSorted.get(index).getName());
        }
    }

    @Then("The active discovery tag response should contain an empty list of tags")
    public void theActiveDiscoveryTagResponseShouldContainAnEmptyListOfTags() {
        assertNotNull(fetchedTagList);
        assertEquals(0, fetchedTagList.getTagsCount());
    }

    @And("Both active discovery have the same tags of {string}")
    public void bothActiveDiscoveryHaveTheSameTagsOf(String tags) {
        String[] tagArray = tags.split(",");

        var tagServiceBlockingStub = backgroundHelper.getTagServiceBlockingStub();
        TagListDTO discovery1TagList = tagServiceBlockingStub.getTagsByEntityId(ListTagsByEntityIdParamsDTO.newBuilder()
            .setEntityId(TagEntityIdDTO.newBuilder().setActiveDiscoveryId(activeDiscovery1.getId())).build());
        TagListDTO discovery2TagList = tagServiceBlockingStub.getTagsByEntityId(ListTagsByEntityIdParamsDTO.newBuilder()
            .setEntityId(TagEntityIdDTO.newBuilder().setActiveDiscoveryId(activeDiscovery2.getId())).build());

        assertEquals(tagArray.length, discovery1TagList.getTagsCount());
        assertEquals(discovery1TagList.getTagsCount(), discovery2TagList.getTagsCount());

        List<String> tagArraySorted = Arrays.stream(tagArray).sorted().toList();
        List<TagDTO> discovery1TagListSorted = discovery1TagList.getTagsList().stream()
            .sorted(Comparator.comparing(TagDTO::getName)).toList();
        List<TagDTO> discovery2TagListSorted = discovery2TagList.getTagsList().stream()
            .sorted(Comparator.comparing(TagDTO::getName)).toList();

        assertEquals(discovery1TagListSorted, discovery2TagListSorted);

        for (int index = 0; index < tagArraySorted.size(); index++) {
            assertEquals(tagArraySorted.get(index), discovery1TagListSorted.get(index).getName());
        }
    }

    /*
     * INTERNAL
     * *********************************************************************************
     */
    private void deleteAllTags() {
        var tagServiceBlockingStub = backgroundHelper.getTagServiceBlockingStub();
        List<Int64Value> tagIds = tagServiceBlockingStub.getTags(ListAllTagsParamsDTO.newBuilder().build())
            .getTagsList().stream().map(tagDTO -> Int64Value.of(tagDTO.getId())).toList();
        tagServiceBlockingStub.deleteTags(DeleteTagsDTO.newBuilder().addAllTagIds(tagIds).build());
    }

    private void deleteAllActiveDiscovery() {
        var activeDiscoveryServiceBlockingStub = backgroundHelper.getActiveDiscoveryServiceBlockingStub();
        for (ActiveDiscoveryDTO discoveryDTO : activeDiscoveryServiceBlockingStub.listDiscoveries(Empty.newBuilder().build()).getActiveDiscoveriesList()) {
            Long activeDiscoveryId = switch (discoveryDTO.getActiveDiscoveryCase()) {
                case AZURE -> discoveryDTO.getAzure().getId();
                case ICMP -> discoveryDTO.getIcmp().getId();
                case ACTIVEDISCOVERY_NOT_SET ->
                    throw new NotImplementedException("Other types not implemented here yet");
            };
            activeDiscoveryServiceBlockingStub.deleteDiscovery(Int64Value.of(activeDiscoveryId));
        }
    }

    private static List<TagCreateDTO> getTagCreateList(String[] tagArray) {
        List<TagCreateDTO> tagCreateList = new ArrayList<>();
        for (String name : tagArray) {
            tagCreateList.add(TagCreateDTO.newBuilder().setName(name).build());
        }
        return tagCreateList;
    }
}
