package org.opennms.taskset.service.igniteclient.impl;

import org.apache.ignite.Ignite;
import org.opennms.taskset.model.TaskSet;
import org.opennms.taskset.service.api.TaskSetService;
import org.opennms.taskset.service.igniteclient.IgniteTaskSetClient;
import org.opennms.taskset.service.model.LocatedTaskSet;

public class IgniteTaskSetClientImpl implements IgniteTaskSetClient {
    private Ignite ignite;

//========================================
// Getters and Setters
//----------------------------------------

    public Ignite getIgnite() {
        return ignite;
    }

    public void setIgnite(Ignite ignite) {
        this.ignite = ignite;
    }


//========================================
// Ignite Task Set Client API
//----------------------------------------

    @Override
    public void publishTaskSet(String location, TaskSet taskSet) {
        LocatedTaskSet locatedTaskSet = new LocatedTaskSet(location, taskSet);

        ignite.message().send(TaskSetService.TASK_SET_TOPIC, locatedTaskSet);
    }
}
