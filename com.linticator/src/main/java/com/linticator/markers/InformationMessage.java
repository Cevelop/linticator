package com.linticator.markers;

import org.eclipse.core.resources.IMarker;

public class InformationMessage extends Message {

	public static final String MESSAGE_LEVEL = "info"; //$NON-NLS-1$
	public static final String MESSAGE_LEVEL_NOTE = "note"; //$NON-NLS-1$

	public InformationMessage(final MessageParameters params) {
		super(params);
	}
	@Override
	public String getMessageLevel() {
		return MESSAGE_LEVEL;
	}

	@Override
	protected int getSeverity() {
		return IMarker.SEVERITY_INFO;
	}

}
