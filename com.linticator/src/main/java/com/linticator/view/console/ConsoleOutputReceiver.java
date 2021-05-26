package com.linticator.view.console;

import org.eclipse.core.runtime.IPath;

public interface ConsoleOutputReceiver {
	void print(final String msg);

	void printLink(final String linkText, final IPath path);
}