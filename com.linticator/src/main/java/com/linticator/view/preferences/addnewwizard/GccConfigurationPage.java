package com.linticator.view.preferences.addnewwizard;

import java.io.File;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;

import com.linticator.base.FileUtil;
import com.linticator.base.HelpUtil;
import com.linticator.base.WorkspaceUtil;
import com.linticator.config.ConfigurationEntry;
import com.linticator.config.PreferenceConstants;
import com.linticator.functional.Function1;
import com.linticator.view.preferences.fieldeditors.FileAndVariablesFieldEditor;

public class GccConfigurationPage extends WizardPage {

	private final String configId;
	private StringFieldEditor compilerConfig;
	private StringFieldEditor compilerHeader;
	private StringFieldEditor makefile;
	private final AtomicBoolean canFinishWizard;
	private IPreferenceStore preferenceStore;

	public GccConfigurationPage(final String configId, IPreferenceStore preferenceStore, final AtomicBoolean canFinishWizard) {
		super("wizardPage");
		this.configId = configId;
		this.preferenceStore = preferenceStore;
		this.canFinishWizard = canFinishWizard;
		setTitle("Configure GCC Compiler");
		setDescription("");
	}

	@Override
	public void createControl(final Composite parent) {
		final Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout(3, false));

		final ConfigurationEntry config = ConfigurationEntry.fromId(configId, preferenceStore);
		final File installLocation = config.getInstallLocation();
		final Collection<File> allFiles = FileUtil.allFilesRecursively(installLocation);

		final Composite c1 = new Composite(container, SWT.NULL);
		compilerConfig = FileAndVariablesFieldEditor.create(c1, configId + PreferenceConstants.LINT_COMPILER_CONFIG, preferenceStore,
				"Compiler Configuration (co-*.lnt)", this, new Function1<String, Void>() {

					@Override
					public Void apply(final String t) {
						saveSettings();
						return null;
					}
				});

		compilerConfig.setStringValue(InstallationDiscovery.getPathToOrEmptyString("co-gcc.lnt", allFiles));

		final Composite c2 = new Composite(container, SWT.NULL);
		compilerHeader = FileAndVariablesFieldEditor.create(c2, configId + PreferenceConstants.LINT_COMPILER_CONFIG_HEADER, preferenceStore,
				"Compiler Configuration Header (co-*.h)", this, new Function1<String, Void>() {

					@Override
					public Void apply(final String t) {
						saveSettings();
						return null;
					}
				});

		compilerHeader.setStringValue(InstallationDiscovery.getPathToOrEmptyString("co-gcc.h", allFiles));

		final Composite c3 = new Composite(container, SWT.NULL);
		makefile = FileAndVariablesFieldEditor.create(c3, configId + PreferenceConstants.LINT_MAKE_FILE, preferenceStore, "Configuration Makefile (Optional)", this,
				new Function1<String, Void>() {

					@Override
					public Void apply(final String t) {
						saveSettings();
						return null;
					}
				});

		makefile.setStringValue(InstallationDiscovery.getPathToOrEmptyString("co-gcc.mak", allFiles));

		final int width = compilerHeader.getLabelControl(c2).computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
		((GridData) compilerConfig.getLabelControl(c1).getLayoutData()).widthHint = width;
		((GridData) makefile.getLabelControl(c3).getLayoutData()).widthHint = width;

		final Link label = new Link(container, SWT.WRAP);
		label.setText(
				"The Makefile can be used to automatically generate configuration files for your system. It is not included in the PC/FlexeLint distribution but can be obtained from <a>www.gimpel.com</a>. See the help for more information.");
		final GridData gd = new GridData(SWT.LEFT, SWT.NONE, true, false, 3, 1);
		gd.widthHint = c3.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
		label.setLayoutData(gd);

		label.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(final Event event) {
				WorkspaceUtil.openLink("http://www.gimpel.com/html/pub90/co-gcc.mak", getClass().getSimpleName());
			}
		});

		saveSettings();

		setControl(container);

		HelpUtil.setFlexeLintConfigurationEntryDetailsHelp(container);
	}

	@Override
	public boolean isPageComplete() {

		if (compilerConfig == null || compilerHeader == null)
			return false;

		compilerConfig.store();

		compilerHeader.store();

		if (makefile != null) {
			makefile.store();
		}

		return compilerConfig.isValid() && compilerHeader.isValid() /* && makefile.isValid() */;
	}

	private void saveSettings() {
		final boolean pageComplete = isPageComplete();
		setPageComplete(pageComplete);
		canFinishWizard.set(pageComplete);
		getContainer().updateButtons();
	}
}
