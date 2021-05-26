package com.linticator.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.IPreferenceStore;

import com.linticator.Linticator;
import com.linticator.config.PreferenceConstants;
import com.linticator.view.console.LintConsole;

public class ShowConsoleAction extends Action {

	private final IPreferenceStore prefStore;

	public ShowConsoleAction() {
		super();
		setImageDescriptor(Linticator.getImageDescriptor("icons/writeout_co.gif")); //$NON-NLS-1$
		prefStore = Linticator.getDefault().getPreferenceStore();
		final boolean prefValue = prefStore.getBoolean(PreferenceConstants.LINT_SHOW_CONSOLE);
		setChecked(prefValue);
		setToolTipText("Show Lint Console When Output Changes");
	}

	@Override
	public void run() {
		final boolean checked = isChecked();
		prefStore.setValue(PreferenceConstants.LINT_SHOW_CONSOLE, checked);
		LintConsole.showConsole = checked;
	}

}
