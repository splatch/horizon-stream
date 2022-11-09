package org.opennms.horizon.inventory.repository;

import org.opennms.horizon.inventory.model.IpInterface;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IpInterfaceRepository extends JpaRepository<IpInterface, Long> {
    List<IpInterface> findByTenantId(UUID tenantId);
}
