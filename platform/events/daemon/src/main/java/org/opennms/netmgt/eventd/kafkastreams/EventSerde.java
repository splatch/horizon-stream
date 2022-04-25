package org.opennms.netmgt.eventd.kafkastreams;

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import org.opennms.horizon.events.xml.Event;
import org.slf4j.LoggerFactory;

public class EventSerde implements Serde<Event> {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(EventSerde.class);

    private final EventMapper eventMapper;

    public EventSerde(EventMapper eventMapper) {
        this.eventMapper = eventMapper;
    }

    @Override
    public Serializer<Event> serializer() {
        return new EventSerializer(eventMapper);
    }

    @Override
    public Deserializer<Event> deserializer() {
        return new EventDeserializer(eventMapper);
    }

    static class EventSerializer implements Serializer<Event> {
        private final EventMapper eventMapper;

        EventSerializer(EventMapper eventMapper) {
            this.eventMapper = eventMapper;
        }

        @Override
        public byte[] serialize(String topic, Event event) {
            return eventMapper.eventToEventProto(event).toByteArray();
        }
    }

    static class EventDeserializer implements Deserializer<Event> {
        private final EventMapper eventMapper;

        EventDeserializer(EventMapper eventMapper) {
            this.eventMapper = eventMapper;
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
            return this.eventMapper.eventProtoToEvent(protobufEvent);
        }
    }
}
