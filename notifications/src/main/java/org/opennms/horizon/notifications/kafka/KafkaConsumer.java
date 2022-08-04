package org.opennms.horizon.notifications.kafka;

import org.opennms.horizon.shared.dto.event.AlarmDTO;
import org.opennms.horizon.shared.dto.event.EventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {
    private final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);

    @KafkaListener(
        topics = "${horizon.kafka.events.topic}",
        concurrency = "${horizon.kafka.events.concurrency}"
    )
    public void consume(EventDTO event) {
        log.info(String.format("Consumed Event: %s", event.getId()));
    }

    @KafkaListener(
        topics = "${horizon.kafka.alarms.topic}",
        concurrency = "${horizon.kafka.alarms.concurrency}"
    )
    public void consume(AlarmDTO alarm) {
        log.info(String.format("Consumed Alarm: %s", alarm.getId()));
    }
}
