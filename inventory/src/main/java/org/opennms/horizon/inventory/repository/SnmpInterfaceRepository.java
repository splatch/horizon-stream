package org.opennms.horizon.inventory.repository;

import org.opennms.horizon.inventory.model.SnmpInterface;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SnmpInterfaceRepository extends JpaRepository<SnmpInterface, Long> {
    List<SnmpInterface> findByTenantId(UUID tenantId);
}
