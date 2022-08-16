package org.opennms.horizon.minion.registration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.opennms.horizon.ipc.sink.api.MessageDispatcherFactory;
import org.opennms.horizon.ipc.sink.api.SyncDispatcher;
import org.opennms.horizon.minion.ignite.model.workflows.PluginMetadata;
import org.opennms.horizon.minion.ignite.model.workflows.WorkflowType;
import org.opennms.horizon.minion.registration.proto.PluginConfigMessage;

public class PluginRegistrationRoutingTest extends CamelTestSupport {

    @Mock
    MessageDispatcherFactory messageDispatcherFactory;

    @Mock
    SyncDispatcher<PluginConfigMessage> dispatcher;

    private String uri = "direct:blah";

    protected ProducerTemplate template;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        super.setUp();

        template = context().createProducerTemplate();
    }

    @Override
    public RouteBuilder createRouteBuilder() throws Exception
    {
        when(messageDispatcherFactory.createSyncDispatcher(any(PluginConfigSinkModule.class))).thenReturn(dispatcher);
        return new PluginRegistrationRouting(uri, messageDispatcherFactory, 10000);
    }

    @Test
    public void configure() throws InterruptedException {
        template.sendBody(uri, new PluginMetadata("blah", WorkflowType.DETECTOR, new ArrayList<>()));

        Thread.sleep(12000);
        verify(dispatcher).send(any());
    }
}
