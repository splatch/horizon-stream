package org.opennms.horizon.inventory.repository;

import org.opennms.horizon.inventory.model.MonitoringLocations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonitoringLocationsRepository extends JpaRepository<MonitoringLocations, Long> {
}
