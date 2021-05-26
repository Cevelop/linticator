package com.linticator.documentation;

public class EntryNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;
	private int code;

	public EntryNotFoundException(int code) {
		this.code = code;
	}

	@Override
	public String getLocalizedMessage() {
		return "The requested documentation entry was not found: " + code; //$NON-NLS-1$
	}
}
