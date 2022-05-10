package org.opennms.netmgt.provision.service;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.opennms.netmgt.provision.persistence.ProvisionRepository;

public class ProvisionerImplTest {

    Provisioner provisioner;
    String xml=null;
    @Mock
    ProvisionRepository provisionRepository;
    
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        provisioner = new ProvisionerImpl(provisionRepository);

        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("import_dummy-empty.xml");
        if (stream == null) {
            throw new FileNotFoundException("Test file does not exist.");
        }
        xml = IOUtils.toString(stream, StandardCharsets.UTF_8);
        stream.close();
    }

    @Test
    public void publishRequisition() throws Exception {

        provisioner.publishRequisition(xml);

        verify(provisionRepository).save(eq(xml));
        verifyNoMoreInteractions(provisionRepository);
    }
}