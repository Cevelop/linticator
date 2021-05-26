package com.linticator.view.preferences.infrastructure;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPropertyPage;


//TODO move to Infrastructure-layer once created
/**
 * A {@code PropertyPage} which uses {@code FieldEditor}s. Uses {@code FieldEditor}s for easier and cleaner definition
 * of preferences.
 *
 * @author tstauber
 */
public abstract class FieldEditorPropertyPage extends FieldEditorPreferencePage implements IWorkbenchPropertyPage {

	private final IPropertyAndPreferenceHelper propertyAndPreferenceHelper = createPropertyAndPreferenceHelper();

	private final List<FieldEditor> editors = new ArrayList<FieldEditor>();
	private IAdaptable projectElement;

	public FieldEditorPropertyPage(final int style) {
		super(style);
	}

	public FieldEditorPropertyPage(final String title, final int style) {
		super(title, style);
	}

	public FieldEditorPropertyPage(final String title, final ImageDescriptor image, final int style) {
		super(title, image, style);
	}

	/**
	 * Returns the id of the current preference page as defined in plugin.xml
	 *
	 * Subclasses must implement.
	 */
	abstract protected String getPageId();

	/**
	 * Initially creates the {@link IPropertyAndPreferenceHelper} for this {@code FieldEditorPropertyAndPreferencePage}
	 *
	 * DO NOT CALL DIRECTLY - USE {@link #getPropertyAndPreferenceHelper()} INSTEAD.
	 *
	 * Subclasses must implement.
	 */
	abstract protected IPropertyAndPreferenceHelper createPropertyAndPreferenceHelper();

	/**
	 * Returns the {@link IPropertyAndPreferenceHelper}
	 */
	protected IPropertyAndPreferenceHelper getPropertyAndPreferenceHelper() {
		return propertyAndPreferenceHelper;
	}

	/**
	 * Stores the {@link IAdaptable} passed to the property page by the framework.
	 *
	 * @see org.eclipse.ui.IWorkbenchPropertyPage#setElement(org.eclipse.core.runtime.IAdaptable)
	 */
	@Override
	public void setElement(final IAdaptable element) {
		projectElement = (IAdaptable) element.getAdapter(IResource.class);
		setPreferenceStore(getPreferenceStore());
	}

	/**
	 * Delivers the object that owns the properties shown in this property page.
	 *
	 * @see org.eclipse.ui.IWorkbenchPropertyPage#getElement()
	 */
	@Override
	public IAdaptable getElement() {
		return projectElement;
	}

	/**
	 * The addField method must be overridden to store the created {@link FieldEditor}s.
	 *
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#addField(org.eclipse.jface.preference.FieldEditor)
	 */
	@Override
	protected void addField(final FieldEditor editor) {
		editors.add(editor);
		super.addField(editor);
	}

	/**
	 * Returns the property store in case of this page being used as property page or the standard preference store in
	 * case of being a preference page
	 *
	 * @see org.eclipse.jface.preference.PreferencePage#getPreferenceStore()
	 */
	@Override
	public IPreferenceStore getPreferenceStore() {
		return getPropertyAndPreferenceHelper().getProjectPreferences((IProject) projectElement);
	}

	/**
	 * Returns the {@link FieldEditor} members
	 */
	protected List<FieldEditor> getFieldEditors() {
		return editors;
	}

}
