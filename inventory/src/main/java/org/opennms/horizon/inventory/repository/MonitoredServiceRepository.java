package org.opennms.horizon.inventory.repository;

import org.opennms.horizon.inventory.model.MonitoredService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonitoredServiceRepository extends JpaRepository<MonitoredService, Long> {
}
