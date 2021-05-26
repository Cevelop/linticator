package com.linticator.view.welcome;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.PlatformUI;

import com.linticator.config.WorkspaceConfiguration;

public class StartupHook implements IStartup {

	@Override
	public void earlyStartup() {
		if (WorkspaceConfiguration.askForConfiguration()) {
			openWelcomeWizard();
		}
	}

	public static void openWelcomeWizard() {
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				final WelcomeWizard wizard = new WelcomeWizard();
				final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				final WizardDialog dialog = new WizardDialog(shell, wizard) {
					@Override
					protected Control createContents(final Composite parent) {
						final Control control = super.createContents(parent);
						final Button button = getButton(IDialogConstants.CANCEL_ID);
						button.setText("Don't Ask Again");
						setButtonLayoutData(button);
						return control;
					}
				};
				dialog.create();
				dialog.open();
			}
		});
	}
}
