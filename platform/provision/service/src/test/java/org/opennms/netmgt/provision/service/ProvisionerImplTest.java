package org.opennms.netmgt.provision.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.google.gson.Gson;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.opennms.netmgt.provision.persistence.dao.RequisitionRepository;
import org.opennms.netmgt.provision.persistence.dto.RequisitionDTO;

public class ProvisionerImplTest {

    Provisioner provisioner;
    String requisitionJsonStr = null;
    @Mock
    RequisitionRepository requisitionRepository;

    Gson gson = new Gson();
    RequisitionDTO requisitionDTO;
    
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        provisioner = new ProvisionerImpl(requisitionRepository);

        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("import_dummy-empty.json");
        if (stream == null) {
            throw new FileNotFoundException("Test file does not exist.");
        }
        requisitionJsonStr = IOUtils.toString(stream, StandardCharsets.UTF_8);
        requisitionDTO = gson.fromJson(requisitionJsonStr, RequisitionDTO.class);
        stream.close();
    }

    @Test
    public void publishRequisition() throws Exception {

        provisioner.publish(requisitionDTO);

        verify(requisitionRepository).save(isA(RequisitionDTO.class));
        verifyNoMoreInteractions(requisitionRepository);
    }

    @Test
    public void readRequisition() throws Exception {

        provisioner.read("blahId");

        verify(requisitionRepository).read(eq("blahId"));
        verifyNoMoreInteractions(requisitionRepository);
    }

    @Test
    public void updateRequisition() throws Exception {

        provisioner.update(requisitionDTO);

        verify(requisitionRepository).update(isA(RequisitionDTO.class));
        verifyNoMoreInteractions(requisitionRepository);
    }

    @Test
    public void deleteRequisition() throws Exception {

        provisioner.delete("blahId");

        verify(requisitionRepository).delete(eq("blahId"));
        verifyNoMoreInteractions(requisitionRepository);
    }
}