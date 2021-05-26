package com.linticator.lint.parsing;

import java.util.TreeMap;


public class LineParserFactory {

	private TreeMap<Integer, LineParser>parserMap = new TreeMap<Integer, LineParser>();
	private LineParser defaultParser = new DefaultLineParser();


	public LineParserFactory() {
		super();
		parserMap.put(529, new Msg529LineParser());
	}



	public LineParser getLineParser(int messageCode) {
		LineParser parser = parserMap.get(messageCode);
		if(parser == null)
			return defaultParser;
		return parser;
	}
}
