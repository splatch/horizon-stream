package org.opennms.horizon.inventory.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.opennms.horizon.inventory.dto.MonitoringLocationDTO;
import org.opennms.horizon.inventory.mapper.MonitoringLocationMapper;
import org.opennms.horizon.inventory.model.MonitoringLocation;
import org.opennms.horizon.inventory.repository.MonitoringLocationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MonitoringLocationService {
    private final MonitoringLocationRepository modelRepo;

    private MonitoringLocationMapper mapper = Mappers.getMapper(MonitoringLocationMapper.class);

    public MonitoringLocationDTO saveMonitoringLocation(MonitoringLocationDTO dto) {
        MonitoringLocation model = mapper.dtoToModel(dto);
        MonitoringLocation ret = modelRepo.save(model);
        return mapper.modelToDTO(ret);
    }

    public List<MonitoringLocationDTO> findAllMonitoringLocations() {
        List<MonitoringLocation> all = modelRepo.findAll();
        return all
            .stream()
            .map(mapper::modelToDTO)
            .collect(Collectors.toList());
    }

    public Optional<MonitoringLocationDTO> findMonitoringLocation(long id) {
        Optional<MonitoringLocation> model = modelRepo.findById(id);
        Optional<MonitoringLocationDTO> dto = Optional.empty();
        if (model.isPresent()) {
            dto = Optional.of(mapper.modelToDTO(model.get()));
        }
        return dto;
    }
}
