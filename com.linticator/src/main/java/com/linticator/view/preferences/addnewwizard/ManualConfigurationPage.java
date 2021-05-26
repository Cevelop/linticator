package com.linticator.view.preferences.addnewwizard;

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.linticator.config.PreferenceConstants;
import com.linticator.functional.Function1;
import com.linticator.view.preferences.fieldeditors.FileAndVariablesFieldEditor;

public class ManualConfigurationPage extends WizardPage {

	private final String configId;
	private StringFieldEditor compilerConfig;
	private StringFieldEditor compilerHeader;
	private final AtomicBoolean canFinishWizard;
	private IPreferenceStore preferenceStore;

	public ManualConfigurationPage(final String configId, IPreferenceStore preferenceStore, final AtomicBoolean canFinishWizard) {
		super("wizardPage");
		this.configId = configId;
		this.preferenceStore = preferenceStore;
		this.canFinishWizard = canFinishWizard;
		setTitle("Manual Configuration");
		setDescription("");
	}

	@Override
	public void createControl(final Composite parent) {
		final Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout(3, false));

		final Composite c1 = new Composite(container, SWT.NULL);
		compilerConfig = FileAndVariablesFieldEditor.create(c1, configId + PreferenceConstants.LINT_COMPILER_CONFIG, preferenceStore,
				"Compiler Configuration (co-*.lnt)", this, new Function1<String, Void>() {

					@Override
					public Void apply(final String t) {
						saveSettings();
						return null;
					}
				});


		final Composite c2 = new Composite(container, SWT.NULL);
		compilerHeader = FileAndVariablesFieldEditor.create(c2, configId
				+ PreferenceConstants.LINT_COMPILER_CONFIG_HEADER, preferenceStore, "Compiler Configuration Header (co-*.h) (optional)", this,
				new Function1<String, Void>() {

					@Override
					public Void apply(final String t) {
						saveSettings();
						return null;
					}
				});


		final int width = compilerHeader.getLabelControl(c2).computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
		((GridData) compilerConfig.getLabelControl(c1).getLayoutData()).widthHint = width;

		saveSettings();
		
		setControl(container);
	}

	@Override
	public boolean isPageComplete() {

		if (compilerConfig == null)
			return false;

		compilerConfig.store();

		if (compilerHeader != null) {
			compilerHeader.store();
		}

		return compilerConfig.isValid();
	}

	private void saveSettings() {
		final boolean pageComplete = isPageComplete();
		setPageComplete(pageComplete);
		canFinishWizard.set(pageComplete);
		getContainer().updateButtons();
	}
}
