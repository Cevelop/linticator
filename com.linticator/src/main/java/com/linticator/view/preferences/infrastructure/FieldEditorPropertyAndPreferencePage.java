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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PreferencesUtil;

//TODO move to Infrastructure-layer once created
/**
 * A combined {@code PropertyPage} and {@code PreferencePage}. Uses {@code FieldEditor}s for easier and cleaner
 * definition of preferences. If the page acts as a {@code PropertyPage} a header-bar, containing a checkbox (to enable
 * component specific settings) and link (to open the workspace preferences), will be added.
 *
 * The header-bar's design is held consistent with the default {@code PropertyAndPreferencePage}'s design.
 *
 *
 * @author tstauber
 */
public abstract class FieldEditorPropertyAndPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPropertyPage, IWorkbenchPreferencePage {

	private static final String S_LINK_TEXT = InfrastructureMessages.FieldEditorPropertyAndPreferencePage_link_text;

	private static final String S_ENABLE_PROJECT_PREF = InfrastructureMessages.FieldEditorPropertyAndPreferencePage_checkbox_text;

	private final IPropertyAndPreferenceHelper propertyAndPreferenceHelper = createPropertyAndPreferenceHelper();

	private final List<FieldEditor> editors = new ArrayList<FieldEditor>();
	private IAdaptable projectElement;
	private Button headerCheckbox;
	private Link headerLink;

	public FieldEditorPropertyAndPreferencePage(final int style) {
		super(style);
	}

	public FieldEditorPropertyAndPreferencePage(final String title, final int style) {
		super(title, style);
	}

	public FieldEditorPropertyAndPreferencePage(final String title, final ImageDescriptor image, final int style) {
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
	 * Returns true if this instance represents a property page
	 *
	 * @return true for property pages, false for preference pages
	 */
	public boolean isPropertyPage() {
		return getElement() != null;
	}

	/**
	 * Returns true if this instance represents a preference page
	 *
	 * @return false for property pages, true for preference pages
	 */
	public boolean isPreferencePage() {
		return !isPropertyPage();
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
	 * If this is a property page, the passed projectElement will be handled by calling {@link
	 * handlePropertyPageElement(IAdaptable)}. Then the controls will be created and the {@link FieldEditor}s will be
	 * updated.
	 *
	 * @see org.eclipse.jface.preference.PreferencePage#createControl(Composite)
	 */
	@Override
	public void createControl(final Composite parent) {
		super.createControl(parent);
		if (isPropertyPage()) {
			updateFieldEditors();
		}
	}

	/**
	 * If this is a property page a header, containing a checkbox and a link, will be inserted at the top. Then the
	 * contained {@link FieldEditor}s will be created.
	 *
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(final Composite parent) {
		if (isPropertyPage()) {
			createAndInitializeHeader(parent);
		}
		return super.createContents(parent);
	}

	/**
	 * This method is called when the checkbox, which toggles project specific preferences, has been changed.
	 */
	protected void validationHook() {
		// Override and do validation checks and setValid in subclass
	}

	/**
	 * Creates and initializes the header
	 */
	protected void createAndInitializeHeader(final Composite parent) {
		final int numColumnsHeader = 2;
		final Composite headerComposite = createHeaderComposite(parent, numColumnsHeader);
		headerCheckbox = createHeaderCheckbox(headerComposite, S_ENABLE_PROJECT_PREF);
		headerLink = createHeaderLink(headerComposite, S_LINK_TEXT);
		createHeaderSeparator(headerComposite, numColumnsHeader);
		initializeHeader();
	}

	/* Creates the composite the header is drawn on */
	private Composite createHeaderComposite(final Composite parent, final int numColumnsHeader) {
		final Composite headerComposite = new Composite(parent, SWT.NONE);
		final GridLayout layout = new GridLayout(numColumnsHeader, false);
		layout.marginHeight = 3;
		layout.marginWidth = 3;
		headerComposite.setLayout(layout);
		headerComposite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false, false));
		return headerComposite;
	}

