package com.linticator.quickfixes.inhibitmessages;

import java.util.Collection;
import java.util.HashMap;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

import com.linticator.base.HelpUtil;
import com.linticator.base.WorkspaceUtil;
import com.linticator.functional.Function1;
import com.linticator.functional.Function2;

public class InhibitionOptionsTablePage extends WizardPage {

	private final Collection<MessageInhibitionConfigurationEntry> messagesToConfigure;
	private Boolean runLinticatorAfterwards = true;

	public InhibitionOptionsTablePage(final Collection<MessageInhibitionConfigurationEntry> messagesToConfigure) {
		super("wizardPage");
		this.messagesToConfigure = messagesToConfigure;
		setTitle("Inhibit Messages");
		setDescription("Configure the inhibition options for the messages. Note that not all options are available for each message.");
	}

	@Override
	public void createControl(final Composite parent) {
		final Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new FillLayout(SWT.HORIZONTAL));

		createTable(container);

		HelpUtil.setInhibitionOptionsHelp(getControl());
	}

	private void createTable(final Composite container) {

		final Composite composite = new Composite(container, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		final Composite tableComposite = new Composite(composite, SWT.NONE);
		tableComposite.setLayout(new GridLayout(1, false));
		tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		final TableViewer tableViewer = new TableViewer(tableComposite, SWT.BORDER | SWT.FULL_SELECTION);
		final Table table = tableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		tableViewer.setContentProvider(new IStructuredContentProvider() {

			@Override
			public Object[] getElements(final Object inputElement) {
				return ((Collection<?>) inputElement).toArray();
			}

			@Override
			public void dispose() {
			}

			@Override
			public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
			}

		});

		addColumns(tableViewer);

		tableViewer.setInput(messagesToConfigure);
		tableViewer.getTable().setLinesVisible(true);
		tableViewer.getTable().setHeaderVisible(true);

		final Button btnCheckButton = new Button(composite, SWT.CHECK);
		btnCheckButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				setRunLinticatorAfterwards(btnCheckButton.getSelection());
			}
		});
		btnCheckButton.setSelection(true);
		btnCheckButton.setText("Run Linticator after configuring inhibition options.");

		final TableColumnLayout layout = new TableColumnLayout();
		tableComposite.setLayout(layout);

		for (final TableColumn tc : tableViewer.getTable().getColumns()) {
			layout.setColumnData(tc, new ColumnWeightData(1, 50));
		}

		layout.setColumnData(tableViewer.getTable().getColumns()[0], new ColumnWeightData(20));
	}

	private void addColumns(final TableViewer tableViewer) {

		final TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
		column.getColumn().setWidth(200);
		column.getColumn().setText("Message");
		column.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(final Object element) {
				return ((MessageInhibitionConfigurationEntry) element).displayString();
			}
		});

		column.getViewer().addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				if (event.getSelection() instanceof IStructuredSelection) {
					final Object firstElement = ((IStructuredSelection) event.getSelection()).getFirstElement();
					if (firstElement instanceof MessageInhibitionConfigurationEntry) {
						final int line = ((MessageInhibitionConfigurationEntry) firstElement).getLineStartingFromZero();

						final IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();

						if (editor instanceof ITextEditor && line >= 0) {
							WorkspaceUtil.selectLineInEditor(line + 1, (ITextEditor) editor);
						}
					}
				}
			}
		});

		for (final ConfigEntryTriple e : MessageInhibitionConfigurationEntry.getAllConfigurableOptions()) {
			addColumn(tableViewer, e.name, e.getter, e.setter, e.editable);
		}
	}

	private void addColumn(final TableViewer tableViewer, final String name, final Function1<MessageInhibitionConfigurationEntry, Boolean> getValue,
			final Function2<MessageInhibitionConfigurationEntry, Boolean, Void> setValue,
			final Function1<MessageInhibitionConfigurationEntry, Boolean> canElementBeChecked) {
		TableViewerColumn column;
		column = new TableViewerColumn(tableViewer, SWT.NONE);
		column.getColumn().setWidth(50);
		column.getColumn().setText(name);

		final HashMap<MessageInhibitionConfigurationEntry, Boolean> lazyCanElementBeChecked = new HashMap<MessageInhibitionConfigurationEntry, Boolean>();

		column.setLabelProvider(new ColumnLabelProvider() {

			public String getText(Object element) {

				if (element instanceof MessageInhibitionConfigurationEntry && element != null) {

					memoizeCanElementBeChecked(canElementBeChecked, lazyCanElementBeChecked, element);
					
					if(!lazyCanElementBeChecked.get(element)) {
						return "";
					}
					
					if (isChecked(element)) {
						return Character.toString((char) 0x2714);
					} else {
						return Character.toString((char) 0x2610);
					}
				}
				return super.getText(element);
			}

			@Override
			public Image getImage(Object element) {
				return null;
			}
			
			@Override
			public Font getFont(Object element) {
				Display display = Display.getCurrent();
				Font font = tableViewer.getControl().getFont();
				FontDescriptor fontDescriptor = FontDescriptor.createFrom(font);
				return fontDescriptor.setStyle(SWT.BOLD).createFont(display);
			}

			private boolean isChecked(final Object element) {
				return getValue.apply((MessageInhibitionConfigurationEntry) element);
			}
		});

		column.setEditingSupport(new EditingSupport(tableViewer) {

			CheckboxCellEditor checkboxCellEditor = new CheckboxCellEditor(tableViewer.getTable());

			@Override
			protected boolean canEdit(final Object element) {

				memoizeCanElementBeChecked(canElementBeChecked, lazyCanElementBeChecked, element);

				return lazyCanElementBeChecked.get(element);
			}

			@Override
			protected CellEditor getCellEditor(final Object element) {
				return checkboxCellEditor;
			}

			@Override
			protected void setValue(final Object element, final Object value) {
				setValue.apply(((MessageInhibitionConfigurationEntry) element), (Boolean) value);
				getViewer().update(element, null);
			}

			@Override
			protected Object getValue(final Object element) {
				return getValue.apply((MessageInhibitionConfigurationEntry) element);
			}

		});
	}

	public Boolean getRunLinticatorAfterwards() {
		return runLinticatorAfterwards;
	}

	public void setRunLinticatorAfterwards(final Boolean runLinticatorAfterwards) {
		this.runLinticatorAfterwards = runLinticatorAfterwards;
	}

	private void memoizeCanElementBeChecked(final Function1<MessageInhibitionConfigurationEntry, Boolean> canEdit,
			final HashMap<MessageInhibitionConfigurationEntry, Boolean> lazyCanElementBeChecked, final Object element) {
		if (!lazyCanElementBeChecked.containsKey(element)) {
			lazyCanElementBeChecked.put((MessageInhibitionConfigurationEntry) element, canEdit.apply((MessageInhibitionConfigurationEntry) element));
		}
	}
}
