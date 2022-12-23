package org.opennms.horizon.protobuf.util;

import com.google.protobuf.InvalidProtocolBufferException;
import org.junit.Assert;
import org.junit.Test;
import org.opennms.horizon.shared.protobuf.util.ProtobufUtil;
import org.opennms.sink.flows.contract.AdapterConfig;
import org.opennms.sink.flows.contract.FlowsConfig;
import org.opennms.sink.flows.contract.ListenerConfig;
import org.opennms.sink.flows.contract.PackageConfig;
import org.opennms.sink.flows.contract.Parameter;
import org.opennms.sink.flows.contract.ParserConfig;
import org.opennms.sink.flows.contract.QueueConfig;
import org.opennms.sink.flows.contract.Rrd;

public class ProtoBufUtilTest {
    @Test
    public void testRoundTrip() throws InvalidProtocolBufferException {
        ListenerConfig listenerConfig = ListenerConfig.newBuilder()
            .setName("Netflow-5-UDP-8877")
            .setClassName("org.opennms.netmgt.telemetry.listeners.UdpListener")
            .setEnabled(true)
            .addParameters(Parameter.newBuilder().setKey("port").setValue("8877"))
            .addParsers(
                ParserConfig.newBuilder()
                    .setName("Netflow-5-Parser")
                    .setClassName("org.opennms.netmgt.telemetry.protocols.netflow.parser.Netflow5UdpParser")
                    .setQueue(
                        QueueConfig.newBuilder()
                            .setName("Netflow-5")
                            .addAdapters(AdapterConfig.newBuilder()
                                .setName("Netflow-5-Adapter")
                                .setClassName("org.opennms.netmgt.telemetry.protocols.netflow.adapter.netflow5.Netflow5Adapter")
                                .setEnabled(true)
                                .addParameters(Parameter.newBuilder().setKey("applicationDataCollection").setValue("false"))
                                .addParameters(Parameter.newBuilder().setKey("applicationThresholding").setValue("false"))
                                .addPackages(PackageConfig.newBuilder()
                                    .setName("Netflow-5-Default")
                                    .setRrd(Rrd.newBuilder()
                                        .setStep(300)
                                        .addRras("RRA:AVERAGE:0.5:1:2016")
                                        .addRras("RRA:AVERAGE:0.5:12:1488")
                                        .addRras("RRA:AVERAGE:0.5:288:366")
                                        .addRras("RRA:MAX:0.5:288:366")
                                        .addRras("RRA:MAX:0.5:288:366")
                                    )
                                )
                            )
                    )
            ).build();
        FlowsConfig flowsConfig = FlowsConfig.newBuilder().addListeners(listenerConfig).build();
        String json = ProtobufUtil.toJson(flowsConfig);
        Assert.assertTrue("Json size should not empty.", json.length() > 0);
        FlowsConfig convertedFlowsConfig = ProtobufUtil.fromJson(json, FlowsConfig.class);
        Assert.assertTrue(flowsConfig.equals(convertedFlowsConfig));
    }
}
