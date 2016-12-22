package com.boco.rzserver.exception;

/**
 * 
 * @author lij
 *
 */
public class RzProcessException extends Exception {

	private static final long serialVersionUID = -2249746024158678099L;

	private String message;

	public RzProcessException(String message) {
		this.message = message;
	}

	public String getMessage() {
		return this.message;
	}

}
