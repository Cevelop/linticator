package com.linticator.base;


public abstract class ParserUtil {
	public static final String SYSTEM_NEWLINE = System.getProperty("line.separator"); //$NON-NLS-1$
	public static final String ESCAPED_LINE_SEPARATOR = escapeNewLines(SYSTEM_NEWLINE);
	public static final String FORMATTED_LINT_MESSAGE_REGEX = 
		"-#m-((.+?)-#t-(\\d+)-#t-(\\d+)-#t-)?(\\w+)-#t-(\\d+)-#t-((.|" + ESCAPED_LINE_SEPARATOR + ")+?)(-#m-|$)";

	public static String trimStringOrEmptyOnNull(final String string) {
		return string == null ? "" : string.trim(); //$NON-NLS-1$
	}

	public static String trimLintMultilineText(final String text) {
		final String[] parts = text.split(SYSTEM_NEWLINE);
		final StringBuilder result = new StringBuilder();
		for (final String part : parts) {
			result.append(part.trim());
			result.append(' ');
		}
		result.deleteCharAt(result.lastIndexOf(" ")); //$NON-NLS-1$
		return result.toString();
	}

	public static int parseIntOrZero(final String string) {
		try {
			return Integer.parseInt(string);
		} catch (final NumberFormatException e) {
			return 0;
		}
	}

	public static int parseIntOrNeg(final String string) {
		try {
			return Integer.parseInt(string);
		} catch (final NumberFormatException e) {
			return -1;
		}
	}

	@SuppressWarnings("nls")
	private static String escapeNewLines(final String str) {
		return str.replaceAll("\n", "\\\n").replaceAll("\r", "\\\r");
	}
}
