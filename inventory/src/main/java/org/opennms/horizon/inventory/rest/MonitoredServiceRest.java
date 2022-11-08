package org.opennms.horizon.inventory.rest;

import lombok.RequiredArgsConstructor;
import org.opennms.horizon.inventory.dto.MonitoredServiceDTO;
import org.opennms.horizon.inventory.service.MonitoredServiceService;
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
@RequestMapping("/inventory/services")
@RequiredArgsConstructor
public class MonitoredServiceRest {
    private final MonitoredServiceService monitoredServiceService;

    @PostMapping
    public ResponseEntity<MonitoredServiceDTO> postMonitoredService(@RequestBody MonitoredServiceDTO dto) {
        Optional<MonitoredServiceDTO> found = monitoredServiceService.findMonitoredService(dto.getId());
        if (found.isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            MonitoredServiceDTO saved = monitoredServiceService.saveMonitoredService(dto);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        }
    }

    @PutMapping
    public ResponseEntity<MonitoredServiceDTO> putMonitoredService(@RequestBody MonitoredServiceDTO data) {
        Optional<MonitoredServiceDTO> found = monitoredServiceService.findMonitoredService(data.getId());
        if (found.isPresent()) {
            MonitoredServiceDTO saved = monitoredServiceService.saveMonitoredService(data);
            return new ResponseEntity<>(saved, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<MonitoredServiceDTO>> getMonitoredService() {
        List<MonitoredServiceDTO> all = monitoredServiceService.findAllMonitoredServices();
        return new ResponseEntity<>(all, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MonitoredServiceDTO> getMonitoredService(@PathVariable long id) {
        Optional<MonitoredServiceDTO> found = monitoredServiceService.findMonitoredService(id);
        if (found.isPresent()) {
            return new ResponseEntity<>(found.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
