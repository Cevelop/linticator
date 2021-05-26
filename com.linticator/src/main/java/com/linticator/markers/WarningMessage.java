package com.linticator.markers;

import org.eclipse.core.resources.IMarker;

public class WarningMessage extends Message {

	public static final String MESSAGE_LEVEL = "warning"; //$NON-NLS-1$

	public WarningMessage(MessageParameters params) {
		super(params);
	}

	@Override
	public String getMessageLevel() {
		return MESSAGE_LEVEL;
	}

	@Override
	protected int getSeverity() {
		return IMarker.SEVERITY_WARNING;
	}

}
