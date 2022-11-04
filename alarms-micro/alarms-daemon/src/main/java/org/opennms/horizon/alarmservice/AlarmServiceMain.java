package org.opennms.horizon.alarmservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootConfiguration
@SpringBootApplication
public class AlarmServiceMain {

    public static void main(String[] args) {
        SpringApplication.run(Alarmd.class, args);
    }
}
