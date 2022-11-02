package org.opennms.horizon.inventory.rest;

import lombok.RequiredArgsConstructor;
import org.opennms.horizon.inventory.dto.MonitoringLocationDTO;
import org.opennms.horizon.inventory.service.MonitoringLocationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/inventory/monitoring_location")
@RequiredArgsConstructor
public class MonitoringLocationRest {
    private final MonitoringLocationService monitoringLocationService;

    @PostMapping
    public ResponseEntity<MonitoringLocationDTO> postMonitoringLocations(@RequestBody MonitoringLocationDTO dto) throws Exception {
        Optional<MonitoringLocationDTO> ml = monitoringLocationService.findMonitoringLocation(dto.getId());
        if (ml.isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            MonitoringLocationDTO saved = monitoringLocationService.saveMonitoringLocation(dto);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        }
    }

    @PutMapping
    public ResponseEntity<MonitoringLocationDTO> putMonitoringLocations(@RequestBody MonitoringLocationDTO data) throws Exception {
        Optional<MonitoringLocationDTO> ml = monitoringLocationService.findMonitoringLocation(data.getId());
        if (ml.isPresent()) {
            MonitoringLocationDTO saved = monitoringLocationService.saveMonitoringLocation(data);
            return new ResponseEntity<>(saved, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<MonitoringLocationDTO>> getMonitoringLocations() throws Exception {
        List<MonitoringLocationDTO> all = monitoringLocationService.findAllMonitoringLocations();
        return new ResponseEntity<>(all, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<MonitoringLocationDTO> getMonitoringLocations(@PathVariable long id) throws Exception {
        Optional<MonitoringLocationDTO> ml = monitoringLocationService.findMonitoringLocation(id);
        if (ml.isPresent()) {
            return new ResponseEntity<>(ml.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
