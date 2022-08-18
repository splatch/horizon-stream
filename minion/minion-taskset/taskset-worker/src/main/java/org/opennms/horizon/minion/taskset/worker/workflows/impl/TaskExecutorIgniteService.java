package org.opennms.horizon.minion.ignite.worker.workflows.impl;

import org.apache.ignite.IgniteLogger;
import org.apache.ignite.resources.LoggerResource;
import org.apache.ignite.services.Service;
import org.opennms.horizon.minion.taskset.worker.ignite.registries.OsgiServiceHolder;
import org.opennms.horizon.minion.ignite.worker.workflows.TaskExecutorLocalService;
import org.opennms.horizon.minion.ignite.worker.workflows.TaskExecutorLocalServiceFactory;
import org.opennms.taskset.model.TaskDefinition;
import org.opennms.taskset.model.TaskSet;

/**
 * Ignite version of the service to execute workflows.  Uses the "local" version of the service,
 *  WorkflowExecutorLocalService, which is never serialized/deserialized, reducing the challenges that introduces.
 */
public class TaskExecutorIgniteService implements Service {

    private TaskDefinition taskDefinition;

    @LoggerResource
    private IgniteLogger logger;

    private transient TaskExecutorLocalServiceFactory workflowExecutorLocalServiceFactory;
    private transient TaskExecutorLocalService localService;
    private transient boolean shutdown;

    private transient Object sync;

    public TaskExecutorIgniteService(TaskDefinition taskDefinition) {
        this.taskDefinition = taskDefinition;
    }

//========================================
// Ignite Service API
//----------------------------------------

    @Override
    public void init() throws Exception {
        sync = new Object();
        shutdown = false;

        workflowExecutorLocalServiceFactory = OsgiServiceHolder.getWorkflowExecutorLocalServiceFactory();
    }

    @Override
    public void execute() throws Exception {
        if (shutdown) {
            logger.info("Skipping execution of workflow; appears to have been canceled already");
            return;
        }

        TaskExecutorLocalService newLocalService = workflowExecutorLocalServiceFactory.create(taskDefinition);
        synchronized (sync) {
            if (! shutdown) {
                localService = newLocalService;
                localService.start();
            } else {
                logger.info("Aborting execution of workflow; appears to have been canceled before fully started");
            }
        }
    }

    @Override
    public void cancel() {
        TaskExecutorLocalService shutdownService = null;

        synchronized (sync) {
            if (! shutdown) {
                shutdownService = localService;
            }
            shutdown = true;
            localService = null;
        }

        if (shutdownService != null) {
            shutdownService.cancel();
        }
    }
}
