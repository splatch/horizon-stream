package org.opennms.horizon.inventory.service;

import lombok.RequiredArgsConstructor;
import org.opennms.horizon.inventory.model.MonitoringLocations;
import org.opennms.horizon.inventory.repository.MonitoringLocationsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MonitoringLocationsService {
    private final MonitoringLocationsRepository mlRepo;

    public MonitoringLocations saveMonitoringLocations(MonitoringLocations ml) {
        return mlRepo.save(ml);
    }

    public List<MonitoringLocations> findAllMonitoringLocations() {
        return mlRepo.findAll();
    }

    public Optional<MonitoringLocations> findMonitoringLocations(long id) {
        return mlRepo.findById(id);
    }

    public void deleteAllMonitoringLocations() {
        mlRepo.deleteAll();
    }
}
