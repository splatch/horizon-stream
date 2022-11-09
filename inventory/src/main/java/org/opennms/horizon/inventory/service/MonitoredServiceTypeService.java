package org.opennms.horizon.inventory.service;

import lombok.RequiredArgsConstructor;
import org.opennms.horizon.inventory.dto.MonitoredServiceTypeDTO;
import org.opennms.horizon.inventory.mapper.MonitoredServiceTypeMapper;
import org.opennms.horizon.inventory.model.MonitoredServiceType;
import org.opennms.horizon.inventory.repository.MonitoredServiceTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MonitoredServiceTypeService {
    private final MonitoredServiceTypeRepository modelRepo;

    private final MonitoredServiceTypeMapper mapper;

    public MonitoredServiceTypeDTO saveMonitoredServiceType(MonitoredServiceTypeDTO dto) {
        MonitoredServiceType model = mapper.dtoToModel(dto);
        MonitoredServiceType ret = modelRepo.save(model);
        return mapper.modelToDTO(ret);
    }

    public List<MonitoredServiceTypeDTO> findAllMonitoredServiceTypes() {
        List<MonitoredServiceType> all = modelRepo.findAll();
        return all
            .stream()
            .map(mapper::modelToDTO)
            .collect(Collectors.toList());
    }

    public Optional<MonitoredServiceTypeDTO> findMonitoredServiceType(long id) {
        Optional<MonitoredServiceType> model = modelRepo.findById(id);
        Optional<MonitoredServiceTypeDTO> dto = Optional.empty();
        if (model.isPresent()) {
            dto = Optional.of(mapper.modelToDTO(model.get()));
        }
        return dto;
    }

    public List<MonitoredServiceTypeDTO> findByTenantId(String tenantId) {
        UUID tenantUUID = UUID.fromString(tenantId);
        List<MonitoredServiceType> all = modelRepo.findByTenantId(tenantUUID);
        return all
            .stream()
            .map(mapper::modelToDTO)
            .collect(Collectors.toList());
    }
}
