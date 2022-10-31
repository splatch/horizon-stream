package org.opennms.horizon.inventory.rest;

import lombok.RequiredArgsConstructor;
import org.opennms.horizon.inventory.model.MonitoringLocations;
import org.opennms.horizon.inventory.service.MonitoringLocationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/inventory/monitoring_locations")
@RequiredArgsConstructor
public class MonitoringLocationsRest {
    private final MonitoringLocationsService monitoringLocationsService;

    @PostMapping(consumes="application/json")
    public ResponseEntity<MonitoringLocations> postMonitoringLocations(@RequestBody MonitoringLocations data) throws Exception {
        Optional<MonitoringLocations> ml = monitoringLocationsService.findMonitoringLocations(data.getId());
        if (ml.isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            MonitoringLocations saved = monitoringLocationsService.saveMonitoringLocations(data);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        }
    }

    @PutMapping(consumes="application/json")
    public ResponseEntity<MonitoringLocations> putMonitoringLocations(@RequestBody MonitoringLocations data) throws Exception {
        Optional<MonitoringLocations> ml = monitoringLocationsService.findMonitoringLocations(data.getId());
        if (ml.isPresent()) {
            MonitoringLocations saved = monitoringLocationsService.saveMonitoringLocations(data);
            return new ResponseEntity<>(saved, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<MonitoringLocations>> getMonitoringLocations() throws Exception {
        List<MonitoringLocations> all = monitoringLocationsService.findAllMonitoringLocations();
        return new ResponseEntity<>(all, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<MonitoringLocations> getMonitoringLocations(@PathVariable long id) throws Exception {
        Optional<MonitoringLocations> ml = monitoringLocationsService.findMonitoringLocations(id);
        if (ml.isPresent()) {
            return new ResponseEntity<>(ml.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping
    public ResponseEntity<String> deleteMonitoringLocations() throws Exception {
        monitoringLocationsService.deleteAllMonitoringLocations();
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }
}
