package org.opennms.horizon.inventory.service;

import org.opennms.horizon.inventory.exception.ExceptionUtils;
import org.opennms.horizon.inventory.exception.InventoryException;
import org.opennms.horizon.inventory.exception.InventoryNotFoundException;
import org.opennms.horizon.inventory.model.MonitoringLocations;
import org.opennms.horizon.inventory.repository.MonitoringLocationsRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// TODO: service per table
@Service
public class InventoryService {
    MonitoringLocationsRepository mlRepo;
    ExceptionUtils exceptionUtils;

    public InventoryService(MonitoringLocationsRepository mlRepo, ExceptionUtils exceptionUtils) {
        this.mlRepo = mlRepo;
        this.exceptionUtils = exceptionUtils;
    }

    public MonitoringLocations saveMonitoringLocations(MonitoringLocations ml) throws InventoryException {
        try {
            return mlRepo.save(ml);
        } catch (DataAccessException e) {
            throw exceptionUtils.convertException("Error saving MonitorLocations", e);
        }
    }

    public List<MonitoringLocations> findAllMonitoringLocations() {
        return mlRepo.findAll();
    }

    public MonitoringLocations findMonitoringLocations(long id) throws InventoryException {
        Optional<MonitoringLocations> ml = mlRepo.findById(id);

        if (ml.isPresent()) {
            return ml.get();
        } else {
            throw new InventoryNotFoundException("MonitoringLocations not found. id="+id);
        }
    }

    public void deleteAllMonitoringLocations() {
        mlRepo.deleteAll();
    }
}
