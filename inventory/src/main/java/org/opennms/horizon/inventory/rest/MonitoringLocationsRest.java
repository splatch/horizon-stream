package org.opennms.horizon.inventory.rest;

import lombok.RequiredArgsConstructor;
import org.opennms.horizon.inventory.dto.MonitoringLocationsDTO;
import org.opennms.horizon.inventory.model.MonitoringLocations;
import org.opennms.horizon.inventory.service.MonitoringLocationsService;
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

    @PostMapping
    public ResponseEntity<MonitoringLocationsDTO> postMonitoringLocations(@RequestBody MonitoringLocationsDTO dto) throws Exception {
        Optional<MonitoringLocationsDTO> ml = monitoringLocationsService.findMonitoringLocations(dto.getId());
        if (ml.isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            MonitoringLocationsDTO saved = monitoringLocationsService.saveMonitoringLocations(dto);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        }
    }

    @PutMapping
    public ResponseEntity<MonitoringLocationsDTO> putMonitoringLocations(@RequestBody MonitoringLocationsDTO data) throws Exception {
        Optional<MonitoringLocationsDTO> ml = monitoringLocationsService.findMonitoringLocations(data.getId());
        if (ml.isPresent()) {
            MonitoringLocationsDTO saved = monitoringLocationsService.saveMonitoringLocations(data);
            return new ResponseEntity<>(saved, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<MonitoringLocationsDTO>> getMonitoringLocations() throws Exception {
        List<MonitoringLocationsDTO> all = monitoringLocationsService.findAllMonitoringLocations();
        return new ResponseEntity<>(all, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<MonitoringLocationsDTO> getMonitoringLocations(@PathVariable long id) throws Exception {
        Optional<MonitoringLocationsDTO> ml = monitoringLocationsService.findMonitoringLocations(id);
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
