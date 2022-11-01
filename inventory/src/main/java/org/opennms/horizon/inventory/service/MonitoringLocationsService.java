package org.opennms.horizon.inventory.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.opennms.horizon.inventory.dto.MonitoringLocationsDTO;
import org.opennms.horizon.inventory.mapper.MonitoringLocationsMapper;
import org.opennms.horizon.inventory.model.MonitoringLocations;
import org.opennms.horizon.inventory.repository.MonitoringLocationsRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MonitoringLocationsService {
    private final MonitoringLocationsRepository mlRepo;

    private MonitoringLocationsMapper mapper = Mappers.getMapper(MonitoringLocationsMapper.class);

    public MonitoringLocationsDTO saveMonitoringLocations(MonitoringLocationsDTO dto) {
        MonitoringLocations ml = mapper.dtoToModel(dto);
        MonitoringLocations ret;
        try {
            ret = mlRepo.save(ml);
        } catch (Exception ex) {
            throw ex;
        }
        return mapper.modelToDTO(ret);
    }

    public List<MonitoringLocationsDTO> findAllMonitoringLocations() {
        List<MonitoringLocations> all = mlRepo.findAll();
        return all
            .stream()
            .map(mapper::modelToDTO)
            .collect(Collectors.toList());
    }

    public Optional<MonitoringLocationsDTO> findMonitoringLocations(long id) {
        Optional<MonitoringLocations> ml = mlRepo.findById(id);
        Optional<MonitoringLocationsDTO> dto = Optional.empty();
        if (ml.isPresent()) {
            dto = Optional.of(mapper.modelToDTO(ml.get()));
        }
        return dto;
    }

    public void deleteAllMonitoringLocations() {
        mlRepo.deleteAll();
    }
}
