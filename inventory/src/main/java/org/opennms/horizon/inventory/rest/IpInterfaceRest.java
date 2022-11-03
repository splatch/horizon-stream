package org.opennms.horizon.inventory.rest;

import lombok.RequiredArgsConstructor;
import org.opennms.horizon.inventory.dto.IpInterfaceDTO;
import org.opennms.horizon.inventory.service.IpInterfaceService;
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
@RequestMapping("/inventory/ipInterfaces")
@RequiredArgsConstructor
public class IpInterfaceRest {
    private final IpInterfaceService ipInterfaceService;

    @PostMapping
    public ResponseEntity<IpInterfaceDTO> postIpInterface(@RequestBody IpInterfaceDTO dto) {
        Optional<IpInterfaceDTO> ml = ipInterfaceService.findIpInterface(dto.getId());
        if (ml.isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            IpInterfaceDTO saved = ipInterfaceService.saveIpInterface(dto);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        }
    }

    @PutMapping
    public ResponseEntity<IpInterfaceDTO> putIpInterface(@RequestBody IpInterfaceDTO data) {
        Optional<IpInterfaceDTO> ml = ipInterfaceService.findIpInterface(data.getId());
        if (ml.isPresent()) {
            IpInterfaceDTO saved = ipInterfaceService.saveIpInterface(data);
            return new ResponseEntity<>(saved, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<IpInterfaceDTO>> getIpInterface() {
        List<IpInterfaceDTO> all = ipInterfaceService.findAllIpInterfaces();
        return new ResponseEntity<>(all, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<IpInterfaceDTO> getIpInterface(@PathVariable long id) {
        Optional<IpInterfaceDTO> ml = ipInterfaceService.findIpInterface(id);
        if (ml.isPresent()) {
            return new ResponseEntity<>(ml.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
