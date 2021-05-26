package com.linticator.lint.parsing;

import static com.linticator.base.ParserUtil.parseIntOrZero;
import static com.linticator.base.ParserUtil.parseIntOrNeg;

public class DefaultLineParser implements LineParser {

	@Override
	public int getLineNumber(String lineString, String description) {
		return parseIntOrZero(lineString);
	}

	@Override
	public int getQuickfixLineNumber(String lineString, String description) {
		return parseIntOrNeg(lineString);
	}

}
