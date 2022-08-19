package org.opennms.horizon.core.monitor.taskset;

import org.opennms.taskset.model.TaskDefinition;
import org.opennms.taskset.model.TaskSet;
import org.opennms.taskset.model.TaskType;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class TaskSetManager {

    private MonitorTaskSetIdentityUtil monitorTaskSetIdentityUtil = new MonitorTaskSetIdentityUtil();

    private Map<String, TaskDefinition> tasksById = new HashMap<>();
    private TaskSet taskSet;

    public TaskSetManager() {
        this.taskSet = new TaskSet();
        taskSet.setTaskDefinitionList(tasksById.values());
    }

    public TaskSet getTaskSet() {
        return taskSet;
    }

    public void addIpTask(InetAddress inetAddress, String name, TaskType taskType, String pluginName, String schedule, Map<String, String> parameters) {
        String taskId = monitorTaskSetIdentityUtil.identityForIpTask(inetAddress.getHostAddress(), name);

        TaskDefinition taskDefinition = new TaskDefinition();
        taskDefinition.setType(taskType);
        taskDefinition.setPluginName(pluginName);
        taskDefinition.setSchedule(schedule);
        taskDefinition.setId(taskId);
        taskDefinition.setParameters(parameters);

        tasksById.put(taskId, taskDefinition);
    }
}
