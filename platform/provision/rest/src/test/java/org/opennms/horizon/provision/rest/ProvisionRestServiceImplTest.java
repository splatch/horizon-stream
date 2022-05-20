package org.opennms.horizon.provision.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.google.gson.Gson;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Supplier;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.apache.commons.io.IOUtils;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.opennms.horizon.db.dao.api.SessionUtils;
import org.opennms.netmgt.provision.persistence.dto.RequisitionDTO;
import org.opennms.netmgt.provision.service.Provisioner;

public class ProvisionRestServiceImplTest {
    @Mock
    Provisioner provisioner;

    SessionUtils sessionUtils;

    ProvisionRestService provisionRestService;
    String requisitionJsonStr;
    RequisitionDTO requisitionDTO;
    Gson gson = new Gson();

    @org.junit.Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        sessionUtils = new MockSessionUtils();
        provisionRestService = new ProvisionRestServiceImpl(provisioner, sessionUtils);

        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("import_dummy-empty.json");
        if (stream == null) {
            throw new FileNotFoundException("Test file does not exist.");
        }
        requisitionJsonStr = IOUtils.toString(stream, StandardCharsets.UTF_8);
        requisitionDTO = gson.fromJson(requisitionJsonStr, RequisitionDTO.class);
    }

    @org.junit.Test
    public void publishRequisition() throws Exception{
        when(provisioner.publishRequisition(any())).thenReturn(Optional.of("blahId"));

        Response response = provisionRestService.publishRequisition(requisitionJsonStr);

        assertEquals(Status.OK.getStatusCode(), response.getStatus());
        verify(provisioner).publishRequisition(any());
        verifyNoMoreInteractions(provisioner);
    }

    @org.junit.Test
    public void getRequisition() {
        when(provisioner.read(anyString())).thenReturn(Optional.of(requisitionDTO));
        Response response = provisionRestService.getRequisition("blahId");
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        verify(provisioner).read(anyString());
        verifyNoMoreInteractions(provisioner);
    }

    @org.junit.Test
    public void deleteRequisition() {
        Response response = provisionRestService.deleteRequisition("blahId");
        assertEquals(response.getStatus(), Status.OK.getStatusCode());
        verify(provisioner).delete(anyString());
        verifyNoMoreInteractions(provisioner);
    }

    public class MockSessionUtils implements SessionUtils {
        @Override
        public <V> V withTransaction(Supplier<V> supplier) {
            return supplier.get();
        }

        @Override
        public <V> V withReadOnlyTransaction(Supplier<V> supplier) {
            return supplier.get();
        }
    }
}