package org.opennms.horizon.alarmservice.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.ProtobufLibrary;
import org.opennms.horizon.alarmservice.api.AlarmService;
import org.opennms.horizon.events.proto.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EventRouteBuilder extends RouteBuilder {

    public static final String RECEIVE_EVENT = "RECEIVE_EVENT_ROUTE";

    @Autowired
    private AlarmService alarmService;

    @Override
    public void configure() throws Exception {                 

        from("{{event.from}}").id(RECEIVE_EVENT)
            .unmarshal()
            .protobuf(ProtobufLibrary.Jackson, Event.class)
            .process(exchange -> {
                Event e = exchange.getIn().getBody(Event.class);
                log.info("Received Event: {}", e);
                alarmService.process(e);
        });
    }
}
