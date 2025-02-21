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

package org.opennms.horizon.inventory.mapper;


import java.net.InetAddress;
import java.net.UnknownHostException;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValueCheckStrategy;
import org.opennms.horizon.inventory.dto.IpInterfaceDTO;
import org.opennms.horizon.inventory.model.IpInterface;
import org.opennms.horizon.shared.utils.InetAddressUtils;
import org.opennms.node.scan.contract.IpInterfaceResult;

@Mapper(componentModel = "spring", uses = EmptyStringMapper.class)
public interface IpInterfaceMapper {

    @Mappings({
        @Mapping(target = "netmask", source = "netmask", qualifiedByName = "emptyString"),
        @Mapping(target = "tenantId", source = "tenantId", qualifiedByName = "emptyString"),
        @Mapping(target = "hostname", source = "hostname", qualifiedByName = "emptyString")
    })
    IpInterface dtoToModel(IpInterfaceDTO dto);

    @BeanMapping(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    IpInterfaceDTO modelToDTO(IpInterface model);

    @Mappings({
        @Mapping(target = "netmask", source = "netmask", qualifiedByName = "emptyString"),
        @Mapping(target = "hostname", source = "ipHostName", qualifiedByName = "emptyString")
    })
    IpInterface fromScanResult(IpInterfaceResult result);

    default InetAddress map(String value) throws UnknownHostException {
        return InetAddressUtils.getInetAddress(value);
    }

    default String map(InetAddress value) {
        return InetAddressUtils.toIpAddrString(value);
    }
}


