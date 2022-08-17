package org.opennms.horizon.notifications.kafka;

import org.opennms.horizon.shared.dto.event.AlarmDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {
    private final Logger LOG = LoggerFactory.getLogger(KafkaConsumer.class);

    @KafkaListener(
        topics = "${horizon.kafka.alarms.topic}",
        concurrency = "${horizon.kafka.alarms.concurrency}"
    )
    public void consume(AlarmDTO alarm) {
        LOG.info("Consumed Alarm: {}", alarm.getId());
    }
}
