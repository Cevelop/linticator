package com.linticator.quickfixes;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "com.linticator.quickfixes.messages"; //$NON-NLS-1$
	public static String LinticatorQuickfix_1;
	public static String LinticatorQuickfix_2;
	public static String QuickFixProcessor_0;
	public static String QuickFixProcessor_1;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
