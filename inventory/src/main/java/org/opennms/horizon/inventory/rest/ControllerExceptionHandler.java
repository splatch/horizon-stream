package org.opennms.horizon.inventory.rest;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
@ResponseBody
public class ControllerExceptionHandler {
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public DataIntegrityViolationException dataIntegrityViolationException(DataIntegrityViolationException ex) {
        return ex;
    }
}
