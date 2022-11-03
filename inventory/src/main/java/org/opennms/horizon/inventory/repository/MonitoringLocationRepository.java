package org.opennms.horizon.inventory.repository;

import org.opennms.horizon.inventory.model.MonitoringLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonitoringLocationRepository extends JpaRepository<MonitoringLocation, Long> {
}
