package org.opennms.horizon.inventory.rest;

import lombok.RequiredArgsConstructor;
import org.opennms.horizon.inventory.dto.MonitoredServiceTypeDTO;
import org.opennms.horizon.inventory.dto.MonitoredServiceTypeDTO;
import org.opennms.horizon.inventory.service.MonitoredServiceTypeService;
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
@RequestMapping("/inventory/serviceTypes")
@RequiredArgsConstructor
public class MonitoredServiceTypeRest {
    private final MonitoredServiceTypeService monitoredServiceTypeService;

    @PostMapping
    public ResponseEntity<MonitoredServiceTypeDTO> postMonitoredServiceType(@RequestBody MonitoredServiceTypeDTO dto) throws Exception {
        Optional<MonitoredServiceTypeDTO> ml = monitoredServiceTypeService.findMonitoredServiceType(dto.getId());
        if (ml.isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            MonitoredServiceTypeDTO saved = monitoredServiceTypeService.saveMonitoredServiceType(dto);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        }
    }

    @PutMapping
    public ResponseEntity<MonitoredServiceTypeDTO> putMonitoredServiceType(@RequestBody MonitoredServiceTypeDTO data) throws Exception {
        Optional<MonitoredServiceTypeDTO> ml = monitoredServiceTypeService.findMonitoredServiceType(data.getId());
        if (ml.isPresent()) {
            MonitoredServiceTypeDTO saved = monitoredServiceTypeService.saveMonitoredServiceType(data);
            return new ResponseEntity<>(saved, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<MonitoredServiceTypeDTO>> getMonitoredServiceType() throws Exception {
        List<MonitoredServiceTypeDTO> all = monitoredServiceTypeService.findAllMonitoredServiceTypes();
        return new ResponseEntity<>(all, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<MonitoredServiceTypeDTO> getMonitoredServiceType(@PathVariable long id) throws Exception {
        Optional<MonitoredServiceTypeDTO> ml = monitoredServiceTypeService.findMonitoredServiceType(id);
        if (ml.isPresent()) {
            return new ResponseEntity<>(ml.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
