package org.opennms.taskset.service.igniteservice.impl;

import org.apache.ignite.Ignite;
import org.opennms.taskset.service.api.TaskSetPublisher;
import org.opennms.taskset.service.model.LocatedTaskSet;

public class TaskSetIgniteMessageListener {
    private final Ignite ignite;
    private final TaskSetPublisher taskSetPublisher;

    private boolean shutdownInd = false;

    public TaskSetIgniteMessageListener(Ignite ignite, TaskSetPublisher taskSetPublisher) {
        this.ignite = ignite;
        this.taskSetPublisher = taskSetPublisher;
    }

    public void start() {
        ignite.message().localListen(TaskSetPublisher.TASK_SET_TOPIC, this::handleTaskSetMessage);
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

            taskSetPublisher.publishTaskSet(locatedTaskSet.getLocation(), locatedTaskSet.getTaskSet());
        }

        return true;
    }
}
