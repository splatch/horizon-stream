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

package org.opennms.horizon.server.config;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.dataloader.BatchLoader;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderRegistry;
import org.opennms.horizon.server.mapper.LocationMapper;
import org.opennms.horizon.server.model.inventory.Location;
import org.opennms.horizon.server.service.grpc.InventoryClient;
import org.springframework.stereotype.Component;

import io.leangen.graphql.spqr.spring.autoconfigure.DataLoaderRegistryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class DataLoaderFactory implements DataLoaderRegistryFactory {
    public static final String DATA_LOADER_LOCATION = "location";
    private final InventoryClient inventoryClient;
    private final LocationMapper locationMapper;
    private final BatchLoader<Long, Location> locationBatchLoader = this::locations;

    @Override
    public DataLoaderRegistry createDataLoaderRegistry() {
        DataLoader<Long, Location> locationDataLoader = new DataLoader<>(locationBatchLoader);
        DataLoaderRegistry loaders = new DataLoaderRegistry();
        loaders.register(DATA_LOADER_LOCATION, locationDataLoader);
        return loaders;
    }

    private CompletableFuture<List<Location>> locations(List<Long> locationIds) {
        return CompletableFuture.completedFuture(
            inventoryClient.listLocationsByIds(locationIds)
                .stream().map(locationMapper::protoToLocation).collect(Collectors.toList()));
    }
}
