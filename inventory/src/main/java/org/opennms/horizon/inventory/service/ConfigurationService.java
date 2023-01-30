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

package org.opennms.horizon.inventory.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.opennms.horizon.inventory.dto.ConfigurationDTO;
import org.opennms.horizon.inventory.mapper.ConfigurationMapper;
import org.opennms.horizon.inventory.model.Configuration;
import org.opennms.horizon.inventory.repository.ConfigurationRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConfigurationService {
    private final ConfigurationRepository modelRepo;

    private final ConfigurationMapper mapper;

    public Configuration createSingle(ConfigurationDTO newConfigurationDTO) {

        Optional<Configuration> configuration = modelRepo.getByKeyAndLocation(
            newConfigurationDTO.getTenantId(),
            newConfigurationDTO.getKey(),
            newConfigurationDTO.getLocation());

        return configuration.orElseGet(() -> modelRepo.save(mapper.dtoToModel(newConfigurationDTO)));

    }

    public List<ConfigurationDTO> findAll() {
        List<Configuration> all = modelRepo.findAll();
        return all
            .stream()
            .map(mapper::modelToDTO)
            .collect(Collectors.toList());
    }

    public List<ConfigurationDTO> findByLocation(String tenantId, String location) {
        List<Configuration> all = modelRepo.findByLocation(tenantId, location);
        return all
            .stream()
            .map(mapper::modelToDTO)
            .collect(Collectors.toList());
    }

    public List<ConfigurationDTO> findByKey(String tenantId, String key) {
        List<Configuration> all = modelRepo.findByKey(tenantId, key);
        return all
            .stream()
            .map(mapper::modelToDTO)
            .collect(Collectors.toList());
    }

    public Optional<ConfigurationDTO> getByKeyAndLocation(String tenantId, String key, String location) {
        Optional<Configuration> configuration = modelRepo.getByKeyAndLocation(tenantId, key, location);
        return configuration
            .map(mapper::modelToDTO);
    }

    public Optional<ConfigurationDTO> findByConfigurationId(Long id) {
        return modelRepo.findById(id).map(mapper::modelToDTO);
    }
}
