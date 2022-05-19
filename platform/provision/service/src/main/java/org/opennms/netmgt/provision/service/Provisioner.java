package org.opennms.netmgt.provision.service;

public interface Provisioner {
    String publishRequisition(final String requisition) throws Exception;
}
