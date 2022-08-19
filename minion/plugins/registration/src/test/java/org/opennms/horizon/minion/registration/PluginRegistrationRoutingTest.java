package org.opennms.horizon.minion.registration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.opennms.horizon.minion.ignite.model.workflows.PluginMetadata;
import org.opennms.horizon.minion.ignite.model.workflows.WorkflowType;
import org.opennms.horizon.minion.plugin.api.FieldConfigMeta;
import org.opennms.horizon.minion.registration.proto.PluginConfigMessage;
import org.opennms.horizon.shared.ipc.sink.api.MessageDispatcherFactory;
import org.opennms.horizon.shared.ipc.sink.api.SyncDispatcher;

@Ignore
public class PluginRegistrationRoutingTest extends CamelTestSupport {

    @Mock
    MessageDispatcherFactory messageDispatcherFactory;

    @Mock
    SyncDispatcher<PluginConfigMessage> dispatcher;

    private String uri = "direct:blah";
    private ProducerTemplate template;
    private long aggregationDelay = 10000;

    @Captor
    private ArgumentCaptor<PluginConfigMessage> pluginConfigMessageArgumentCaptor;

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

        return new PluginRegistrationRouting(uri, messageDispatcherFactory, aggregationDelay);
    }

    @Test
    public void goodDetection() throws Exception {
        doNothing().when(dispatcher).send(pluginConfigMessageArgumentCaptor.capture());
        FieldConfigMeta fieldConfigMeta = new FieldConfigMeta("blahDisplayName", "blahDeclarledName", "java.lang.String");
        template.sendBody(uri, new PluginMetadata("blah", WorkflowType.DETECTOR/*, Arrays.asList(fieldConfigMeta)*/));

        Thread.sleep(aggregationDelay + 2000);

        verify(dispatcher).send(any());
        PluginConfigMessage pluginConfigMessage = pluginConfigMessageArgumentCaptor.getValue();
        assertNotNull(pluginConfigMessage);
        assertEquals(1, pluginConfigMessage.getPluginconfigsCount());
    }

    @Test
    public void badDetection() throws Exception {

        template.sendBody(uri, null);

        Thread.sleep(aggregationDelay + 2000);

        verifyNoInteractions(dispatcher);
    }
}
