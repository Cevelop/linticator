package com.linticator.view.console;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.ui.console.IPatternMatchListener;
import org.eclipse.ui.console.PatternMatchEvent;
import org.eclipse.ui.console.TextConsole;

import com.linticator.base.ParserUtil;
import com.linticator.lint.parsing.Parser;
import com.linticator.markers.Message;

final class LintMessagePatternMatchListener implements IPatternMatchListener {

	Parser parser = new Parser();

	@Override
	public void matchFound(final PatternMatchEvent event) {
		if (event.getSource() instanceof AbstractLintConsole) {
			addHyperlinksToConsole(event.getOffset(), event.getLength(), (AbstractLintConsole) event.getSource());
		}
	}

	public void addHyperlinksToConsole(final int offset, final int length, final AbstractLintConsole console) {
		try {
			final String matchedLine = console.getDocument().get(offset, length);
			for (final Message message : parser.parseAllMessagesIn(matchedLine)) {
				final int offsetInLine = matchedLine.indexOf(message.getRawFileName());
				addHyperlinkForMessage(offset + offsetInLine, console, message);
			}
		} catch (final BadLocationException e) {
		}
	}

	public void addHyperlinkForMessage(final int offset, final AbstractLintConsole console, final Message message) {
		try {
			final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(message.getFile());

			if (file != null) {
				console.addHyperlink(createLink(message, message.getFile()), offset, message.getRawFileName().length());
			}
		} catch (final BadLocationException e) {
		} catch (final IllegalArgumentException e) {
			// yes, ignore
		}
	}

	private FileLinkForExternalFiles createLink(final Message message, final IPath path) {
		return new FileLinkForExternalFiles(path, message.getLine());
	}

	@Override
	public void disconnect() {
	}

	@Override
	public void connect(final TextConsole console) {
	}

	@Override
	public String getPattern() {
		return ParserUtil.FORMATTED_LINT_MESSAGE_REGEX;
	}

	@Override
	public String getLineQualifier() {
		return "-#m-";
	}

	@Override
	public int getCompilerFlags() {
		return 0;
	}
}