package org.opennms.netmgt.provision.service;

public interface Provisioner {
    void publishRequisition(final String requisition) throws Exception;
}
