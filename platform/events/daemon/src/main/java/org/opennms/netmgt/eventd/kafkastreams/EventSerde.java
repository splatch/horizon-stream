package org.opennms.netmgt.eventd.kafkastreams;

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import org.opennms.horizon.db.model.mapper.EventProtobufMapper;
import org.opennms.horizon.events.protobuf.OpennmsEventModelProtos;
import org.opennms.horizon.events.xml.Event;
import org.slf4j.LoggerFactory;

public class EventSerde implements Serde<Event> {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(EventSerde.class);

    private final EventProtobufMapper eventProtobufMapper;

    public EventSerde(EventProtobufMapper eventProtobufMapper) {
        this.eventProtobufMapper = eventProtobufMapper;
    }

    @Override
    public Serializer<Event> serializer() {
        return new EventSerializer(eventProtobufMapper);
    }

    @Override
    public Deserializer<Event> deserializer() {
        return new EventDeserializer(eventProtobufMapper);
    }

    static class EventSerializer implements Serializer<Event> {
        private final EventProtobufMapper eventProtobufMapper;

        EventSerializer(EventProtobufMapper eventProtobufMapper) {
            this.eventProtobufMapper = eventProtobufMapper;
        }

        @Override
        public byte[] serialize(String topic, Event event) {
            return eventProtobufMapper.eventToEventProto(event).toByteArray();
        }
    }

    static class EventDeserializer implements Deserializer<Event> {
        private final EventProtobufMapper eventProtobufMapper;

        EventDeserializer(EventProtobufMapper eventProtobufMapper) {
            this.eventProtobufMapper = eventProtobufMapper;
        }

        @Override
        public Event deserialize(String topic, byte[] value) {
            OpennmsEventModelProtos.Event protobufEvent = null;
            try {
                protobufEvent = OpennmsEventModelProtos.Event.parseFrom(value);
            } catch (InvalidProtocolBufferException e) {
                LOG.error("Error while deserializing Event from Protobuf", e);
                throw new RuntimeException(e);
            }
            return this.eventProtobufMapper.eventProtoToEvent(protobufEvent);
        }
    }
}
