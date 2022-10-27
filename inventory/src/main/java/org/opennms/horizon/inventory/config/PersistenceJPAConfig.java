package org.opennms.horizon.inventory.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "org.opennms.horizon.inventory.repository")
public class PersistenceJPAConfig {
}
