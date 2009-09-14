package com.ospgames.goh.server.framework.errorhandling;

/**
 * Base class for all checked exceptions within the project.
 */
public class GohException extends Exception {

	public GohException() {
		super();
	}

	public GohException(String arg0) {
		super(arg0);
	}

	public GohException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public GohException(Throwable arg0) {
		super(arg0);
	}

}
