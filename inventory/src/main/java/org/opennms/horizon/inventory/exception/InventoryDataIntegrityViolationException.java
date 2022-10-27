package org.opennms.horizon.inventory.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InventoryDataIntegrityViolationException extends InventoryException {
    public InventoryDataIntegrityViolationException() {
    }

    public InventoryDataIntegrityViolationException(String message, Throwable cause) {
        super(message, cause);
    }
}