	/* Creates the checkbox to enable project wide preferences */
	private Button createHeaderCheckbox(final Composite parent, final String label) {
		final Button button = new Button(parent, SWT.CHECK);
		button.setText(label);
		button.setLayoutData(new GridData(SWT.LEAD, SWT.NONE, false, false));
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				headerLink.setEnabled(!projectPreferencesEnabled());
				validationHook();
				updateFieldEditors();
			}
		});
		return button;
	}

	/* Creates the link to the workspace preferences */
	private Link createHeaderLink(final Composite comp, final String text) {
		final Link link = new Link(comp, SWT.PUSH);
		link.setText(text);
		final GridData gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		gd.horizontalAlignment = SWT.TRAIL;
		link.setLayoutData(gd);
		link.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				openWorkspacePreferences();
			}
		});
		return link;
	}

	/* Draws the line to separate the header from the content */
	private void createHeaderSeparator(final Composite headerComposite, final int numColumnsHeader) {
		final Label separator = new Label(headerComposite, SWT.HORIZONTAL | SWT.SEPARATOR);
		final GridData gd_separator = new GridData(GridData.FILL_HORIZONTAL);
		gd_separator.horizontalSpan = numColumnsHeader;
		separator.setLayoutData(gd_separator);
	}

	/* Initializes the headers components */
	private void initializeHeader() {
		final boolean projectPrefEnabled = propertyAndPreferenceHelper.projectSpecificPreferencesEnabled((IProject) getElement());
		headerCheckbox.setSelection(projectPrefEnabled);
		headerLink.setEnabled(!projectPrefEnabled);
	}

	/**
	 * Returns the property store in case of this page being used as property page or the standard preference store in
	 * case of being a preference page
	 *
	 * @see org.eclipse.jface.preference.PreferencePage#getPreferenceStore()
	 */
	@Override
	public IPreferenceStore getPreferenceStore() {
		return isPropertyPage() ? propertyAndPreferenceHelper.getProjectPreferences((IProject) projectElement) : propertyAndPreferenceHelper
				.getWorkspacePreferences();
	}

	/**
	 * Returns the {@link FieldEditor} members
	 */
	protected List<FieldEditor> getFieldEditors() {
		return editors;
	}

	/**
	 * Enables / disables the contained {@link FieldEditor}s and updates their {@code PreferenceStores} (Switches to
	 * property store for property page and to preference store for preference page)
	 */
	protected void updateFieldEditors() {
		for (final FieldEditor editor : editors) {
			editor.setEnabled(projectPreferencesEnabled(), getFieldEditorParent());
		}
	}

	/**
	 * If this is a property page, this method must set a flag-value in the property store to indicate that this project
	 * should use project specific preferences.
	 *
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	@Override
	public boolean performOk() {
		final boolean result = super.performOk();
		if (result && isPropertyPage()) {
			getPropertyAndPreferenceHelper().setProjectSpecificPreferences((IProject) getElement(), projectPreferencesEnabled());
		}
		return result;
	}

	/**
	 * Convenience method for testing if project specific preferences are enabled.
	 */
	protected boolean projectPreferencesEnabled() {
		return headerCheckbox.getSelection();
	}

	/**
	 * Defines what should happen after the "Restore Defaults" button was pressed. If this is a property page, the
	 * project will be reset to workspace preferences and the values of the contained {@link FieldEditor}s will be reset
	 * to the values in the corresponding {@link PreferenceInitializer}. If this method is overloaded, the overload
	 * should call {@code super.performDefaults()}.
	 *
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	@Override
	protected void performDefaults() {
		if (isPropertyPage()) {
			headerCheckbox.setSelection(false);
			headerLink.setEnabled(true);
			updateFieldEditors();
		}
		super.performDefaults();
	}

	/**
	 * If this is a property page, this method opens the corresponding workspace preference page.
	 */
	protected void openWorkspacePreferences() {
		PreferencesUtil.createPreferenceDialogOn(getShell(), getPageId(), null, null).open();
	}

	/**
	 * Initializes the {@link FieldEditorPreferencePage}'s preference store.
	 *
	 * Subclass can implement, but should call {@link super.init(IWorkbench)}
	 */
	@Override
	public void init(final IWorkbench workbench) {
		super.setPreferenceStore(propertyAndPreferenceHelper.getWorkspacePreferences());
	}
}
