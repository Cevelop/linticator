package com.linticator.lint.parsing;

import static com.linticator.base.ParserUtil.parseIntOrZero;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Msg529LineParser extends DefaultLineParser {

	private static final Pattern MSG_PATTERN = Pattern.compile("(.*)line (\\d*).*"); //$NON-NLS-1$

	@Override
	public int getLineNumber(String lineString, String description) {
		final Matcher matcher = MSG_PATTERN.matcher(description);
		if(matcher.find()) {
			lineString = matcher.group(2);
		}
		return parseIntOrZero(lineString);
	}

}
