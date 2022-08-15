package org.opennms.miniongateway.taskset;

import org.apache.ignite.Ignite;
import org.opennms.taskset.service.api.TaskSetService;
import org.opennms.taskset.service.igniteservice.impl.IgniteTaskSetService;
import org.opennms.taskset.service.impl.TaskSetServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TaskSetServiceConfig {

    @Bean
    public TaskSetService taskSetService() {
        return new TaskSetServiceImpl();
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public IgniteTaskSetService igniteTaskSetService(@Autowired Ignite ignite, @Autowired TaskSetService taskSetService) {
        return new IgniteTaskSetService(ignite, taskSetService);
    }
}
