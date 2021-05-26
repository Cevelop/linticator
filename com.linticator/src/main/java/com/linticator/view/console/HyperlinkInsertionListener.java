package com.linticator.view.console;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;

import com.linticator.Linticator;

final class HyperlinkInsertionListener implements IDocumentListener {

	private final AbstractLintConsole console;
	private final IPath path;
	private final String linkText;

	HyperlinkInsertionListener(final AbstractLintConsole console, final IPath path, final String linkText) {
		this.console = console;
		this.path = path;
		this.linkText = linkText;
	}

	@Override
	public void documentAboutToBeChanged(final DocumentEvent event) {
		// don't care
	}

	@Override
	public void documentChanged(final DocumentEvent event) {

		if (linkTextFound(event)) {
			try {
				addLinkToConsole(event);
			} catch (final BadLocationException e) {
				Linticator.getDefault().handleError("LintConsole", e);
			} finally {
				console.getDocument().removeDocumentListener(this);
			}
		}
	}

	private void addLinkToConsole(final DocumentEvent event) throws BadLocationException {
		final int offset = event.fOffset + event.fText.lastIndexOf(linkText);

		if (offset < 0)
			return;

		console.addHyperlink(new FileLinkForExternalFiles(path), offset, linkText.length());
	}

	private boolean linkTextFound(final DocumentEvent event) {
		return event.fText.contains(linkText);
	}
}