package org.opennms.core.spring;

public class DataAccessResourceFailureException extends DataAccessException {
    public DataAccessResourceFailureException(String message, Throwable t) {
        super(message,t);
    }

    public DataAccessResourceFailureException(String message) {
        super(message);
    }
}
