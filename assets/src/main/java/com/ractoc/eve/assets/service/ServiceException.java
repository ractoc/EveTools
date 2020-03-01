package com.ractoc.eve.assets.service;

public class ServiceException extends RuntimeException {
	private static final long serialVersionUID = 1L;

    public ServiceException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
