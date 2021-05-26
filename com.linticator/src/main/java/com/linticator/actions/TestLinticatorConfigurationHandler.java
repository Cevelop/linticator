package com.linticator.actions;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.linticator.view.welcome.TestConfigWizard;

public class TestLinticatorConfigurationHandler implements IHandler {

	@Override
	public void addHandlerListener(final IHandlerListener handlerListener) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {

		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				final TestConfigWizard wizard = new TestConfigWizard();
				final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				final WizardDialog dialog = new WizardDialog(shell, wizard) {
					@Override
					protected Control createContents(final Composite parent) {
						final Control control = super.createContents(parent);
						renameCancelButtonToFinish();
						hideFinishButton();
						return control;
					}

					private void hideFinishButton() {
						final Button finishButton = getButton(IDialogConstants.FINISH_ID);
						finishButton.setVisible(false);
						setButtonLayoutData(finishButton);
					}

					private void renameCancelButtonToFinish() {
						final Button cancelButton = getButton(IDialogConstants.CANCEL_ID);
						cancelButton.setText("&Finish");
						setButtonLayoutData(cancelButton);
					}
				};
				dialog.create();
				dialog.open();
			}
		});

		return null;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean isHandled() {
		return true;
	}

	@Override
	public void removeHandlerListener(final IHandlerListener handlerListener) {
	}
}
