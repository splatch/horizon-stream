package org.opennms.horizon.inventory.repository;

import java.util.Optional;

import org.opennms.horizon.inventory.model.MonitoringLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MonitoringLocationRepository extends JpaRepository<MonitoringLocation, Long> {
    @Query("select location from MonitoringLocation location where location.location =?1")
    Optional<MonitoringLocation> findByLocation(String location);

}
