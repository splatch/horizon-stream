package org.opennms.horizon.inventory.rest;

import lombok.RequiredArgsConstructor;
import org.opennms.horizon.inventory.dto.MonitoringLocationDTO;
import org.opennms.horizon.inventory.service.MonitoringLocationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/inventory/locations")
@RequiredArgsConstructor
public class MonitoringLocationRest {
    private final MonitoringLocationService monitoringLocationService;

    @PostMapping
    public ResponseEntity<MonitoringLocationDTO> postMonitoringLocations(@RequestBody MonitoringLocationDTO dto) {
        Optional<MonitoringLocationDTO> ml = monitoringLocationService.findMonitoringLocation(dto.getId());
        if (ml.isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            MonitoringLocationDTO saved = monitoringLocationService.saveMonitoringLocation(dto);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        }
    }

    @PutMapping
    public ResponseEntity<MonitoringLocationDTO> putMonitoringLocations(@RequestBody MonitoringLocationDTO data) {
        Optional<MonitoringLocationDTO> ml = monitoringLocationService.findMonitoringLocation(data.getId());
        if (ml.isPresent()) {
            MonitoringLocationDTO saved = monitoringLocationService.saveMonitoringLocation(data);
            return new ResponseEntity<>(saved, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<MonitoringLocationDTO>> getMonitoringLocations() {
        List<MonitoringLocationDTO> all = monitoringLocationService.findAllMonitoringLocations();
        return new ResponseEntity<>(all, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<MonitoringLocationDTO> getMonitoringLocations(@PathVariable long id) {
        Optional<MonitoringLocationDTO> ml = monitoringLocationService.findMonitoringLocation(id);
        if (ml.isPresent()) {
            return new ResponseEntity<>(ml.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/tenant/{tenantId}")
    public ResponseEntity<List<MonitoringLocationDTO>> getByTenant(@PathVariable String tenantId) {
        List<MonitoringLocationDTO> all = monitoringLocationService.findByTenantId(tenantId);
        return new ResponseEntity<>(all, HttpStatus.OK);
    }
}
