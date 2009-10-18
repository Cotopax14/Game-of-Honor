package com.ospgames.goh.server.framework.errorhandling;

/**
 * Base class for all unchecked exceptions within the project.
 */
public class GohRuntimeException extends RuntimeException {

	public GohRuntimeException() {
		super();
	}

	public GohRuntimeException(String arg0) {
		super(arg0);
	}

	public GohRuntimeException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public GohRuntimeException(Throwable arg0) {
		super(arg0);
	}

}
