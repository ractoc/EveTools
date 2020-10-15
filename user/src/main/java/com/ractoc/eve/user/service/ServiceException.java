package com.ractoc.eve.user.service;

public class ServiceException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ServiceException(String msg) {
        super(msg);
    }

    ServiceException(String msg, Throwable cause) {
        super(msg, cause);
    }
}