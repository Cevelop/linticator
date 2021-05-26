package com.linticator.view.preferences.fieldeditors;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import com.linticator.config.ConfigurationEntry;
import com.linticator.view.preferences.uicomponents.AvailableConfigurationsComposite;

//TODO move to Infrastructure-layer once created
/**
 * A {@link FieldEditor} to wrap a Linticator configuration component
 *
 * @author tstauber
 */
public class LinticatorConfigFieldEditor extends FieldEditor implements ISelectionProvider {

	private final AvailableConfigurationsComposite configComposite;

	public LinticatorConfigFieldEditor(final String name, final Composite parent) {
		configComposite = new AvailableConfigurationsComposite();
		configComposite.createControl(parent);
	}

	@Override
	public void addSelectionChangedListener(final ISelectionChangedListener listener) {
		configComposite.addSelectionChangedListener(listener);
	}

	@Override
	public ISelection getSelection() {
		return configComposite.getSelection();
	}

	public ConfigurationEntry getCheckedConfiguration() {
		return configComposite.getCheckedConfiguration();
	}

	@Override
	public void removeSelectionChangedListener(final ISelectionChangedListener listener) {
		configComposite.removeSelectionChangedListener(listener);
	}

	@Override
	public void setSelection(final ISelection selection) {
		configComposite.setSelection(selection);
	}

	@Override
	protected void adjustForNumColumns(final int numColumns) {
	}

	@Override
	protected void doFillIntoGrid(final Composite parent, final int numColumns) {
		final GridData gd = new GridData();
		gd.horizontalSpan = numColumns;
		configComposite.getControl().setLayoutData(gd);
	}

	/**
	 * Linticator configuration entries are loaded on creation, so this method is empty.
	 */
	@Override
	protected void doLoad() {
	}

	/**
	 * Linticator configuration entries are loaded on creation, so this method is empty.
	 */
	@Override
	protected void doLoadDefault() {
	}

	/**
	 * Linticator configuration entries are stored on creation, so this method is empty.
	 */
	@Override
	protected void doStore() {
	}

	@Override
	public int getNumberOfControls() {
		return 1;
	}

	@Override
	public void setEnabled(final boolean enabled, final Composite parent) {
		configComposite.setEnabled(enabled);
	}

	@Override
	public void setPreferenceStore(final IPreferenceStore store) {
		super.setPreferenceStore(store);
		if (store != null) {
			configComposite.setPreferenceStore(store);
		}
	}

}
