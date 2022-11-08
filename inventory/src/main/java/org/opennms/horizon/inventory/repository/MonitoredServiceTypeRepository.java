package org.opennms.horizon.inventory.repository;

import org.opennms.horizon.inventory.model.MonitoredServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonitoredServiceTypeRepository extends JpaRepository<MonitoredServiceType, Long> {
}
