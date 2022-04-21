package org.opennms.netmgt.eventd.kafkastreams;

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import org.opennms.horizon.events.xml.Event;

// TODO: Refactor to use Protobuf
public class EventSerde implements Serde<Event> {

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
                e.printStackTrace();
            }
            return this.eventMapper.eventProtoToEvent(protobufEvent);
        }
    }
}
