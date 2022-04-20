package org.opennms.netmgt.eventd.kafkastreams;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import org.opennms.core.xml.JaxbUtils;
import org.opennms.horizon.events.xml.Event;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

// TODO: Refactor to use Protobuf
public class EventSerde implements Serde<Event> {

    @Override
    public Serializer<Event> serializer() {
        return new EventSerializer();
    }

    @Override
    public Deserializer<Event> deserializer() {
        return new EventDeserializer();
    }

    static class EventSerializer implements Serializer<Event> {
        @Override
        public byte[] serialize(String topic, Event event) {
            return JaxbUtils.marshal(event).getBytes(StandardCharsets.UTF_8);
        }
    }

    static class EventDeserializer implements Deserializer<Event> {
        @Override
        public Event deserialize(String topic, byte[] value) {
            return JaxbUtils.unmarshal(Event.class, new ByteArrayInputStream(value));
        }
    }
}
