package org.opennms.horizon.inventory.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.opennms.horizon.inventory.dto.IpInterfaceDTO;
import org.opennms.horizon.inventory.mapper.IpInterfaceMapper;
import org.opennms.horizon.inventory.model.IpInterface;
import org.opennms.horizon.inventory.model.Node;
import org.opennms.horizon.inventory.repository.IpInterfaceRepository;
import org.opennms.horizon.inventory.repository.NodeRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IpInterfaceService {
    private final IpInterfaceRepository modelRepo;

    private final IpInterfaceMapper mapper;

    public List<IpInterfaceDTO> findByTenantId(String tenantId) {
        List<IpInterface> all = modelRepo.findByTenantId(tenantId);
        return all
            .stream()
            .map(mapper::modelToDTO)
            .collect(Collectors.toList());
    }
}
