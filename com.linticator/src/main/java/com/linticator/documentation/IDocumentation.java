package com.linticator.documentation;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IMarker;
import org.xml.sax.SAXException;

public interface IDocumentation {

	public static final String HTML_SEPARATOR = "<br/><br/>";
	public static final String PLAIN_SEPARATOR = "\n\n";

	String getEntry(int code) throws EntryNotFoundException;

	String documentationForMarker(int code, IMarker marker, String separator) throws EntryNotFoundException;

	void parse(InputStream is) throws IOException, SAXException, ParserConfigurationException;

}