package com.plexus.database;

/**
 * A generic exception that is thrown by the Mms client.
 */
public class DatabaseException extends Exception {
    private static final long serialVersionUID = -7323249827281485390L;

    /**
     * Creates a new DatabaseException.
     */
    public DatabaseException() {
        super();
    }

    /**
     * Creates a new DatabaseException with the specified detail message.
     *
     * @param message the detail message.
     */
    public DatabaseException(String message) {
        super(message);
    }

    /**
     * Creates a new DatabaseException with the specified cause.
     *
     * @param cause the cause.
     */
    public DatabaseException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new DatabaseException with the specified detail message and cause.
     *
     * @param message the detail message.
     * @param cause the cause.
     */
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
