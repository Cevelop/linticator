package com.linticator.view.console;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;

import com.linticator.Linticator;

public class LintConsole extends AbstractLintConsole {

	public static boolean showConsole;

	private final Collection<ConsoleOutputReceiver> outs = new ArrayList<ConsoleOutputReceiver>();

	public LintConsole() {
		outs.add(new ConsoleOutputReceiver() {

			@Override
			public void print(final String msg) {
				newMessageStream().print(msg);
			}

			@Override
			public void printLink(final String linkText, final IPath path) {
				addHyperlink(linkText, path, findConsole());
				print(linkText);
			}
		});

		addPatternMatchListener(new LintMessagePatternMatchListener());
	}

	@Override
	public void sendOutputTo(final ConsoleOutputReceiver out) {
		outs.add(out);
	}

	@Override
	public void stopSendingOutputTo(final ConsoleOutputReceiver out) {
		outs.remove(out);
	}

	@Override
	public void print(final String msg) {
		for (final ConsoleOutputReceiver out : outs) {
			out.print(msg);
		}
	}

	@Override
	public void printLink(final String linkText, final IPath path) {
		for (final ConsoleOutputReceiver out : outs) {
			out.printLink(linkText, path);
		}
	}

	private void addHyperlink(final String linkText, final IPath path, final AbstractLintConsole console) {
		console.getDocument().addDocumentListener(new HyperlinkInsertionListener(console, path, linkText));
	}

	@Override
	public void revealConsole() {
		if (showConsole) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					IConsoleView view;
					try {
						view = (IConsoleView) page.showView(IConsoleConstants.ID_CONSOLE_VIEW);
						view.display(findConsole());
					} catch (final PartInitException e) {
						Linticator.getDefault().handleError(this.getClass().getName(), e);
					}
				}
			});
		}
	}

	public static AbstractLintConsole findConsole() {
		final IConsoleManager consoleManager = ConsolePlugin.getDefault().getConsoleManager();

		for (final IConsole console : consoleManager.getConsoles()) {
			if (CONSOLE_NAME.equals(console.getName()))
				return (AbstractLintConsole) console;
		}

		final AbstractLintConsole console = new LintConsole();
		consoleManager.addConsoles(new IConsole[] { console });
		return console;
	}
}
