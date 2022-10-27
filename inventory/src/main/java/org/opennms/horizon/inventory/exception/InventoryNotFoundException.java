package org.opennms.horizon.inventory.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class InventoryNotFoundException extends InventoryException {
    public InventoryNotFoundException() {
    }

    public InventoryNotFoundException(String message) {
        super(message);
    }

    public InventoryNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
