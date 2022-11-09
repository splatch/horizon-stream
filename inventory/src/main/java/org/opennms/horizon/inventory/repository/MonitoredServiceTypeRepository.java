package org.opennms.horizon.inventory.repository;

import org.opennms.horizon.inventory.model.MonitoredServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MonitoredServiceTypeRepository extends JpaRepository<MonitoredServiceType, Long> {
    List<MonitoredServiceType> findByTenantId(UUID tenantId);
}
