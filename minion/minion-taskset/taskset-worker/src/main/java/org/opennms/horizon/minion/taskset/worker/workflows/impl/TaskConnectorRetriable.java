package org.opennms.horizon.minion.ignite.worker.workflows.impl;

import java.util.HashMap;
import java.util.Map;
import org.opennms.horizon.minion.ignite.worker.workflows.RetriableExecutor;
import org.opennms.horizon.minion.ignite.worker.workflows.TaskExecutionResultProcessor;
import org.opennms.horizon.minion.taskset.worker.ignite.registries.OsgiServiceHolder;
import org.opennms.horizon.minion.plugin.api.ServiceConnector;
import org.opennms.horizon.minion.plugin.api.ServiceConnectorFactory;
import org.opennms.taskset.model.TaskDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Connector Service
 */
public class TaskConnectorRetriable implements RetriableExecutor {

    private static final Logger DEFAULT_LOGGER = LoggerFactory.getLogger(TaskConnectorRetriable.class);

    private Logger log = DEFAULT_LOGGER;

    private TaskDefinition taskDefinition;
    private TaskExecutionResultProcessor resultProcessor;
    private ServiceConnector serviceConnector;

    private Runnable onDisconnect;

    public TaskConnectorRetriable(TaskDefinition taskDefinition, TaskExecutionResultProcessor resultProcessor) {
        this.taskDefinition = taskDefinition;
        this.resultProcessor = resultProcessor;
    }

//========================================
// API
//----------------------------------------

    @Override
    public void init(Runnable handleRetryNeeded) {
        this.onDisconnect = handleRetryNeeded;
    }

    @Override
    public void attempt() throws Exception {
        ServiceConnectorFactory serviceConnectorFactory = lookupServiceConnectorFactory(taskDefinition);

        Map<String, Object> castMap = new HashMap<>(taskDefinition.getParameters());

        serviceConnector =
                serviceConnectorFactory.create(
                        result -> resultProcessor.queueSendResult(taskDefinition.getId(), result),
                        castMap,
                        onDisconnect
                );

        log.info("Attempting to connect: workflow-uuid={}", taskDefinition.getId());
        serviceConnector.connect();
    }

    @Override
    public void cancel() {
        serviceConnector.disconnect();
    }

//========================================
// Setup Internals
//----------------------------------------

    private ServiceConnectorFactory lookupServiceConnectorFactory(TaskDefinition workflow) throws Exception {
        String pluginName = workflow.getPluginName();

        ServiceConnectorFactory result = OsgiServiceHolder.getServiceConnectorFactoryRegistry().getService(pluginName);

        if (result == null) {
            log.error("Failed to locate connector factory for workflow: plugin-name={}; workflow-uuid={}",
                    pluginName, workflow.getId());
            throw new Exception("Failed to locate connector factory for workflow: plugin-name=" + pluginName);
        }

        return result;
    }
}
