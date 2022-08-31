package org.opennms.horizon.notifications.api.dto;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class PagerDutyEventActionSerializer extends StdSerializer<PagerDutyEventAction> {
    protected PagerDutyEventActionSerializer() {
        super(PagerDutyEventAction.class);
    }

    @Override
    public void serialize(PagerDutyEventAction action, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(action.name().toLowerCase());
    }
}
