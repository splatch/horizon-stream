package org.opennms.horizon.inventory.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.opennms.horizon.inventory.dto.IpInterfaceDTO;
import org.opennms.horizon.inventory.mapper.IPInterfaceMapper;
import org.opennms.horizon.inventory.model.IpInterface;
import org.opennms.horizon.inventory.model.Node;
import org.opennms.horizon.inventory.repository.IpInterfaceRepository;
import org.opennms.horizon.inventory.repository.NodeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IpInterfaceService {
    private final IpInterfaceRepository modelRepo;
    private final NodeRepository nodeRepo;

    private IPInterfaceMapper mapper = Mappers.getMapper(IPInterfaceMapper.class);

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
}
