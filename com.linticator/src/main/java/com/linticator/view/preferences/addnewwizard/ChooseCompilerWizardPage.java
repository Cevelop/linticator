package com.linticator.view.preferences.addnewwizard;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.linticator.base.FileUtil;
import com.linticator.config.ConfigurationEntry;
import com.linticator.config.PreferenceConstants;

public class ChooseCompilerWizardPage extends WizardPage {

	private final String configurationId;
	private Button btnGccincludingMingw;
	private Button btnPredefined;
	private Button btnManualConfiguration;
	private final AtomicBoolean canFinishWizard;
	private final TreeMap<String, File> existingCompilerOptionFiles = new TreeMap<String, File>();
	private final TreeMap<String, File> existingCompilerHeaderFiles = new TreeMap<String, File>();
	private IPreferenceStore preferenceStore;

	public ChooseCompilerWizardPage(final String configurationId, IPreferenceStore preferenceStore, final AtomicBoolean canFinish) {
		super("wizardPage");
		this.configurationId = configurationId;
		this.preferenceStore = preferenceStore;
		this.canFinishWizard = canFinish;
		setTitle("Configure Linticator for your compiler");
		setDescription("");
		setPageComplete(false);
	}

	@Override
	public void createControl(final Composite parent) {
		final Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(2, false));

		final Label lblSelectTheCompiler = new Label(container, SWT.NONE);
		lblSelectTheCompiler.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		lblSelectTheCompiler.setText("PC-/FlexeLint comes with existing configuration files for many compilers. ");

		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		final Label lblPleaseSelectHow = new Label(container, SWT.NONE);
		lblPleaseSelectHow.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		lblPleaseSelectHow.setText("Please select how you want to configure Linticator:");

		btnGccincludingMingw = new Button(container, SWT.RADIO);
		btnGccincludingMingw.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				setCompilerConfig(null);
			}
		});
		btnGccincludingMingw.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnGccincludingMingw.setText("Configure for use with GCC (including MinGW, Cygwin)");

		btnPredefined = new Button(container, SWT.RADIO);
		btnPredefined.setText("Use a built-in configuration:");

		final CCombo combo = new CCombo(container, SWT.BORDER | SWT.READ_ONLY);

		combo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		combo.setListVisible(true);
		combo.setText("select your compiler");
		combo.setVisibleItemCount(10);

		// Don't initialize in the constructor because then it's created on wizard startup when the base-path has not
		// been configured yet.
		findExistingConfigurations(configurationId, preferenceStore, existingCompilerOptionFiles, existingCompilerHeaderFiles);

		for (final String entry : existingCompilerOptionFiles.keySet()) {
			combo.add(entry);
		}

		btnManualConfiguration = new Button(container, SWT.RADIO);
		btnManualConfiguration.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				setCompilerConfig(null);
			}
		});
		btnManualConfiguration.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnManualConfiguration.setText("Select configuration files manually");

		combo.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(final MouseEvent e) {
				btnGccincludingMingw.setSelection(false);
				btnPredefined.setSelection(true);
				btnManualConfiguration.setSelection(false);
			}
		});

		final SelectionAdapter predefinedConfigurationListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				final int selectionIndex = combo.getSelectionIndex();
				if (selectionIndex < 0) {
					return;
				}
				final String selected = combo.getItems()[selectionIndex];
				setCompilerConfig(selected);
			}
		};
		btnPredefined.addSelectionListener(predefinedConfigurationListener);
		combo.addSelectionListener(predefinedConfigurationListener);
	}

	@Override
	public IWizardPage getNextPage() {

		if (btnPredefined.getSelection()) {
			return null; // we're done with the configuration
		}

		if (btnGccincludingMingw.getSelection()) {
			final GccConfigurationPage page = new GccConfigurationPage(configurationId, preferenceStore, canFinishWizard);
			page.setWizard(getWizard());
			return page;
		}

		if (btnManualConfiguration.getSelection()) {
			final ManualConfigurationPage page = new ManualConfigurationPage(configurationId, preferenceStore, canFinishWizard);
			page.setWizard(getWizard());
			return page;
		}

		return null;
	}

	private void setCompilerConfig(final String selectedConfigName) {
		if (selectedConfigName != null) {

			// Set the .lnt file
			final File compilerOptionsFile = existingCompilerOptionFiles.get(selectedConfigName);
			preferenceStore.setValue(configurationId + PreferenceConstants.LINT_COMPILER_CONFIG, compilerOptionsFile.getPath());

			// Set the .h file if it exists
			final File compilerHeaderFile = existingCompilerHeaderFiles.get(selectedConfigName);
			if (compilerHeaderFile != null) {
				preferenceStore.setValue(configurationId + PreferenceConstants.LINT_COMPILER_CONFIG_HEADER, compilerHeaderFile.getPath());
			}
		}

		// An option has been selected, which one doesn't matter.
		setPageComplete(true);

		// When a built-in configuration was selected, we can finish the wizard here.
		canFinishWizard.set(selectedConfigName != null);

		getContainer().updateButtons();
	}

	private static void findExistingConfigurations(final String configId, final IPreferenceStore preferenceStore, final TreeMap<String, File> optionsOut,
			final TreeMap<String, File> headersOut) {

		final ConfigurationEntry config = ConfigurationEntry.fromId(configId, preferenceStore);
		final File installLocation = config.getInstallLocation();

		final Pattern pattern = Pattern.compile("compiler options for (the )?(.*)", Pattern.CASE_INSENSITIVE);

		for (final File file : FileUtil.allFilesRecursively(installLocation)) {
			final String lintCompilerOptions = "co-(.*)\\.lnt";

			if (file.isFile() && file.getParent().endsWith("lnt") && file.getName().matches(lintCompilerOptions)) {

				try {
					final String compilerName = extractCompilerNameOrNull(pattern, file);

					if (compilerName != null) {

						final String displayName = compilerName + " (" + file.getName() + ")";
						optionsOut.put(displayName, file);

						// Some option files have a corresponding header file, ending in .h instead of .lnt
						final File correspondingHeader = new File(file.getParentFile(), file.getName().replaceAll("\\.lnt$", "\\.h"));
						if (correspondingHeader.exists()) {
							headersOut.put(displayName, correspondingHeader);
						}
					}

				} catch (final FileNotFoundException e) {
				} catch (final IOException e) {
				}
			}
		}
	}

	private static String extractCompilerNameOrNull(final Pattern pattern, final File file) throws FileNotFoundException, IOException {
		
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line;
			while ((line = reader.readLine()) != null) {
				final Matcher matcher = pattern.matcher(line);
				if (matcher.find()) {
					return matcher.group(2);
				}
			}
		} finally {
			if(reader != null) {
				reader.close();
			}
		}
		return null;
	}
}
