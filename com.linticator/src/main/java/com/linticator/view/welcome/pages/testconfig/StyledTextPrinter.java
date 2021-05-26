package com.linticator.view.welcome.pages.testconfig;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;

import com.linticator.view.console.ConsoleOutputReceiver;

class StyledTextPrinter implements ConsoleOutputReceiver {

	private final StyledText styledText;

	public StyledTextPrinter(final StyledText styledText) {
		this.styledText = styledText;
	}

	void printException(final Exception e) {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		e.printStackTrace(new PrintStream(baos));
		println(baos.toString());
	}

	void println(final String msg) {
		printLog(msg + "\n");
	}

	void printLog(final String msg) {
		styledText.getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				styledText.append(msg);
				scrollToEnd();
			}
		});
	}

	void boldprintln(final String msg) {
		coloredprint(msg + "\n", SWT.COLOR_BLACK);
	}

	void redprintln(final String msg) {
		coloredprint(msg + "\n", SWT.COLOR_RED);
	}

	void greenprintln(final String msg) {
		coloredprint(msg + "\n", SWT.COLOR_DARK_GREEN);
	}

	void grayprintln(final String msg) {
		coloredprint(msg + "\n", SWT.COLOR_GRAY);
	}

	void coloredprint(final String msg, final int color) {
		styledText.getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				final StyleRange styleRange = new StyleRange();
				styleRange.start = styledText.getText().length();
				styleRange.length = msg.length();
				styleRange.fontStyle = SWT.BOLD;
				styleRange.foreground = styledText.getDisplay().getSystemColor(color);

				styledText.append(msg);

				styledText.setStyleRange(styleRange);
				scrollToEnd();
			}
		});
	}

	private void scrollToEnd() {
		styledText.setTopIndex(styledText.getLineCount());
	}

	void emptyOutputArea() {
		styledText.getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				styledText.setText("");
			}
		});
	}

	@Override
	public void printLink(final String linkText, final IPath path) {
		coloredprint(linkText, SWT.COLOR_GRAY);
	}

	@Override
	public void print(final String msg) {
		coloredprint(msg, SWT.COLOR_GRAY);
	}
}
