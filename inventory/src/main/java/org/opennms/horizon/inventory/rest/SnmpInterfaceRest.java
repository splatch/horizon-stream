package org.opennms.horizon.inventory.rest;

import lombok.RequiredArgsConstructor;
import org.opennms.horizon.inventory.dto.SnmpInterfaceDTO;
import org.opennms.horizon.inventory.service.SnmpInterfaceService;
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
@RequestMapping("/inventory/snmpInterfaces")
@RequiredArgsConstructor
public class SnmpInterfaceRest {
    private final SnmpInterfaceService snmpInterfaceService;

    @PostMapping
    public ResponseEntity<SnmpInterfaceDTO> postSnmpInterface(@RequestBody SnmpInterfaceDTO dto) {
        Optional<SnmpInterfaceDTO> found = snmpInterfaceService.findSnmpInterface(dto.getId());
        if (found.isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            SnmpInterfaceDTO saved = snmpInterfaceService.saveSnmpInterface(dto);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        }
    }

    @PutMapping
    public ResponseEntity<SnmpInterfaceDTO> putSnmpInterface(@RequestBody SnmpInterfaceDTO data) {
        Optional<SnmpInterfaceDTO> found = snmpInterfaceService.findSnmpInterface(data.getId());
        if (found.isPresent()) {
            SnmpInterfaceDTO saved = snmpInterfaceService.saveSnmpInterface(data);
            return new ResponseEntity<>(saved, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<SnmpInterfaceDTO>> getSnmpInterface() {
        List<SnmpInterfaceDTO> all = snmpInterfaceService.findAllSnmpInterfaces();
        return new ResponseEntity<>(all, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SnmpInterfaceDTO> getSnmpInterface(@PathVariable long id) {
        Optional<SnmpInterfaceDTO> found = snmpInterfaceService.findSnmpInterface(id);
        if (found.isPresent()) {
            return new ResponseEntity<>(found.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
