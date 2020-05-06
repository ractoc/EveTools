package com.ractoc.eve.user.model;

public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException(String eveState) {
        super("Initialization not finished for eve-state " + eveState);
    }

    public AccessDeniedException(String eveState, Throwable e) {
        super("Initialization not finished for eve-state " + eveState, e);
    }
}
