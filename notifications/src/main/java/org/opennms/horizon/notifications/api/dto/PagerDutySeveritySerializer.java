package org.opennms.horizon.notifications.api.dto;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class PagerDutySeveritySerializer extends StdSerializer<PagerDutySeverity> {
    protected PagerDutySeveritySerializer() {
        super(PagerDutySeverity.class);
    }

    @Override
    public void serialize(PagerDutySeverity action, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(action.name().toLowerCase());
    }
}
