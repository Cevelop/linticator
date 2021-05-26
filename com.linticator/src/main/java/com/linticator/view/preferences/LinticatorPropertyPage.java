package com.linticator.view.preferences;

import org.eclipse.cdt.ui.newui.MultiLineTextFieldEditor;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.linticator.Linticator;
import com.linticator.config.PreferenceConstants;
import com.linticator.lint.LintProjectJob;
import com.linticator.lint.configurator.BuildLintProjectConfigJob;
import com.linticator.view.preferences.fieldeditors.FileAndVariablesFieldEditor;
import com.linticator.view.preferences.fieldeditors.infrastructure.GroupFieldEditor;
import com.linticator.view.preferences.fieldeditors.infrastructure.LinkFieldEditor;
import com.linticator.view.preferences.helpers.PropAndPrefHelper;
import com.linticator.view.preferences.infrastructure.FieldEditorPropertyPage;
import com.linticator.view.preferences.infrastructure.IPropertyAndPreferenceHelper;

public class LinticatorPropertyPage extends FieldEditorPropertyPage implements IWorkbenchPropertyPage {

	final String SUFFIX = ".lnt"; //$NON-NLS-1$
	private static final String U_ICON_PATH = "resources/icons/scrubber.gif"; //$NON-NLS-1$
	private static final ImageDescriptor IMAGE_DESCRIPTOR_FROM_PLUGIN = AbstractUIPlugin.imageDescriptorFromPlugin(Linticator.PLUGIN_ID, U_ICON_PATH);

	public LinticatorPropertyPage() {
		super(Linticator.PLUGIN_NAME, IMAGE_DESCRIPTOR_FROM_PLUGIN, GRID);
	}

	@Override
	protected String getPageId() {
		return "com.linticator.properties"; //$NON-NLS-1$
	}

	@Override
	protected IPropertyAndPreferenceHelper createPropertyAndPreferenceHelper() {
		return new PropAndPrefHelper();
	}

	@Override
	protected void createFieldEditors() {
		/* Library Headers */
		final String[][] entryNamesAndValues = new String[][] { { Messages.PropertyPage_2, "0" }, // $NON-NLS-2$
			{ Messages.PropertyPage_4, "1" }, // $NON-NLS-2$
			{ Messages.PropertyPage_6, "2" }, // $NON-NLS-2$
			{ Messages.PropertyPage_8, "3" }, // $NON-NLS-2$
			{ Messages.PropertyPage_10, "4" } // $NON-NLS-2$
		};
		final ComboFieldEditor cmb_libraryHeaders = new ComboFieldEditor(PreferenceConstants.MESSAGE_LEVEL_PROPKEY, Messages.PropertyPage_1,
				entryNamesAndValues, getFieldEditorParent());
		addField(cmb_libraryHeaders);

		/* Custom file text box */
		final FileAndVariablesFieldEditor str_customLintFile = new FileAndVariablesFieldEditor(PreferenceConstants.CUSTOM_LINT_FILE_PROPKEY,
				Messages.PropertyPage_12, getFieldEditorParent());
		addField(str_customLintFile);

		/* Custom arguments text box */
		final MultiLineTextFieldEditor mlt_customArgs = new MultiLineTextFieldEditor(PreferenceConstants.CUSTOM_LINT_ARGUMENTS_PROPKEY,
				Messages.PropertyPage_13, getFieldEditorParent());
		addField(mlt_customArgs);
		// Make TextWidget grow vertically
		final Text mlt_text = mlt_customArgs.getTextControl(getFieldEditorParent());
		final GridData gd_customArgs = (GridData) mlt_text.getLayoutData();
		gd_customArgs.grabExcessVerticalSpace = true;
		gd_customArgs.verticalAlignment = SWT.FILL;
		mlt_text.setLayoutData(gd_customArgs);

		/* Other options check-boxes */
		final GroupFieldEditor grp_otherOptions = new GroupFieldEditor("Ignored", Messages.PropertyPage_15, getFieldEditorParent(), 3, 2); //$NON-NLS-1$
		grp_otherOptions.addField(new BooleanFieldEditor(PreferenceConstants.USE_CPP14, Messages.PropertyPage_16, grp_otherOptions.getFieldEditorParent()));
		grp_otherOptions.addField(new BooleanFieldEditor(PreferenceConstants.USE_CPP11, Messages.PropertyPage_17, grp_otherOptions.getFieldEditorParent()));
		grp_otherOptions.addField(new BooleanFieldEditor(PreferenceConstants.USE_C11, Messages.PropertyPage_18, grp_otherOptions.getFieldEditorParent()));
		grp_otherOptions.addField(
				new BooleanFieldEditor(PreferenceConstants.USE_PREDEFINED_COMPILER_SYMBOLS, Messages.PropertyPage_19, grp_otherOptions.getFieldEditorParent()));
		addField(grp_otherOptions);

		/* Link to Workspace Preferences */
		addField(new LinkFieldEditor(Messages.PropertyPage_20, new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				PreferencesUtil.createPreferenceDialogOn(getShell(), "com.linticator.preferences", null, null).open(); //$NON-NLS-1$
			}
		}, getFieldEditorParent()));

		/* Link to load pre-update properties */
		createAndAddUpdateHelperLink(str_customLintFile, mlt_customArgs);
	}

	private void createAndAddUpdateHelperLink(final FileAndVariablesFieldEditor str_customLintFile, final MultiLineTextFieldEditor mlt_customArgs) {
		try {
			final String legacyPropertyCustomFile = ((IResource) getElement())
					.getPersistentProperty(new QualifiedName(PreferenceConstants.PLUGIN_ID, PreferenceConstants.CUSTOM_LINT_FILE_PROPKEY));
			final String legacyPropertyCustomArgs = ((IResource) getElement())
					.getPersistentProperty(new QualifiedName(PreferenceConstants.PLUGIN_ID, PreferenceConstants.CUSTOM_LINT_ARGUMENTS_PROPKEY));
			if (legacyPropertyCustomArgs != null || legacyPropertyCustomFile != null) {
				addField(new LinkFieldEditor("Load pre-update custom arguments and custom lint file (Textbox which shall be loaded must be empty!)",
						new SelectionAdapter() {
					@Override
					public void widgetSelected(final SelectionEvent event) {
						if (str_customLintFile.getStringValue().isEmpty()) {
							str_customLintFile.setStringValue(legacyPropertyCustomFile);
						}
						if (mlt_customArgs.getStringValue().isEmpty()) {
							mlt_customArgs.setStringValue(legacyPropertyCustomArgs);
						}
					}
				}, getFieldEditorParent()));
			}
		} catch (final Exception Ignored) {
		}
	}

	@Override
	protected void performApply() {
		/* Run linticator on apply */
		new BuildLintProjectConfigJob((IProject) getElement()).schedule();
		new LintProjectJob((IProject) getElement()).schedule();

		performOk();
	}

}
