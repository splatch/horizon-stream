package org.opennms.horizon.inventory.rest;

import lombok.RequiredArgsConstructor;
import org.opennms.horizon.inventory.dto.NodeDTO;
import org.opennms.horizon.inventory.service.NodeService;
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
@RequestMapping("/inventory/nodes")
@RequiredArgsConstructor
public class NodeRest {
    private final NodeService nodeService;

    @PostMapping
    public ResponseEntity<NodeDTO> postNode(@RequestBody NodeDTO dto) {
        Optional<NodeDTO> ml = nodeService.findNode(dto.getId());
        if (ml.isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            NodeDTO saved = nodeService.saveNode(dto);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        }
    }

    @PutMapping
    public ResponseEntity<NodeDTO> putNode(@RequestBody NodeDTO data) {
        Optional<NodeDTO> ml = nodeService.findNode(data.getId());
        if (ml.isPresent()) {
            NodeDTO saved = nodeService.saveNode(data);
            return new ResponseEntity<>(saved, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<NodeDTO>> getNode() {
        List<NodeDTO> all = nodeService.findAllNodes();
        return new ResponseEntity<>(all, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<NodeDTO> getNode(@PathVariable long id) {
        Optional<NodeDTO> ml = nodeService.findNode(id);
        if (ml.isPresent()) {
            return new ResponseEntity<>(ml.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
