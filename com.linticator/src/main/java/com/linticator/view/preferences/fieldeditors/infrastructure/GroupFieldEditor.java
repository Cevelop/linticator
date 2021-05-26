package com.linticator.view.preferences.fieldeditors.infrastructure;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;


//TODO move to Infrastructure-layer once created
/**
 * A {@link FieldEditor} that represents a SWT {@link Group}. The
 * {@code GroupFieldEditor} draws a box around all the {@code FieldEditor}s
 * contained in it. The advantage in comparison to just putting a SWT
 * {@code Group} around the elements is, that all the margins are provided.
 *
 * @author tstauber
 */
public class GroupFieldEditor extends GroupingFieldEditor {

	/**
	 * Creates a {@code GroupFieldEditor} with default value for {@code margin}
	 * (8)
	 *
	 * @param name
	 *            The name of the preference this field editor works on
	 * @param captionText
	 *            The caption text of the composite, empty String "" is allowed
	 * @param parent
	 *            The parent of the field editor's control
	 * @param numColumnsInternal
	 *            In how many columns the contained FieldEditors shall be
	 *            aligned
	 * @param numColumnsExternal
	 *            In how many columns the GroupFieldEditor shall appear for
	 *            external observers
	 */
	public GroupFieldEditor(final String name, final String captionText, final Composite parent,
			final int numColumnsInternal, final int numColumnsExternal) {
		this(name, captionText, parent, numColumnsInternal, numColumnsExternal, DEFAULT_MARGIN);
	}

	/**
	 * Creates a composite field editor. This editor can contain multiple other
	 * field editors and draws a simple border around. The number of columns can
	 * be passed as an argument, else it will adjust to the minimum width
	 * required by the components
	 *
	 * @param name
	 *            The name of the preference this field editor works on
	 * @param captionText
	 *            The caption text of the composite, empty String "" is allowed
	 * @param parent
	 *            The parent of the field editor's control
	 * @param numColumnsInternal
	 *            In how many columns the FieldEditors shall be aligned
	 * @param numColumnsExternal
	 *            In how many columns the GroupFieldEditor shall appear for
	 *            external observers
	 * @param marginInPx
	 *            The margin between the contained field editors in pixels
	 */
	public GroupFieldEditor(final String name, final String captionText, final Composite parent,
			final int numColumnsInternal, final int numColumnsExternal, final int marginInPx) {
		super(name, captionText, parent, numColumnsInternal, numColumnsExternal, marginInPx);

		composite = new Group(parent, SWT.DEFAULT);
		((Group) composite).setText(getLabelText());

	}

}
