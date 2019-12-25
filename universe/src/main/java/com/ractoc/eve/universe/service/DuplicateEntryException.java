package com.ractoc.eve.universe.service;

public class DuplicateEntryException extends ServiceException {
	private static final long serialVersionUID = 1L;

	public DuplicateEntryException(String msg) {
		super(msg);
	}
}
