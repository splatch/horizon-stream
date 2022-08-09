package org.opennms.horizon.minion.ignite.worker.workflows;

import org.opennms.horizon.minion.plugin.api.ServiceMonitorResponse;

public interface WorkflowExecutionResultProcessor {
    /**
     * Queue the given result to be sent out.
     *
     * @param uuid
     * @param serviceMonitorResponse
     */
    void queueSendResult(String uuid, ServiceMonitorResponse serviceMonitorResponse);
}
