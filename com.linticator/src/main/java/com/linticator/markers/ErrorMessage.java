package com.linticator.markers;

import org.eclipse.core.resources.IMarker;

public class ErrorMessage extends Message {

	public static final String MESSAGE_LEVEL = "error"; //$NON-NLS-1$

	public ErrorMessage(MessageParameters params) {
		super(params);
	}

	@Override
	public String getMessageLevel() {
		return MESSAGE_LEVEL;
	}

	@Override
	protected int getSeverity() {
		return IMarker.SEVERITY_ERROR;
	}

}
