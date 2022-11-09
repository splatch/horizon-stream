package org.opennms.horizon.inventory.repository;

import org.opennms.horizon.inventory.model.MonitoringLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MonitoringLocationRepository extends JpaRepository<MonitoringLocation, Long> {
    @Query("select location from MonitoringLocation location where location.location =?1")
    Optional<MonitoringLocation> findByLocation(String location);

    List<MonitoringLocation> findByTenantId(UUID tenantId);
}
