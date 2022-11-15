package org.opennms.horizon.inventory.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.opennms.horizon.inventory.dto.MonitoredServiceDTO;
import org.opennms.horizon.inventory.mapper.MonitoredServiceMapper;
import org.opennms.horizon.inventory.model.IpInterface;
import org.opennms.horizon.inventory.model.MonitoredService;
import org.opennms.horizon.inventory.model.MonitoredServiceType;
import org.opennms.horizon.inventory.repository.IpInterfaceRepository;
import org.opennms.horizon.inventory.repository.MonitoredServiceRepository;
import org.opennms.horizon.inventory.repository.MonitoredServiceTypeRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MonitoredServiceService {
    private final MonitoredServiceRepository modelRepo;
    private final MonitoredServiceTypeRepository monitoredServiceTypeRepository;
    private final IpInterfaceRepository ipInterfaceRepository;

    private final MonitoredServiceMapper mapper;

    public MonitoredServiceDTO saveMonitoredService(MonitoredServiceDTO dto) {
        MonitoredService model = mapper.dtoToModel(dto);

        IpInterface ipInterface = ipInterfaceRepository.getReferenceById(dto.getIpInterfaceId());
        model.setIpInterface(ipInterface);

        MonitoredServiceType monitoredServiceType = monitoredServiceTypeRepository.getReferenceById((dto.getMonitoredServiceTypeId()));
        model.setMonitoredServiceType(monitoredServiceType);

        MonitoredService ret = modelRepo.save(model);
        return mapper.modelToDTO(ret);
    }

    public List<MonitoredServiceDTO> findAllMonitoredServices() {
        List<MonitoredService> all = modelRepo.findAll();
        return all
            .stream()
            .map(mapper::modelToDTO)
            .collect(Collectors.toList());
    }

    public Optional<MonitoredServiceDTO> findMonitoredService(long id) {
        Optional<MonitoredService> model = modelRepo.findById(id);
        Optional<MonitoredServiceDTO> dto = Optional.empty();
        if (model.isPresent()) {
            dto = Optional.of(mapper.modelToDTO(model.get()));
        }
        return dto;
    }

    public List<MonitoredServiceDTO> findByTenantId(String tenantId) {
        List<MonitoredService> all = modelRepo.findByTenantId(tenantId);
        return all
            .stream()
            .map(mapper::modelToDTO)
            .collect(Collectors.toList());
    }
}
