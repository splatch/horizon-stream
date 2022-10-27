package org.opennms.horizon.inventory.exception;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
public class ExceptionUtils {
    public InventoryException convertException(String message, DataAccessException e) {
        if (e instanceof DataIntegrityViolationException) {
            return new InventoryDataIntegrityViolationException(message, e);
        } else {
            return new InventoryInternalException(message, e);
        }
    }
}
