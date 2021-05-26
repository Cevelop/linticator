package com.linticator.view.preferences;


import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import com.linticator.base.HelpUtil;
import com.linticator.config.ConfigurationEntry;
import com.linticator.config.PreferenceConstants;
import com.linticator.view.preferences.fieldeditors.FileAndVariablesFieldEditor;

public class SingleConfigurationEntryDialog extends FieldEditorPreferencePage implements IWizardPage {

	private final String uniqueId;
	private IWizard wizard;
	private ConfigurationEntry defaults = null;
	private StringFieldEditor nameField;
	private FileFieldEditor compilerConfigurationField;
	private FileFieldEditor compilerConfigurationHeaderField;
	private FileFieldEditor makeFileField;
	private FileFieldEditor documentationFileField;
	private FileFieldEditor executableField;
	private StringFieldEditor fileExtensionsField;
	private final static String title = "Flexe/Pc-Lint Configuration";

	public SingleConfigurationEntryDialog(final String uniqueId, final IPreferenceStore preferenceStore) {
		super(GRID);
		this.uniqueId = uniqueId;
		noDefaultAndApplyButton();
		setPreferenceStore(preferenceStore);
		setDescription("Linticator configuration.");
		setTitle(title);
	}

	public SingleConfigurationEntryDialog(final String uniqueId, final IPreferenceStore preferenceStore,
			final ConfigurationEntry defaults) {
		this(uniqueId, preferenceStore);
		this.defaults = defaults;
	}

	@Override
	protected Control createContents(final Composite parent) {
		final Control contents = super.createContents(parent);

		if (defaults != null) {
			nameField.setStringValue(defaults.getName());
			compilerConfigurationField.setStringValue(defaults.getCompilerConfig());
			compilerConfigurationHeaderField.setStringValue(defaults.getCompilerConfigHeader());
			makeFileField.setStringValue(defaults.getMakeFile());
			documentationFileField.setStringValue(defaults.getDocumentationFile());
			executableField.setStringValue(defaults.getExecutable());
			fileExtensionsField.setStringValue(defaults.getFileExtensions());
		}

		if ("".equals(fileExtensionsField.getStringValue())) {
			fileExtensionsField.setStringValue("c cc cpp cxx");
		}

		HelpUtil.setFlexeLintConfigurationEntryDetailsHelp(getControl());

		return contents;
	}

	@Override
	protected void createFieldEditors() {

		nameField = createStringFieldEditor(PreferenceConstants.LINT_CONFIG_NAME, "Name");
		compilerConfigurationField = createFileFieldEditor(PreferenceConstants.LINT_COMPILER_CONFIG,
				"Compiler configuration:");
		compilerConfigurationHeaderField = createFileFieldEditor(PreferenceConstants.LINT_COMPILER_CONFIG_HEADER,
				"Compiler configuration header file:");
		// this is optional
		compilerConfigurationHeaderField.setEmptyStringAllowed(true);
		makeFileField = createMakeFileFieldEditor(PreferenceConstants.LINT_MAKE_FILE, "Makefile (only for GCC; optional):");
		// this is optional
		makeFileField.setEmptyStringAllowed(true);
		documentationFileField = createFileFieldEditor(PreferenceConstants.LINT_DOCUMENTATION,
				"Lint documentation file:");
		executableField = createFileFieldEditor(PreferenceConstants.LINT_EXECUTABLE, "Lint executable:");
		fileExtensionsField = createStringFieldEditor(PreferenceConstants.FILE_EXTENSIONS,
				"Lintable file extensions:");
		addField(nameField);
		addField(compilerConfigurationField);
		addField(compilerConfigurationHeaderField);
		addField(makeFileField);
		addField(documentationFileField);
		addField(executableField);
		addField(fileExtensionsField);
	}

	private StringFieldEditor createStringFieldEditor(final String pref, final String name) {
		final StringFieldEditor field = new StringFieldEditor(uniqueId + pref, name, getFieldEditorParent());
		field.setEmptyStringAllowed(false);
		return field;
	}

	private FileFieldEditor createMakeFileFieldEditor(final String preferenceId, final String preferenceDescription) {
		final FileFieldEditor field = new FileAndVariablesFieldEditor(uniqueId + preferenceId, preferenceDescription,
				getFieldEditorParent()) {

			@Override
			protected void showErrorMessage(final String msg) {
				super.showErrorMessage("Makefile 'co-gcc.mak' missing. It can be downloaded from www.gimpel.com.");
			}

		};
		field.setEmptyStringAllowed(true);
		return field;
	}

	private FileFieldEditor createFileFieldEditor(final String preferenceId, final String preferenceDescription) {
		final FileFieldEditor field = new FileAndVariablesFieldEditor(uniqueId + preferenceId, preferenceDescription,
				getFieldEditorParent());
		field.setEmptyStringAllowed(false);
		return field;
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		super.propertyChange(event);
		if (wizard != null && wizard.getContainer() != null) {
			wizard.getContainer().updateButtons();
			wizard.getContainer().updateMessage();
		}
	}

	public ConfigurationEntry getConfiguration() {
		return ConfigurationEntry.fromId(uniqueId, getPreferenceStore());
	}

	@Override
	protected Label createDescriptionLabel(final Composite parent) {
		// wizard already provides a label
		return null;
	}

	@Override
	public boolean canFlipToNextPage() {
		return false;
	}

	@Override
	public String getName() {
		return title;
	}

	@Override
	public IWizardPage getNextPage() {
		return null;
	}

	@Override
	public IWizardPage getPreviousPage() {
		return null;
	}

	@Override
	public IWizard getWizard() {
		return wizard;
	}

	@Override
	public boolean isPageComplete() {
		return okToLeave();
	}

	@Override
	public void setPreviousPage(final IWizardPage page) {
	}

	@Override
	public void setWizard(final IWizard newWizard) {
		wizard = newWizard;
	}
}
