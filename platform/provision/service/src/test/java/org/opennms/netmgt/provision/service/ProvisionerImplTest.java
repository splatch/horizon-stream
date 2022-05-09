package org.opennms.netmgt.provision.service;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ProvisionerImplTest {

    Provisioner provisioner;

    @Before
    public void setUp() throws Exception {
        provisioner = new ProvisionerImpl();
    }

    @Test(expected = NumberFormatException.class)
    public void publishRequisition() throws Exception {
        provisioner.publishRequisition("blah");
    }
}