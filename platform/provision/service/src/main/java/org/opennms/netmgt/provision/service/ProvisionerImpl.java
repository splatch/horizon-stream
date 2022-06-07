/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2008-2022 The OpenNMS Group, Inc.
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

package org.opennms.netmgt.provision.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ProducerTemplate;
import org.opennms.horizon.db.dao.api.NodeDao;
import org.opennms.horizon.db.model.OnmsIpInterface;
import org.opennms.horizon.db.model.OnmsNode;
import org.opennms.horizon.repository.api.NodeRepository;
import org.opennms.netmgt.provision.persistence.dao.RequisitionRepository;
import org.opennms.netmgt.provision.persistence.dto.RequisitionDTO;
import org.opennms.netmgt.provision.persistence.dto.RequisitionNodeDTO;
import org.opennms.netmgt.provision.scan.NodeScanner;

@Slf4j
@RequiredArgsConstructor
public class ProvisionerImpl implements Provisioner {

    private final RequisitionRepository requisitionRepository;
    private final ProducerTemplate scanProducer;
    private final NodeRepository nodeRepository;

    @Override
    public String publish(RequisitionDTO requisition) {
        log.info("Publishing {}", requisition);
        requisition.validate();

        requisition.getNodes().values().forEach(node -> processNode(node));
        return requisitionRepository.save(requisition);
    }

    private void processNode(RequisitionNodeDTO nodeDTO) {
        OnmsNode node = new OnmsNode();
        node.setLabel(nodeDTO.getNodeLabel());

        OnmsIpInterface iface = new OnmsIpInterface("192.168.1.1");
        iface.setIsManaged("M");
        iface.setSnmpPrimary("N");
        node.getIpInterfaces().add(iface);

        nodeRepository.save(node);
    }

    @Override
    public Optional<RequisitionDTO> read(String name) {
        return Optional.ofNullable(requisitionRepository.read(name));
    }

    @Override
    public void delete(String name) {
        requisitionRepository.delete(name);
    }

    @Override
    public String update(RequisitionDTO requisitionDTO) throws Exception {
        return requisitionRepository.update(requisitionDTO);
    }

    @Override
    public List<RequisitionDTO> read() {
        return requisitionRepository.read();
    }

    @Override
    public void performNodeScan() {
        scanProducer.sendBody(NodeScanner.DIRECT_SCAN,"blah");
    }
}
