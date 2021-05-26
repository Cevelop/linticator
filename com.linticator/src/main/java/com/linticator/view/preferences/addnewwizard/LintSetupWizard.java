package com.linticator.view.preferences.addnewwizard;

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;



public class LintSetupWizard extends Wizard {

	private final String configurationId;
	private final AtomicBoolean canFinish = new AtomicBoolean(false);
	private IPreferenceStore preferenceStore;

	public LintSetupWizard(final String newConfigurationId, IPreferenceStore preferenceStore) {
		this.preferenceStore = preferenceStore;
		setWindowTitle("Lint Setup");
		configurationId = newConfigurationId;
	}
	
	@Override
	public void createPageControls(final Composite pageContainer) {
	}

	@Override
	public void addPages() {
		addPage(new LocateLintInstallation(configurationId, preferenceStore, canFinish));
		addPage(new ChooseCompilerWizardPage(configurationId, preferenceStore, canFinish));
	}

	@Override
	public boolean performFinish() {
		// do we need to do anything? The configuration is saved automatically.
		return true;
	}

	public static int open(final String newConfigurationId, IPreferenceStore preferenceStore) {
		final LintSetupWizard wizard = new LintSetupWizard(newConfigurationId, preferenceStore);
		final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		final WizardDialog dialog = new WizardDialog(shell, wizard);
		return dialog.open();
	}
	
	@Override
	public boolean canFinish() {
		return canFinish.get();
	}
	
}
