package org.opennms.horizon.inventory.service;

import lombok.RequiredArgsConstructor;
import org.opennms.horizon.inventory.dto.SnmpInterfaceDTO;
import org.opennms.horizon.inventory.mapper.SnmpInterfaceMapper;
import org.opennms.horizon.inventory.model.SnmpInterface;
import org.opennms.horizon.inventory.model.Node;
import org.opennms.horizon.inventory.repository.SnmpInterfaceRepository;
import org.opennms.horizon.inventory.repository.NodeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SnmpInterfaceService {
    private final SnmpInterfaceRepository modelRepo;
    private final NodeRepository nodeRepo;

    private final SnmpInterfaceMapper mapper;

    public SnmpInterfaceDTO saveSnmpInterface(SnmpInterfaceDTO dto) {
        SnmpInterface model = mapper.dtoToModel(dto);

        Node node = nodeRepo.getReferenceById(dto.getNodeId());
        model.setNode(node);

        SnmpInterface ret = modelRepo.save(model);
        return mapper.modelToDTO(ret);
    }

    public List<SnmpInterfaceDTO> findAllSnmpInterfaces() {
        List<SnmpInterface> all = modelRepo.findAll();
        return all
            .stream()
            .map(mapper::modelToDTO)
            .collect(Collectors.toList());
    }

    public Optional<SnmpInterfaceDTO> findSnmpInterface(long id) {
        Optional<SnmpInterface> model = modelRepo.findById(id);
        Optional<SnmpInterfaceDTO> dto = Optional.empty();
        if (model.isPresent()) {
            dto = Optional.of(mapper.modelToDTO(model.get()));
        }
        return dto;
    }
}
