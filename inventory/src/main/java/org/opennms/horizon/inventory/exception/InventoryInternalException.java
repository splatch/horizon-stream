package org.opennms.horizon.inventory.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InventoryInternalException extends InventoryException {
    public InventoryInternalException() {
    }

    public InventoryInternalException(String message, Throwable cause) {
        super(message, cause);
    }
}
