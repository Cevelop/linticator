package com.linticator.documentation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;

import com.linticator.base.ParserUtil;

public class DocumentationFromMsgTxt implements IDocumentation {
	private static final Pattern INDENTATION = Pattern.compile("^(\\d+\\s+).*");
	private final Map<Integer, String> entries = new HashMap<Integer, String>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.linticator.documentation.IDocumentation#getEntry(int)
	 */
	@Override
	public String getEntry(final int code) throws EntryNotFoundException {
		if (entries.keySet().contains(code))
			return entries.get(code);
		throw new EntryNotFoundException(code);
	}

	private String getReformatedEntry(final int code) throws EntryNotFoundException {
		final String entry = getEntry(code);
		final String[] lines = entry.split("\n"); //$NON-NLS-1$
		final StringBuilder formatedEntry = new StringBuilder();
		for (int i = 0; i < lines.length; ++i) {
			formatedEntry.append(lines[i]);
			if (startsWithWordChar(lines[i]) && i < (lines.length - 1) && !isEmptyOrStartsWithWhiteSpace(lines[i + 1]))
				formatedEntry.append(" "); //$NON-NLS-1$
			else
				formatedEntry.append("\n"); //$NON-NLS-1$
		}
		return formatedEntry.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.linticator.documentation.IDocumentation#documentationForMarker(int, org.eclipse.core.resources.IMarker,
	 * java.lang.String)
	 */
	@Override
	public String documentationForMarker(final int code, final IMarker marker, final String separator) throws EntryNotFoundException {

		String msg;
		final String doc = getReformatedEntry(code);

		if (marker == null)
			return code + ": " + doc.replaceFirst("-- ", separator);

		try {
			msg = (String) marker.getAttribute(IMarker.MESSAGE);
		} catch (final CoreException e2) {
			return doc.replaceFirst("-- ", separator);
		}

		return doc.replaceFirst(".*-- ", Matcher.quoteReplacement(msg + separator));
	}

	private boolean startsWithWordChar(final String string) {
		return string.matches("\\w.*"); //$NON-NLS-1$
	}

	private boolean isEmptyOrStartsWithWhiteSpace(final String string) {
		if (string.isEmpty())
			return true;
		return string.matches("\\s.*"); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.linticator.documentation.IDocumentation#parse(java.io.InputStream)
	 */
	@Override
	public void parse(final InputStream is) throws IOException {
		final BufferedReader reader = new BufferedReader(new InputStreamReader(is, "ISO-8859-1"));
		StringBuilder currentEntry = null;
		String currentLine;
		while ((currentLine = reader.readLine()) != null) {
			if (INDENTATION.matcher(currentLine).matches()) {
				if (currentEntry != null) {
					createNewDocumentationEntry(currentEntry.toString());
				}
				currentEntry = new StringBuilder(currentLine);
				currentEntry.append(ParserUtil.SYSTEM_NEWLINE);
			} else if (currentEntry != null && (currentLine.matches("^\\s.*") || currentLine.isEmpty())) {
				currentEntry.append(currentLine);
				currentEntry.append(ParserUtil.SYSTEM_NEWLINE);
			} else {
				// skip
			}
		}
		if (currentEntry != null) {
			createNewDocumentationEntry(currentEntry.toString());
		}
	}

	private void createNewDocumentationEntry(final String s) {
		final Matcher matcher = Pattern.compile("(?m)^(\\d+)").matcher(s);
		if (matcher.find()) {
			final int code = ParserUtil.parseIntOrZero(matcher.group(1));
			final String entry = eraseCodeColumn(s);
			entries.put(code, entry);
		}
	}

	private String eraseCodeColumn(final String message) {
		final String lineSeperator = ParserUtil.SYSTEM_NEWLINE;
		final int indentation = getIndentation(message);
		final String[] lines = message.split(lineSeperator);
		final StringBuilder sb = new StringBuilder();
		for (final String line : lines) {
			if (line.length() > indentation)
				sb.append(line.substring(indentation));
			sb.append(lineSeperator);
		}
		sb.delete(sb.length() - lineSeperator.length(), sb.length());
		return sb.toString();
	}

	private int getIndentation(final String message) {
		final Matcher matcher = INDENTATION.matcher(message);
		matcher.find();
		return matcher.group(1).length();
	}
}
