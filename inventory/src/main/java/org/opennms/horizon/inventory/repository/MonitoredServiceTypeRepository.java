package org.opennms.horizon.inventory.repository;

import java.util.List;

import org.opennms.horizon.inventory.model.MonitoredServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonitoredServiceTypeRepository extends JpaRepository<MonitoredServiceType, Long> {
    List<MonitoredServiceType> findByTenantId(String tenantId);
}
