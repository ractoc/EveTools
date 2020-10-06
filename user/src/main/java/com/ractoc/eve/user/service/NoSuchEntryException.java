package com.ractoc.eve.user.service;

public class NoSuchEntryException extends ServiceException {
    private static final long serialVersionUID = 1L;

    public NoSuchEntryException(String msg) {
        super(msg);
    }
}
