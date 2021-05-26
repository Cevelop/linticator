package com.linticator.base;

import java.util.Collection;


public class StringUtil {

	/**
	 * Join several strings together using a separator.
	 */
	public static String join(final String separator, final Object... args) {
		final StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < args.length; i++) {
			final String arg = args[i].toString();
			stringBuilder.append(arg);
			if(i < args.length - 1)
				stringBuilder.append(separator);
		}
		return stringBuilder.toString();
	}

	public static String join(final String separator, final Collection<? extends Object> c) {
		return join(separator, c.toArray(new Object[] {}));
	}
}
