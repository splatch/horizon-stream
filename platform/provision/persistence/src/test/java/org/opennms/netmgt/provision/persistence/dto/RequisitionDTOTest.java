package org.opennms.netmgt.provision.persistence.dto;

import static org.junit.Assert.*;

import com.google.gson.Gson;
import java.net.InetAddress;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.opennms.horizon.core.lib.InetAddressUtils;

@Slf4j
public class RequisitionDTOTest {

    @Test
    public void marshalDummyObject() {
        RequisitionDTO requisitionDTO = new RequisitionDTO("blahId");
        requisitionDTO.setDateStamp(new Date());
        requisitionDTO.setLastImport(new Date());

        // Create a Node
        RequisitionNodeDTO requisitionNodeDTO = new RequisitionNodeDTO("blahForegnId", "blahLocation", "blahBuilding", "blahCity", "blahNodeLabel");
        requisitionNodeDTO.setParentForeignId("blahParentForeignId");
        requisitionNodeDTO.setParentForeignSource("blahParentForeignSource");

        for (int i=0;i<5; i++) {
             RequisitionInterfaceDTO interfaceDTO = new RequisitionInterfaceDTO();
             interfaceDTO.setIpAddr(String.format("192.168.1.%1d", i));
             requisitionNodeDTO.putInterface(interfaceDTO);
        }
        requisitionDTO.putNode(requisitionNodeDTO);

        log.info(new Gson().toJson(requisitionDTO));
    }

}