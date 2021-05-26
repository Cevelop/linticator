package com.linticator.view.preferences.fieldeditors.infrastructure;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;

//TODO move to Infrastructure-layer once created
/**
 * @author tstauber
 */
public class LinkFieldEditor extends FieldEditor {

	protected Link link;

	/**
	 * All links can use the same preference name since they don't store any preference.
	 *
	 * @param linkText
	 *            text for the link
	 * @param listener
	 *            SelectionListener to activate on widgetSelected() and on widgetDefaultSelected()
	 * @param parent
	 *            Composite
	 */
	public LinkFieldEditor(final String linkText, final SelectionListener listener, final Composite parent) {
		init("link", linkText);
		link = new Link(parent, SWT.NONE);
		link.setFont(parent.getFont());
		if (linkText.contains("<A>") && linkText.contains("</A>")) {
			link.setText(linkText);
		} else {
			link.setText("<A>" + linkText + "</A>");
		}
		link.addSelectionListener(listener);
		createControl(parent);
	}

	/**
	 * Adjusts the field editor to be displayed correctly for the given number of columns.
	 *
	 * @param numColumns
	 *            number of columns
	 */
	@Override
	protected void adjustForNumColumns(final int numColumns) {
		((GridData) link.getLayoutData()).horizontalSpan = numColumns;
	}

	/**
	 * Fills the field editor's controls into the given parent.
	 *
	 * @param parent
	 *            Composite
	 * @param numColumns
	 *            number of columns
	 */
	@Override
	protected void doFillIntoGrid(final Composite parent, final int numColumns) {

		final GridData gridData = new GridData();
		gridData.horizontalSpan = numColumns;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = false;
		gridData.verticalAlignment = GridData.CENTER;
		gridData.grabExcessVerticalSpace = false;

		link.setLayoutData(gridData);
	}

	public void setSelectionListener(final SelectionListener listener) {
		link.addSelectionListener(listener);
	}

	public void setLayoutData(final Object layoutData) {
		link.setLayoutData(layoutData);
	}

	/**
	 * Returns the number of controls in the field editor.
	 *
	 * @return 1
	 */
	@Override
	public int getNumberOfControls() {
		return 1;
	}

	/**
	 * Links do not persist any preferences, so this method is empty.
	 */
	@Override
	protected void doLoad() {
	}

	/**
	 * Links do not persist any preferences, so this method is empty.
	 */
	@Override
	protected void doLoadDefault() {
	}

	/**
	 * Links do not persist any preferences, so this method is empty.
	 */
	@Override
	protected void doStore() {
	}

}
