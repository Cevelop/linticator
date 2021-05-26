package com.linticator.documentation;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class DocumentationFromMsgXml implements IDocumentation {

	private final Map<Integer, String> texts = new HashMap<Integer, String>();
	private final Map<Integer, String> commentaries = new HashMap<Integer, String>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.linticator.documentation.IDocumentation#getEntry(int)
	 */
	@Override
	public String getEntry(final int code) throws EntryNotFoundException {
		if (texts.containsKey(code)) {
			return String.format("%s -- %s", texts.get(code), commentaries.get(code));
		}
		throw new EntryNotFoundException(code);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.linticator.documentation.IDocumentation#documentationForMarker(int, org.eclipse.core.resources.IMarker,
	 * java.lang.String)
	 */
	@Override
	public String documentationForMarker(final int code, final IMarker marker, final String separator) throws EntryNotFoundException {

		if (marker == null) {
			return code + ": " + texts.get(code) + separator + commentaries.get(code);
		}

		String msg;

		try {
			msg = (String) marker.getAttribute(IMarker.MESSAGE);
		} catch (final CoreException e) {
			return texts.get(code) + separator + commentaries.get(code);
		}

		// We can use the actual error message from the output in the documentation
		return msg + separator + commentaries.get(code);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.linticator.documentation.IDocumentation#parse(java.io.InputStream)
	 */
	@Override
	public void parse(final InputStream is) throws IOException, SAXException, ParserConfigurationException {

		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		saxParser.parse(is, new DefaultHandler() {

			private int currentCategory;
			private String currentText;
			private String currentCommentary;

			private boolean inText;
			private boolean inCommentary;

			@Override
			public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
				if (qName.equals("message")) {
					String idValue = attributes.getValue("id");
					currentCategory = Integer.parseInt(idValue);
				} else if (qName.equals("text")) {
					inText = true;
				} else if (qName.equals("commentary")) {
					inCommentary = true;
				}
				super.startElement(uri, localName, qName, attributes);
			}

			@Override
			public void endElement(String uri, String localName, String qName) throws SAXException {
				if (qName.equals("message")) {
					texts.put(currentCategory, currentText);
					commentaries.put(currentCategory, currentCommentary);
					currentText = "";
					currentCommentary = "";
				} else if (qName.equals("text")) {
					inText = false;
				} else if (qName.equals("commentary")) {
					inCommentary = false;
				}
				super.endElement(uri, localName, qName);
			}

			@Override
			public void characters(char[] ch, int start, int length) throws SAXException {
				if (inText) {
					currentText += new String(ch, start, length);
				}
				if (inCommentary) {
					currentCommentary += new String(ch, start, length);
				}
				super.characters(ch, start, length);
			}
		});
	}
}
