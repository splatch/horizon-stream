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
    private final NodeRepository nodeRepo;

    private final IpInterfaceMapper mapper;

    public IpInterfaceDTO saveIpInterface(IpInterfaceDTO dto) {
        IpInterface model = mapper.dtoToModel(dto);

        Node node = nodeRepo.getReferenceById(dto.getNodeId());
        model.setNode(node);

        IpInterface ret = modelRepo.save(model);
        return mapper.modelToDTO(ret);
    }

    public List<IpInterfaceDTO> findAllIpInterfaces() {
        List<IpInterface> all = modelRepo.findAll();
        return all
            .stream()
            .map(mapper::modelToDTO)
            .collect(Collectors.toList());
    }

    public Optional<IpInterfaceDTO> findIpInterface(long id) {
        Optional<IpInterface> model = modelRepo.findById(id);
        Optional<IpInterfaceDTO> dto = Optional.empty();
        if (model.isPresent()) {
            dto = Optional.of(mapper.modelToDTO(model.get()));
        }
        return dto;
    }

    public List<IpInterfaceDTO> findByTenantId(String tenantId) {
        List<IpInterface> all = modelRepo.findByTenantId(tenantId);
        return all
            .stream()
            .map(mapper::modelToDTO)
            .collect(Collectors.toList());
    }
}
