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
    private final MonitoringLocationRepository mlRepo;

    private MonitoringLocationMapper mapper = Mappers.getMapper(MonitoringLocationMapper.class);

    public MonitoringLocationDTO saveMonitoringLocation(MonitoringLocationDTO dto) {
        MonitoringLocation ml = mapper.dtoToModel(dto);
        MonitoringLocation ret = mlRepo.save(ml);
        return mapper.modelToDTO(ret);
    }

    public List<MonitoringLocationDTO> findAllMonitoringLocations() {
        List<MonitoringLocation> all = mlRepo.findAll();
        return all
            .stream()
            .map(mapper::modelToDTO)
            .collect(Collectors.toList());
    }

    public Optional<MonitoringLocationDTO> findMonitoringLocation(long id) {
        Optional<MonitoringLocation> ml = mlRepo.findById(id);
        Optional<MonitoringLocationDTO> dto = Optional.empty();
        if (ml.isPresent()) {
            dto = Optional.of(mapper.modelToDTO(ml.get()));
        }
        return dto;
    }
}
