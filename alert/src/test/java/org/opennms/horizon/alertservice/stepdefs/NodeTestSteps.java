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

package org.opennms.horizon.alertservice.stepdefs;

import com.google.protobuf.MessageOrBuilder;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Transpose;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import lombok.RequiredArgsConstructor;
import org.opennms.horizon.alert.tag.proto.TagListProto;
import org.opennms.horizon.alertservice.AlertGrpcClientUtils;
import org.opennms.horizon.alertservice.kafkahelper.KafkaTestHelper;
import org.opennms.horizon.inventory.dto.NodeDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RequiredArgsConstructor
public class NodeTestSteps {
    private final KafkaTestHelper kafkaTestHelper;
    private final BackgroundSteps background;
    private String nodeTopic;
    private String tenantId;
    private List<NodeDTO.Builder> builders = new ArrayList<>();

    @Given("Kafka node topic {string}")
    public void kafkaTagTopic(String nodeTopic) {
        this.nodeTopic = nodeTopic;
        kafkaTestHelper.setKafkaBootstrapUrl(background.getKafkaBootstrapUrl());
        kafkaTestHelper.startConsumerAndProducer(nodeTopic, nodeTopic);
    }

    @Given("[Node] Tenant {string}")
    public void tenant(String tenantId) {
        this.tenantId = tenantId;
    }

    @Given("[Node] operation data")
    public void nodeData(DataTable data) {
        for (Map<String, String> map : data.asMaps()) {
            NodeDTO.Builder builder = NodeDTO.newBuilder();
            builder.setNodeLabel(map.get("label"))
                .setId(Long.valueOf(map.get("id")))
                .setTenantId(map.get("tenant_id"));

            builders.add(builder);
        }
    }

    @And("Sent node message to Kafka topic")
    public void sentMessageToKafkaTopic() {
        for (NodeDTO.Builder builder : builders) {
            NodeDTO node = builder.build();
            kafkaTestHelper.sendToTopic(nodeTopic, node.toByteArray(), tenantId);
        }
    }
}
