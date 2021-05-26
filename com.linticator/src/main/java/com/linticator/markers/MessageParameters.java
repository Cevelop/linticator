package com.linticator.markers;

public class MessageParameters {
	public String fileName;
	public int lineNumber;
	public int quickfixLineNumber;
	public int columnNumber;
	public int messageCode;
	public String description;

	public MessageParameters(String fileName, int lineNumber, int columnNumber, int messageCode, String description) {
		this(fileName, lineNumber, lineNumber, columnNumber, messageCode, description);
	}

	public MessageParameters(String fileName, int lineNumber, int quickfixLineNumber, int columnNumber,
			int messageCode, String description) {
		this.fileName = fileName;
		this.lineNumber = lineNumber;
		this.quickfixLineNumber = quickfixLineNumber;
		this.columnNumber = columnNumber;
		this.messageCode = messageCode;
		this.description = description;
	}


}