/*
 */
package org.opennms.netmgt.eventd.kafkastreams;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.errors.LogAndFailExceptionHandler;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.opennms.horizon.core.lib.StringUtils;
import org.opennms.horizon.events.api.EventProcessorException;
import org.opennms.horizon.events.conf.xml.LogDestType;
import org.opennms.horizon.events.xml.Event;
import org.opennms.horizon.events.xml.Events;
import org.opennms.horizon.events.xml.Header;
import org.opennms.horizon.events.xml.Log;
import org.opennms.netmgt.eventd.EventExpander;
import org.opennms.netmgt.eventd.processor.EventWriter;

import java.util.Properties;

/**
 *
 */
public final class EventdStreams {

    public static final String INTERNAL_INPUT_TOPIC = "internal-events";
    public static final String EXTERNAL_INPUT_TOPIC = "external-events";
    public static final String EXPANDED_OUTPUT_TOPIC = "expanded-events";
    public static final String PERSISTED_OUTPUT_TOPIC = "persisted-events";
    public static final String ALARM_OUTPUT_TOPIC = "alarm-events";

    private final EventExpander eventExpander;
    private final EventWriter eventWriter;
    private final EventMapper eventMapper;

    private StreamsBuilder builder;
    private KafkaStreams streams;

    public EventdStreams(EventExpander eventExpander, EventWriter eventWriter, EventMapper eventMapper) {
        this.eventExpander = eventExpander;
        this.eventWriter = eventWriter;
        this.eventMapper = eventMapper;
    }

    public void init() {
        final Properties props = getStreamsConfig();
        builder = new StreamsBuilder();

        createEventStream();

        ClassLoader currentCL = Thread.currentThread().getContextClassLoader();

        try {
            // workaround for OSGi-related classloader issue so Kafka Streams can find LogAndFailExceptionHandler
            // TODO: Find a better solution, this prevents us from configuring default serdes
            Thread.currentThread().setContextClassLoader(LogAndFailExceptionHandler.class.getClassLoader());

            streams = new KafkaStreams(builder.build(), props);
            streams.start();
        } catch (final Throwable e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            Thread.currentThread().setContextClassLoader(currentCL);
        }
    }

    public void destroy() {
        streams.close();
    }

    private Properties getStreamsConfig() {
        final Properties props = new Properties();

        props.putIfAbsent(StreamsConfig.APPLICATION_ID_CONFIG, "streams-eventd");
        props.putIfAbsent(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.putIfAbsent(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        props.putIfAbsent(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return props;
    }

    private void createEventStream() {
        final KStream<String, Event> internalSource = builder.stream(INTERNAL_INPUT_TOPIC,
                Consumed.with(Serdes.String(), new EventXmlSerde()));
        final KStream<String, Event> externalSource = builder.stream(EXTERNAL_INPUT_TOPIC,
                Consumed.with(Serdes.String(), new EventXmlSerde()));

        final KStream<String, Event> expandedEvents = internalSource
                .merge(externalSource)
                .mapValues(value -> {
                    eventExpander.expandEvent(value);
                    return value;
                });

        expandedEvents.to(EXPANDED_OUTPUT_TOPIC, Produced.with(Serdes.String(), new EventSerde(eventMapper)));

        final KStream<String, Event> persistedEvents = expandedEvents
                .filter((key, event) -> event.getLogmsg() != null)
                .filter((key, event) -> !LogDestType.DONOTPERSIST.toString().equalsIgnoreCase(event.getLogmsg().getDest()))
                .mapValues(value -> {
                    Log log = getLog(value);
                    try {
                        eventWriter.process(log);
                    } catch (EventProcessorException e) {
                        e.printStackTrace();
                    }
                    return value;
                });

        persistedEvents.to(PERSISTED_OUTPUT_TOPIC, Produced.with(Serdes.String(), new EventSerde(eventMapper)));

        persistedEvents
                .filter((key, event) -> event.getAlarmData() != null)
                .to(ALARM_OUTPUT_TOPIC, Produced.with(Serdes.String(), new EventXmlSerde()));
    }

    private Log getLog(Event event) {
        Events events = new Events();
        events.setEvent(new Event[]{event});

        Header header = new Header();
        if (event.getCreationTime() != null) {
            header.setCreated(StringUtils.toStringEfficiently(event.getCreationTime()));
        }

        Log log = new Log();
        log.setHeader(header);
        log.setEvents(events);
        return log;
    }
}
