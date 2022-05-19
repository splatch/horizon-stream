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

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opennms.netmgt.provision.persistence.dao.RequisitionRepository;
import org.opennms.netmgt.provision.persistence.dto.RequisitionDTO;

@Slf4j
@RequiredArgsConstructor
public class ProvisionerImpl implements Provisioner {

    private final RequisitionRepository requisitionRepository;

    @Override
    public String publishRequisition(String requisition) {
        log.info("Publishing {}", requisition);
        RequisitionDTO requisitionDTO = new Gson().fromJson(requisition, RequisitionDTO.class);
        return requisitionRepository.save(requisitionDTO);
    }

    @Override
    public String read(String name) {
        RequisitionDTO requisitionDTO = requisitionRepository.read(name);
        return new Gson().toJson(requisitionDTO);
    }
}
