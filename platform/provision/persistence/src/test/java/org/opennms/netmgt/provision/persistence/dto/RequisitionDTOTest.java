package org.opennms.netmgt.provision.persistence.dto;

import static org.junit.Assert.*;

import com.google.gson.Gson;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

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
        requisitionDTO.putNode(requisitionNodeDTO);
        //TODO: log4j properties file to set log level
        log.info(new Gson().toJson(requisitionDTO));
        System.out.println(new Gson().toJson(requisitionDTO));
    }

}