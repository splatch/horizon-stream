package org.opennms.horizon.inventory.rest;

import lombok.RequiredArgsConstructor;
import org.opennms.horizon.inventory.dto.MonitoringSystemDTO;
import org.opennms.horizon.inventory.service.MonitoringSystemService;
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
@RequestMapping("/inventory/monitoringSystems")
@RequiredArgsConstructor
public class MonitoringSystemRest {
    private final MonitoringSystemService monitoringSystemService;

    @PostMapping
    public ResponseEntity<MonitoringSystemDTO> postMonitoringSystem(@RequestBody MonitoringSystemDTO dto) {
        Optional<MonitoringSystemDTO> found = monitoringSystemService.findMonitoringSystem(dto.getId());
        if (found.isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            MonitoringSystemDTO saved = monitoringSystemService.saveMonitoringSystem(dto);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        }
    }

    @PutMapping
    public ResponseEntity<MonitoringSystemDTO> putMonitoringSystem(@RequestBody MonitoringSystemDTO data) {
        Optional<MonitoringSystemDTO> found = monitoringSystemService.findMonitoringSystem(data.getId());
        if (found.isPresent()) {
            MonitoringSystemDTO saved = monitoringSystemService.saveMonitoringSystem(data);
            return new ResponseEntity<>(saved, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<MonitoringSystemDTO>> getMonitoringSystem() {
        List<MonitoringSystemDTO> all = monitoringSystemService.findAllMonitoringSystems();
        return new ResponseEntity<>(all, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MonitoringSystemDTO> getMonitoringSystem(@PathVariable long id) {
        Optional<MonitoringSystemDTO> found = monitoringSystemService.findMonitoringSystem(id);
        if (found.isPresent()) {
            return new ResponseEntity<>(found.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/tenant/{tenantId}")
    public ResponseEntity<List<MonitoringSystemDTO>> getByTenant(@PathVariable String tenantId) {
        List<MonitoringSystemDTO> all = monitoringSystemService.findByTenantId(tenantId);
        return new ResponseEntity<>(all, HttpStatus.OK);
    }
}
