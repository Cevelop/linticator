package com.linticator.quickfixes.inhibitmessages;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.linticator.config.ProjectConfig;

public class CustomLintFileWizard extends Wizard {

	private final ProjectConfig projectConfig;

	public CustomLintFileWizard(final ProjectConfig projectConfig) {
		this.projectConfig = projectConfig;
		setWindowTitle("Custom Lint File Setup");
	}

	@Override
	public void addPages() {
		CustomLintFileWizardPage page = new CustomLintFileWizardPage(projectConfig);
		addPage(page);
	}

	public static int open(final ProjectConfig projectConfig) {
		final int[] returnCode = new int[] { WizardDialog.CANCEL };
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				final CustomLintFileWizard wizard = new CustomLintFileWizard(projectConfig);
				final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				final WizardDialog dialog = new WizardDialog(shell, wizard);
				dialog.create();
				returnCode[0] = dialog.open();
			}
		});

		return returnCode[0];
	}

	@Override
	public boolean performFinish() {
		return true;
	}
}
