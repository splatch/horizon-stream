package org.opennms.horizon.inventory.repository;

import java.util.List;

import org.opennms.horizon.inventory.model.IpInterface;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IpInterfaceRepository extends JpaRepository<IpInterface, Long> {
    List<IpInterface> findByTenantId(String tenantId);
}
