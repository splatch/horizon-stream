package org.opennms.horizon.provision.rest;

import javax.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opennms.horizon.db.dao.api.SessionUtils;
import org.opennms.netmgt.provision.service.Provisioner;

@AllArgsConstructor
@Slf4j
public class ProvisionRestServiceImpl implements ProvisionRestService {
    private final Provisioner provisioner;
    private SessionUtils sessionUtils;

    @Override
    public Response publishRequisition(String requisition) {
        return this.sessionUtils.withTransaction(() -> {
            try {
                String id = provisioner.publishRequisition(requisition);
                return Response.ok().entity(id).build();
            } catch (Exception e) {
                return Response.serverError().build();
//                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public Response getRequisition(String requisitionName) {
        return this.sessionUtils.withReadOnlyTransaction(() -> {
            String data = provisioner.read(requisitionName);
            log.info("Found the req {}", data);
            return Response.ok().entity(provisioner.read(requisitionName)).build();
        });
    }
}
