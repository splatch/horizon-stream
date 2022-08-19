package org.opennms.taskset.service.api;

import org.opennms.taskset.model.TaskSet;

public interface TaskSetPublisher {
    String TASK_SET_TOPIC = "task-set.publish";

    void publishTaskSet(String location, TaskSet taskSet);
}
