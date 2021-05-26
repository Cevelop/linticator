package com.linticator.view.preferences;

import org.eclipse.cdt.codan.ui.LabelFieldEditor;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.linticator.Linticator;
import com.linticator.config.PreferenceConstants;
import com.linticator.view.preferences.fieldeditors.LinticatorConfigFieldEditor;
import com.linticator.view.preferences.helpers.PropAndPrefHelper;
import com.linticator.view.preferences.infrastructure.FieldEditorPropertyAndPreferencePage;
import com.linticator.view.preferences.infrastructure.IPropertyAndPreferenceHelper;

/**
 * @author tstauber
 */
public class PropertyAndPreferencePage extends FieldEditorPropertyAndPreferencePage {

	private static final String U_ICON_PATH = "resources/icons/scrubber.gif"; //$NON-NLS-1$
	private static final ImageDescriptor IMAGE_DESCRIPTOR_FROM_PLUGIN = AbstractUIPlugin.imageDescriptorFromPlugin(Linticator.PLUGIN_ID, U_ICON_PATH);

	private LinticatorConfigFieldEditor tbl_config;

	public PropertyAndPreferencePage() {
		super(Linticator.PLUGIN_NAME, IMAGE_DESCRIPTOR_FROM_PLUGIN, GRID);
		noDefaultAndApplyButton();
	}

	@Override
	protected String getPageId() {
		return "com.linticator.preferences"; //$NON-NLS-1$
	}

	@Override
	protected IPropertyAndPreferenceHelper createPropertyAndPreferenceHelper() {
		return new PropAndPrefHelper();
	}

	@Override
	protected void createFieldEditors() {
		final LabelFieldEditor lbl_description = new LabelFieldEditor(Messages.PropertyAndPreferencePage_remove_or_edit, getFieldEditorParent());
		addField(lbl_description);

		tbl_config = new LinticatorConfigFieldEditor("linticator_selector", getFieldEditorParent()); //$NON-NLS-1$
		tbl_config.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				validationHook();
			}
		});
		addField(tbl_config);

		if (isPreferencePage()) {
			final BooleanFieldEditor autoRunOnBuild = new BooleanFieldEditor(PreferenceConstants.LINT_AUTOMATICALLY_AFTER_BUILD,
					Messages.PropertyAndPreferencePage_auto_run_on_build, getFieldEditorParent());
			addField(autoRunOnBuild);
		}

	}

	@Override
	protected void validationHook() {
		if (((isPropertyPage() && projectPreferencesEnabled()) || isPreferencePage()) && tbl_config.getCheckedConfiguration() == null) {
			setValid(false);
			setErrorMessage(Messages.PropertyAndPreferencePage_error_default);
		} else {
			setValid(true);
			setErrorMessage(null);
		}
	}
}
