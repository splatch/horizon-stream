package org.opennms.horizon.notifications.kafka;

import org.opennms.horizon.shared.dto.event.AlarmDTO;
import org.opennms.horizon.shared.dto.event.EventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * This has been added for manual test purposes, triggering messages
 * on to the kafka topics when the application is deployed.
 */
@Service
public class KafkaProducer {
    private final Logger log = LoggerFactory.getLogger(KafkaProducer.class);

    @Autowired
    private KafkaTemplate<String, EventDTO> eventKafkaTemplate;

    @Autowired
    private KafkaTemplate<String, AlarmDTO> alarmKafkaTemplate;

    @Value("${horizon.kafka.events.topic}")
    private String eventTopic;

    @Value("${horizon.kafka.alarms.topic}")
    private String alarmsTopic;

    public void send(EventDTO event) {
        log.info(String.format("Event sending to topic... -> %d", event.getId()));
        eventKafkaTemplate.send(eventTopic, event);
    }

    public void send(AlarmDTO alarm) {
        log.info(String.format("Alarm sending to topic... -> %d", alarm.getId()));
        alarmKafkaTemplate.send(alarmsTopic, alarm);
    }
}
