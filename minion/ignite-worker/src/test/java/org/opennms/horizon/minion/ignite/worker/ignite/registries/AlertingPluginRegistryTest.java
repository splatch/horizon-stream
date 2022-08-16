package org.opennms.horizon.minion.ignite.worker.ignite.registries;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.savoirtech.eos.util.ServiceProperties;
import java.util.function.Consumer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.opennms.horizon.minion.ignite.model.workflows.PluginMetadata;
import org.opennms.horizon.minion.ignite.model.workflows.WorkflowType;
import org.opennms.horizon.minion.ignite.model.workflows.Workflows;
import org.opennms.horizon.minion.plugin.api.ServiceDetector;
import org.opennms.horizon.minion.plugin.api.ServiceDetectorManager;
import org.opennms.horizon.minion.plugin.api.ServiceDetectorResults;
import org.opennms.horizon.minion.plugin.api.annotations.HorizonConfig;
import org.opennms.horizon.minion.registration.RegistrationService;
import org.opennms.horizon.minion.registration.proto.Plugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class AlertingPluginRegistryTest {

    private static final String TEST_ID = "test.id";
    
    @Mock
    RegistrationService registrationService;
    @Mock
    BundleContext bundleContext;
    @Mock
    ServiceProperties serviceProperties;

    TestRegistry testRegistry;

    @Captor
    ArgumentCaptor<PluginMetadata> pluginMetadataCaptor;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);

        when(serviceProperties.getServiceId()).thenReturn(10L);
        when(serviceProperties.getProperty(anyString())).thenReturn(TEST_ID);
        doNothing().when(registrationService).notifyOfPluginRegistration(pluginMetadataCaptor.capture());

        testRegistry = new TestRegistry(bundleContext,"blah", registrationService);
    }

    @Test
    public void addService() {

        testRegistry.addService(new TestPlugin(), serviceProperties);

        verify(registrationService).notifyOfPluginRegistration(any(PluginMetadata.class));
        PluginMetadata pluginMetadata = pluginMetadataCaptor.getValue();
        assertNotNull(pluginMetadata);
        assertEquals(TEST_ID, pluginMetadata.getPluginName());
        assertEquals(WorkflowType.DETECTOR, pluginMetadata.getPluginType());
        assertEquals(1, pluginMetadata.getFieldConfigs().size());
    }

    private class TestPlugin implements ServiceDetectorManager {

        @HorizonConfig(displayName = "blah")
        public String prop;

        @Override
        public ServiceDetector create(Consumer<ServiceDetectorResults> resultProcessor) {
            return null;
        }
    }

    private class  TestRegistry extends AlertingPluginRegistry<String, ServiceDetectorManager> {

        public TestRegistry(BundleContext bundleContext, String id, RegistrationService alertingService) {
            super(bundleContext, ServiceDetectorManager.class, id, alertingService);
        }
    }
}
