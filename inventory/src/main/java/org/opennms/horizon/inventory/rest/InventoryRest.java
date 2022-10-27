package org.opennms.horizon.inventory.rest;

import org.opennms.horizon.inventory.model.MonitoringLocations;
import org.opennms.horizon.inventory.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// TODO: Rest controller per table?
@RestController
@RequestMapping("/inventory")
public class InventoryRest {

    @Autowired
    InventoryService inventoryService;

    @PostMapping(value = "/monitoring_locations", consumes="application/json")
    public ResponseEntity<MonitoringLocations> postMonitoringLocations(@RequestBody MonitoringLocations data) throws Exception {
        // TODO: Some check that id doesn't exist
        MonitoringLocations saved = inventoryService.saveMonitoringLocations(data);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping(value = "/monitoring_locations", consumes="application/json")
    public ResponseEntity<MonitoringLocations> putMonitoringLocations(@RequestBody MonitoringLocations data) throws Exception {
        // TODO: Some check that id already exists
        MonitoringLocations saved = inventoryService.saveMonitoringLocations(data);
        return new ResponseEntity<>(saved, HttpStatus.OK);
    }

    @GetMapping(value = "/monitoring_locations")
    public ResponseEntity<List<MonitoringLocations>> getMonitoringLocations() throws Exception {
        List<MonitoringLocations> all = inventoryService.findAllMonitoringLocations();
        return new ResponseEntity<>(all, HttpStatus.OK);
    }

    @GetMapping(value = "/monitoring_locations/{id}")
    public ResponseEntity<MonitoringLocations> getMonitoringLocations(@PathVariable long id) throws Exception {
        MonitoringLocations ml = inventoryService.findMonitoringLocations(id);
        return new ResponseEntity<>(ml, HttpStatus.OK);
    }

    @DeleteMapping(value = "/monitoring_locations")
    public ResponseEntity<String> deleteMonitoringLocations() throws Exception {
        inventoryService.deleteAllMonitoringLocations();
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }
}
