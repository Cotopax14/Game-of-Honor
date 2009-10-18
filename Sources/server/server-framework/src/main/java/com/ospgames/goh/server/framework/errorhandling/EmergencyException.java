package com.ospgames.goh.server.framework.errorhandling;

/**
 * Exception denoting that an emergency happend.
 * 
 * @see Emergency
 * @author Wilko Kempa
 */
public class EmergencyException extends GohRuntimeException {
    //~ Class fields ----------------------------------------------------------------------------------------------------------

    static final long serialVersionUID = 3833742187899861049L;

    //~ Constructors ----------------------------------------------------------------------------------------------------------

    /**
     * Creates a new EmergencyException.
     */
    public EmergencyException() {
        super();
    }

    /**
     * Creates a new EmergencyException.
     * 
     * @param message The detail message
     */
    public EmergencyException(String message) {
        super(message);
    }

    /**
     * Creates a new EmergencyException.
     * 
     * @param cause The exception/error that caused the emergency
     */
    public EmergencyException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new EmergencyException.
     * 
     * @param message The detail message
     * @param cause The exception/error that caused the emergency
     */
    public EmergencyException(String message, Throwable cause) {
        super(message, cause);
    }
}