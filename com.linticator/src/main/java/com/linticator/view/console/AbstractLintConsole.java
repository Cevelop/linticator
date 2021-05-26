package com.linticator.view.console;

import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.MessageConsole;

public abstract class AbstractLintConsole extends MessageConsole implements IPropertyChangeListener {

	protected static final String CONSOLE_NAME = "Linticator";

	public AbstractLintConsole() {
		super(CONSOLE_NAME, null);
	}

	@Override
	protected void init() {
		super.init();

		JFaceResources.getFontRegistry().addListener(this);

		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				changeConsoleFont();
			}
		});
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		if (event.getProperty().equals(IDebugUIConstants.PREF_CONSOLE_FONT)) {
			changeConsoleFont();
		}
	}

	private void changeConsoleFont() {
		setFont(JFaceResources.getFont(IDebugUIConstants.PREF_CONSOLE_FONT));
	}

	public abstract void print(String msg);

	public abstract void printLink(final String linkText, final IPath path);

	public abstract void revealConsole();

	public abstract void sendOutputTo(final ConsoleOutputReceiver out);

	public abstract void stopSendingOutputTo(final ConsoleOutputReceiver out);

}