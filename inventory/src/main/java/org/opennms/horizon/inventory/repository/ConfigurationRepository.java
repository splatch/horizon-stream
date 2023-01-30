package org.opennms.horizon.inventory.repository;

import java.util.List;
import java.util.Optional;

import org.opennms.horizon.inventory.model.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigurationRepository extends JpaRepository<Configuration, Long> {
    List<Configuration> findByTenantId(String tenantId);

    List<Configuration> findByKey(String tenantId, String key);

    List<Configuration> findByLocation(String tenantId, String location);

    Optional<Configuration> getByKeyAndLocation(String tenantId, String key, String location);

    List<Configuration> findAll();
}
