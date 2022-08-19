package org.opennms.miniongateway.taskset;

import org.apache.ignite.Ignite;
import org.opennms.taskset.service.api.TaskSetPublisher;
import org.opennms.taskset.service.igniteservice.impl.TaskSetIgniteMessageListener;
import org.opennms.taskset.service.impl.TaskSetPublisherImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TaskSetServiceConfig {

    @Bean
    public TaskSetPublisher taskSetService() {
        return new TaskSetPublisherImpl();
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public TaskSetIgniteMessageListener igniteTaskSetService(@Autowired Ignite ignite, @Autowired TaskSetPublisher taskSetPublisher) {
        return new TaskSetIgniteMessageListener(ignite, taskSetPublisher);
    }
}
