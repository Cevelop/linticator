package com.linticator.lint.parsing;

public interface LineParser {
	public int getLineNumber(String lineString, String description);
	public int getQuickfixLineNumber(String lineString, String description);
}
