package com.ractoc.eve.fleetmanager.handler;

public class HandlerException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public HandlerException(String msg) {
        super(msg);
    }

    public HandlerException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
