package com.linticator.view.preferences.fieldeditors.infrastructure;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

//TODO move to Infrastructure-layer once created
/**
 * A {@link FieldEditor} that represents a SWT {@link Group}. The
 * {@code GroupingFieldEditor} draws a box around all the {@code FieldEditor}s
 * contained in it. The advantage in comparison to just putting a SWT
 * {@code Group} around the elements is, that all the margins are provided.
 *
 * @author tstauber
 */
public abstract class GroupingFieldEditor extends FieldEditor {

	protected static final int DEFAULT_MARGIN = 8;
	protected Collection<FieldEditor> members = new ArrayList<FieldEditor>();
	protected Composite composite;
	protected int numColumnsExternal;
	protected int numVirtualColsInternal;
	protected int numColsPerVirtualColInternal;
	protected int marginInPx;

	public GroupingFieldEditor(final String name, final String captionText, final Composite parent,
			final int numColumnsInternal, final int numColumnsExternal, final int marginInPx) {
		init(name, captionText);
		numVirtualColsInternal = numColsPerVirtualColInternal = numColumnsInternal;
		this.numColumnsExternal = numColumnsExternal;
		this.marginInPx = marginInPx;
	}
	/**
	 * Creates this field editor's main control containing all of its basic
	 * controls.
	 *
	 * @param parent
	 *            the parent control
	 */
	@Override
	protected void createControl(final Composite parent) {
		final GridLayout parentLayout = new GridLayout();
		parentLayout.numColumns = numColumnsExternal;
		parentLayout.marginWidth = parentLayout.marginHeight = 0;
		parentLayout.horizontalSpacing = HORIZONTAL_GAP;
		parent.setLayout(parentLayout);
		doFillIntoGrid(parent, parentLayout.numColumns);
	}

	/**
	 * Returns the internal composite. The composite can then be passed as
	 * parent to the containing field editors
	 *
	 * @return The {@code Composite} that is the parent of the composite field
	 *         editor
	 */
	public Composite getFieldEditorParent() {
		return composite;
	}

	private Composite getRealFieldEditorParent() {
		return composite.getParent();
	}

	/**
	 * Replaces all the field editors in the composite with the ones provided in
	 * {@code editors}
	 *
	 * @param editors
	 *            The FieldEditors that shall be part of the composite
	 */
	public void setFields(final Collection<FieldEditor> editors) {
		members = editors;
		for (final FieldEditor e : editors) {
			adjustGroupForNumColumns(e.getNumberOfControls());
		}
		fillIntoGrid(getFieldEditorParent(), numColsPerVirtualColInternal);
	}

	/**
	 * Adds the field editor {@code editor} to the composite
	 *
	 * @param editor
	 *            A single field editor that shall be part of the composite
	 */
	public void addField(final FieldEditor editor) {
		members.add(editor);
		adjustGroupForNumColumns(editor.getNumberOfControls());
		fillIntoGrid(getFieldEditorParent(), numColsPerVirtualColInternal);
	}

	/**
	 * Adds all the field editors contained in {@code editors} to the composite
	 *
	 * @param editor
	 *            A {@code Collection} of field editors that shall be part of
	 *            the composite
	 */
	public void addField(final Collection<FieldEditor> editors) {
		members.addAll(editors);
		for (final FieldEditor e : editors) {
			adjustGroupForNumColumns(e.getNumberOfControls());
		}
		fillIntoGrid(getFieldEditorParent(), numColsPerVirtualColInternal);
	}

	private void adjustGroupForNumColumns(final int numColumns) {
		if (numColsPerVirtualColInternal < numColumns) {
			numColsPerVirtualColInternal = numColumns;

			// adjustForNumColumns(numVirtualColsInternal);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void adjustForNumColumns(final int numColumns) {
		numColumnsExternal = numColumns;
		((GridData) getFieldEditorParent().getLayoutData()).horizontalSpan = numColumns;
	}

	/**
	 * Fills this field editor's basic controls into the given parent.
	 *
	 * @param parent
	 *            the composite used as a parent for the basic controls; the
	 *            parent's layout must be a <code>GridLayout</code>
	 * @param numColumns
	 *            the number of columns
	 */
	@Override
	public void fillIntoGrid(final Composite parent, final int numColumns) {
		Assert.isTrue(numColumns >= numColsPerVirtualColInternal);
		Assert.isTrue(parent.getLayout() instanceof GridLayout);
		doFillIntoGrid(parent, numColumns);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doFillIntoGrid(final Composite parent, final int numColumns) {
		adjustMembers();
		/* Need to set this again because children do overwrite the layout */
		final GridLayout groupLayout = new GridLayout();
		groupLayout.numColumns = numColsPerVirtualColInternal * numVirtualColsInternal;
		groupLayout.marginWidth = groupLayout.marginHeight = marginInPx;
		composite.setLayout(groupLayout);

		final GridData gd = new GridData(SWT.FILL, SWT.CENTER,
				(getRealFieldEditorParent() instanceof Group || !(getFieldEditorParent() instanceof Group)), false);
		gd.horizontalIndent = (getRealFieldEditorParent() instanceof Group) ? 0 : marginInPx;
		gd.horizontalSpan = numColumnsExternal;
		composite.setLayoutData(gd);
	}

	protected void adjustMembers() {
		for (final FieldEditor editor : members) {
			try {
				final Method adjustForNumColumns = editor.getClass().getDeclaredMethod("adjustForNumColumns");
				adjustForNumColumns.setAccessible(true);
				adjustForNumColumns.invoke(numColsPerVirtualColInternal);
				editor.fillIntoGrid(getFieldEditorParent(), numColsPerVirtualColInternal);
			} catch (final Exception ignored) {
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doLoad() {
		if (members != null) {
			for (final FieldEditor editor : members) {
				editor.load();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doLoadDefault() {
		if (members != null) {
			for (final FieldEditor editor : members) {
				editor.loadDefault();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doStore() {
		if (members != null) {
			for (final FieldEditor editor : members) {
				editor.store();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void store() {
		super.store();
		doStore();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getNumberOfControls() {
		return numColumnsExternal;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
		if (members != null && !members.isEmpty()) {
			members.iterator().next().setFocus();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setEnabled(final boolean enabled, final Composite parentParam) {
		if (members != null) {
			for (final FieldEditor editor : members) {
				editor.setEnabled(enabled, composite);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPreferenceStore(final IPreferenceStore store) {
		super.setPreferenceStore(store);
		if (members != null) {
			for (final FieldEditor editor : members) {
				editor.setPreferenceStore(store);
			}
		}
	}

}
