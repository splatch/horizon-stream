package org.opennms.netmgt.eventd.kafkastreams;

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import org.opennms.core.xml.JaxbUtils;
import org.opennms.horizon.events.xml.Event;

import java.io.ByteArrayInputStream;

// TODO: Refactor to use Protobuf
public class EventSerde implements Serde<Event> {

    private final ProtobufMapper protobufMapper;

    public EventSerde(ProtobufMapper protobufMapper) {
        this.protobufMapper = protobufMapper;
    }

    @Override
    public Serializer<Event> serializer() {
        return new EventSerializer(protobufMapper);
    }

    @Override
    public Deserializer<Event> deserializer() {
        return new EventDeserializer(protobufMapper);
    }

    static class EventSerializer implements Serializer<Event> {
        private final ProtobufMapper protobufMapper;

        EventSerializer(ProtobufMapper protobufMapper) {
            this.protobufMapper = protobufMapper;
        }

        @Override
        public byte[] serialize(String topic, Event event) {
            return protobufMapper.toEvent(event).build().toByteArray();
        }
    }

    static class EventDeserializer implements Deserializer<Event> {
        private final ProtobufMapper protobufMapper;

        EventDeserializer(ProtobufMapper protobufMapper) {
            this.protobufMapper = protobufMapper;
        }

        @Override
        public Event deserialize(String topic, byte[] value) {
            OpennmsModelProtos.Event protobufEvent = null;
            try {
                protobufEvent = OpennmsModelProtos.Event.parseFrom(value);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
            return this.protobufMapper.toEvent(protobufEvent);
        }
    }
}
