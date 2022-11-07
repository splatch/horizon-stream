package org.opennms.horizon.inventory.repository;

import org.opennms.horizon.inventory.model.SnmpInterface;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SnmpInterfaceRepository extends JpaRepository<SnmpInterface, Long> {
}
