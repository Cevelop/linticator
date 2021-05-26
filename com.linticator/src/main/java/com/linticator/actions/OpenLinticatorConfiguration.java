package com.linticator.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.PreferencesUtil;

public class OpenLinticatorConfiguration extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		open();
		return null;
	}

	public void open() {
		final PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(Display.getCurrent().getActiveShell(),
				"com.linticator.preferences", null, null);
		dialog.open();
	}
}
