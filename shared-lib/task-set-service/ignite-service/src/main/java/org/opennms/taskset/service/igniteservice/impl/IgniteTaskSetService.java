package org.opennms.taskset.service.igniteservice.impl;

import org.apache.ignite.Ignite;
import org.opennms.taskset.service.api.TaskSetService;
import org.opennms.taskset.service.model.LocatedTaskSet;

public class IgniteTaskSetService {
    private final Ignite ignite;
    private final TaskSetService taskSetService;

    private boolean shutdownInd = false;

    public IgniteTaskSetService(Ignite ignite, TaskSetService taskSetService) {
        this.ignite = ignite;
        this.taskSetService = taskSetService;
    }

    public void start() {
        ignite.message().localListen(TaskSetService.TASK_SET_TOPIC, this::handleTaskSetMessage);
    }

    public void shutdown() {
        shutdownInd = true;
    }

//========================================
// Internals
//----------------------------------------

    private boolean handleTaskSetMessage(Object topic, Object payload) {
        if (shutdownInd) {
            return false;
        }

        if (payload instanceof LocatedTaskSet) {
            LocatedTaskSet locatedTaskSet = (LocatedTaskSet) payload;

            taskSetService.publishTaskSet(locatedTaskSet.getLocation(), locatedTaskSet.getTaskSet());
        }

        return true;
    }
}
