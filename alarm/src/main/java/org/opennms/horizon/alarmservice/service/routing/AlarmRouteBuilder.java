package org.opennms.horizon.alarmservice.service.routing;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.ProtobufLibrary;
import org.opennms.horizon.alarmservice.db.entity.Alarm;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Getter
@Setter
public class AlarmRouteBuilder extends RouteBuilder {

    public static final String NEW_ALARM_ROUTE = "NEW_ALARM_FORWARD_ROUTE";

    private String endpoint = "direct:newAlarm";

    @Override
    public void configure() throws Exception {

        from(endpoint).id(NEW_ALARM_ROUTE)
            .marshal().protobuf(ProtobufLibrary.Jackson, Alarm.class)
            .log(LoggingLevel.INFO, "Forwarding alarm to whoever cares")
            .to("{{alarm.to}}");
    }
}
