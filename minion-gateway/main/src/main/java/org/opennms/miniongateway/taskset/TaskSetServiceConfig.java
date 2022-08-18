package org.opennms.miniongateway.taskset;

import org.apache.ignite.Ignite;
import org.opennms.taskset.service.api.TaskSetForwarder;
import org.opennms.taskset.service.api.TaskSetPublisher;
import org.opennms.taskset.service.igniteservice.impl.TaskSetIgniteMessageListener;
import org.opennms.taskset.service.impl.TaskSetPublisherImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TaskSetServiceConfig {

    private TaskSetPublisherImpl taskSetService = new TaskSetPublisherImpl();

    @Bean("publisher")
    public TaskSetPublisher taskSetPublisher() {
        return taskSetService;
    }

    @Bean("forwarder")
    public TaskSetForwarder taskSetForwarder() {
        return taskSetService;
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public TaskSetIgniteMessageListener igniteTaskSetService(@Autowired Ignite ignite) {
        return new TaskSetIgniteMessageListener(ignite, taskSetService);
    }
}
