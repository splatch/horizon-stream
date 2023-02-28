package org.opennms.horizon.inventory.repository;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opennms.horizon.inventory.SpringContextTestInitializer;
import org.opennms.horizon.inventory.model.ActiveDiscoveryConfig;
import org.opennms.horizon.inventory.model.DiscoveryConfig;
import org.opennms.horizon.shared.constants.GrpcConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import io.grpc.Context;


@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = {SpringContextTestInitializer.class})
public class ActiveDiscoveryConfigRepositoryTest {

    private static final String tenantId = "tenant-1";

    @Autowired
    private ActiveDiscoveryConfigRepository repository;

    @Test
    public void testActiveDiscoveryConfigPersistence() {
        Context.current().withValue(GrpcConstants.TENANT_ID_CONTEXT_KEY, tenantId).run(() ->
        {
            var discoveryConfig = new DiscoveryConfig();
            discoveryConfig.setSnmpPorts(Collections.singletonList(1161));
            discoveryConfig.setCommunityString("OpenNMS");
            discoveryConfig.setIpAddresses(Arrays.asList("127.0.0.1", "127.0.0.2"));
            var activeDiscoveryConfig = new ActiveDiscoveryConfig();
            activeDiscoveryConfig.setLocation("MINION");
            activeDiscoveryConfig.setProfileName("Profile-1");
            activeDiscoveryConfig.setTenantId(tenantId);
            activeDiscoveryConfig.setDiscoveryConfig(discoveryConfig);
            var persisted = repository.save(activeDiscoveryConfig);
            Assertions.assertNotNull(persisted);
            Assertions.assertEquals("MINION", activeDiscoveryConfig.getLocation());
            Assertions.assertEquals("Profile-1", activeDiscoveryConfig.getProfileName());
            Assertions.assertEquals("OpenNMS", activeDiscoveryConfig.getDiscoveryConfig().getCommunityString());
            Assertions.assertEquals("127.0.0.1", activeDiscoveryConfig.getDiscoveryConfig().getIpAddresses().get(0));
            Assertions.assertEquals("127.0.0.2", activeDiscoveryConfig.getDiscoveryConfig().getIpAddresses().get(1));
            Assertions.assertEquals(1161, activeDiscoveryConfig.getDiscoveryConfig().getSnmpPorts().get(0));

            var optional = repository.findByLocation("MINION");
            Assertions.assertTrue(optional.isPresent());

            optional = repository.findByLocationAndProfileName("MINION", "Profile-1");
            Assertions.assertTrue(optional.isPresent());
        });

    }

    @AfterEach
    public void destroy() {
        Context.current().withValue(GrpcConstants.TENANT_ID_CONTEXT_KEY, tenantId).run(() ->
            repository.deleteAll());
    }

}
