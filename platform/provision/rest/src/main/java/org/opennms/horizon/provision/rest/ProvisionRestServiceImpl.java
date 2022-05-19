package org.opennms.horizon.provision.rest;

import javax.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import org.opennms.netmgt.provision.service.Provisioner;

@AllArgsConstructor
public class ProvisionRestServiceImpl implements ProvisionRestService {
    Provisioner provisioner;

    @Override
    public Response publishRequisition(String requisition) {
        try {
            String id = provisioner.publishRequisition(requisition);
            return Response.ok().entity(id).build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
