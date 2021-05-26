package com.linticator.view.preferences.infrastructure;

import org.eclipse.osgi.util.NLS;

public class InfrastructureMessages extends NLS {
	private static final String BUNDLE_NAME = "com.linticator.view.preferences.infrastructure.infrastructureMessages"; //$NON-NLS-1$
	public static String FieldEditorPropertyAndPreferencePage_checkbox_text;
	public static String FieldEditorPropertyAndPreferencePage_link_text;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, InfrastructureMessages.class);
	}

	private InfrastructureMessages() {
	}
}
