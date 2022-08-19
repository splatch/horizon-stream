package org.opennms.taskset.service.api;

import org.opennms.taskset.model.TaskSet;

public interface TaskSetListener {
    void onTaskSetUpdate(TaskSet taskSet);
}
