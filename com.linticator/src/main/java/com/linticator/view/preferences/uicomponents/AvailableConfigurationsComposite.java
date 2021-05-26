package com.linticator.view.preferences.uicomponents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.linticator.base.StringUtil;
import com.linticator.config.ConfigurationEntry;
import com.linticator.config.PreferenceConstants;
import com.linticator.config.WorkspaceConfiguration;
import com.linticator.lint.configurator.BuildAllLintConfigsJob;
import com.linticator.view.preferences.SingleConfigurationEntryDialog;
import com.linticator.view.preferences.addnewwizard.LintSetupWizard;

/**
 * A composite that displays installed FlexeLints in a table. They can be added, removed, edited, and searched for.
 * <p>
 * Most of this code has been copied from the JDT's InstalledJREsBlock class.
 */
@SuppressWarnings("rawtypes")
public class AvailableConfigurationsComposite implements ISelectionProvider {

	private Composite fControl;

	private final List<ConfigurationEntry> fFlexeLintConfigurations = new ArrayList<ConfigurationEntry>();

	private CheckboxTableViewer fFlexeLintsList;

	private Button fAddButton;
	private Button fEditButton;
	private Button fDuplicateButton;
	private Button fRemoveButton;

	private IPreferenceStore prefStore;

	private final ListenerList fSelectionListeners = new ListenerList();

	private ISelection fPrevSelection = new StructuredSelection();

	private Table fTable;

	class FlexeLintsContentProvider implements IStructuredContentProvider {
		@Override
		public Object[] getElements(final Object input) {
			return fFlexeLintConfigurations.toArray();
		}

		@Override
		public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
		}

		@Override
		public void dispose() {
		}
	}

	static class FlexeLintLabelProvider extends LabelProvider implements ITableLabelProvider {

		@Override
		public String getColumnText(final Object element, final int columnIndex) {
			if (element instanceof ConfigurationEntry) {
				final ConfigurationEntry config = (ConfigurationEntry) element;
				switch (columnIndex) {
				case 0:
					return config.getName();
				case 1:
					return config.getInstallLocation().getAbsolutePath();
				}
			}
			return element.toString();
		}

		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {
			return null;
		}

	}

	public AvailableConfigurationsComposite() {

	}

	@SuppressWarnings("unchecked")
	@Override
	public void addSelectionChangedListener(final ISelectionChangedListener listener) {
		fSelectionListeners.add(listener);
	}

	@Override
	public ISelection getSelection() {
		return new StructuredSelection(fFlexeLintsList.getCheckedElements());
	}

	@Override
	public void removeSelectionChangedListener(final ISelectionChangedListener listener) {
		fSelectionListeners.remove(listener);
	}

	@Override
	public void setSelection(final ISelection selection) {
		if (selection instanceof IStructuredSelection && !selection.equals(fPrevSelection)) {
			fPrevSelection = selection;

			final Object config = ((IStructuredSelection) selection).getFirstElement();
			if (config == null) {
				fFlexeLintsList.setCheckedElements(new Object[] {});
			} else {
				checkFlexeLintConfiguration(config);

				if (noConfigurationIsDefault()) {
					checkFlexeLintConfiguration(fFlexeLintsList.getElementAt(0));
				}

				fFlexeLintsList.reveal(config);
			}
			fireSelectionChanged();
		}
	}

	private boolean noConfigurationIsDefault() {
		return fFlexeLintsList.getCheckedElements().length == 0 && fFlexeLintsList.getElementAt(0) != null;
	}

	private void checkFlexeLintConfiguration(final Object config) {
		fFlexeLintsList.setCheckedElements(new Object[] { config });
	}

	public void createControl(final Composite ancestor) {
		final Font font = ancestor.getFont();
		final Composite g = new Composite(ancestor, SWT.NONE);
		g.setLayout(new GridLayout(2, false));
		g.setFont(font);
		final GridData gd1 = new GridData(GridData.FILL_BOTH);
		gd1.horizontalSpan = 1;
		g.setLayoutData(gd1);
		final Composite parent = g;
		fControl = parent;
		final Label l = new Label(parent, SWT.NONE);
		l.setFont(parent.getFont());
		l.setText("Available Lint Configurations:");
		final GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
		gd2.horizontalSpan = 2;
		gd2.grabExcessHorizontalSpace = false;
		l.setLayoutData(gd2);

		fTable = new Table(parent, SWT.CHECK | SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
		final GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 250;
		gd.widthHint = 350;
		fTable.setLayoutData(gd);
		fTable.setFont(font);
		fTable.setHeaderVisible(true);
		fTable.setLinesVisible(true);

		TableColumn column = new TableColumn(fTable, SWT.NULL);
		column.setText("Name");
		column.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				sortByName();
			}
		});
		final int defaultwidth = 350 / 3 + 1;
		column.setWidth(defaultwidth);

		column = new TableColumn(fTable, SWT.NULL);
		column.setText("Location");
		column.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				sortByLocation();
			}
		});
		column.setWidth(defaultwidth);

		fFlexeLintsList = new CheckboxTableViewer(fTable);
		fFlexeLintsList.setLabelProvider(new FlexeLintLabelProvider());
		fFlexeLintsList.setContentProvider(new FlexeLintsContentProvider());
		// by default, sort by name
		sortByName();

		fFlexeLintsList.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(final SelectionChangedEvent evt) {
				enableButtons();
			}
		});

		fFlexeLintsList.addCheckStateListener(new ICheckStateListener() {
			@Override
			public void checkStateChanged(final CheckStateChangedEvent event) {
				if (event.getChecked()) {
					setCheckedConfiguration((ConfigurationEntry) event.getElement());
				} else {
					setCheckedConfiguration(null);
				}
			}
		});

		fFlexeLintsList.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(final DoubleClickEvent e) {
				if (!fFlexeLintsList.getSelection().isEmpty()) {
					editFlexeLintConfiguration();
				}
			}
		});
		fTable.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent event) {
				if (event.character == SWT.DEL && event.stateMask == 0) {
					if (fRemoveButton.isEnabled()) {
						removeFlexeLintConfigurations();
					}
				}
			}
		});
		final Composite buttons = new Composite(parent, SWT.NONE);
		final GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		buttons.setLayout(layout);
		buttons.setFont(font);
		final GridData gd3 = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gd3.horizontalSpan = 1;
		buttons.setLayoutData(gd3);

		fAddButton = createPushButton(buttons, "&Add New...");
		fAddButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(final Event evt) {
				addNewConfiguration();
			}
		});

		createSpacer(parent);

		fEditButton = createPushButton(buttons, "&Edit...");
		fEditButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(final Event evt) {
				editFlexeLintConfiguration();
			}
		});

		fDuplicateButton = createPushButton(buttons, "Dupli&cate...");
		fDuplicateButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(final Event evt) {
				duplicateFlexeLintConfiguration();
			}
		});

		fRemoveButton = createPushButton(buttons, "&Remove");
		fRemoveButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(final Event evt) {
				removeFlexeLintConfigurations();
			}
		});

		enableButtons();
	}

	private static void createSpacer(final Composite parent) {
		final Label lbl = new Label(parent, SWT.NONE);
		final GridData gd4 = new GridData(GridData.FILL_HORIZONTAL);
		final Layout layout1 = parent.getLayout();
		if (layout1 instanceof GridLayout) {
			gd4.horizontalSpan = ((GridLayout) parent.getLayout()).numColumns;
		}
		gd4.heightHint = 1;
		lbl.setLayoutData(gd4);
	}

	private static Button createPushButton(final Composite parent, final String label) {
		final Button button = new Button(parent, SWT.PUSH);
		button.setFont(parent.getFont());
		button.setText(label);

		final GridData gd = new GridData();
		button.setLayoutData(gd);

		final GC gc = new GC(button.getFont().getDevice());
		gc.setFont(button.getFont());
		final FontMetrics fontMetrics = gc.getFontMetrics();
		gc.dispose();

		final int widthHint1 = Dialog.convertHorizontalDLUsToPixels(fontMetrics, IDialogConstants.BUTTON_WIDTH);
		gd.widthHint = Math.max(widthHint1, button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
		gd.horizontalAlignment = GridData.FILL;

		return button;
	}

	private void initializeListOfConfiguredFlexeLints() {

		final IPreferenceStore prefs = getPreferenceStore();

		final ArrayList<ConfigurationEntry> list = new ArrayList<ConfigurationEntry>();

		for (final String id : getConfigIds()) {
			list.add(ConfigurationEntry.fromId(id, prefs));
		}

		displayConfigurations(list);

		setDefaultConfigCheckMark(ConfigurationEntry.fromId(prefs.getString(PreferenceConstants.LINT_DEFAULT_CONFIG), prefs));

	}

	public void setPreferenceStore(final IPreferenceStore prefStore) {
		this.prefStore = prefStore;
		initializeListOfConfiguredFlexeLints();
	}

	private IPreferenceStore getPreferenceStore() {
		return prefStore;
	}

	private void duplicateFlexeLintConfiguration() {
		final Iterator<?> it = ((IStructuredSelection) fFlexeLintsList.getSelection()).iterator();

		final ArrayList<ConfigurationEntry> newEntries = new ArrayList<ConfigurationEntry>();
		while (it.hasNext()) {

			final ConfigurationEntry s = (ConfigurationEntry) it.next();

			final String createNewConfigurationId = WorkspaceConfiguration.createNewConfigurationId();

			/* @formatter:off */
			final ConfigurationEntry config =
					new ConfigurationEntry(createNewConfigurationId,
							s.getName(),
							s.getCompilerConfig(),
							s.getCompilerConfigHeader(),
							s.getMakeFile(),
							s.getDocumentationFile(),
							s.getExecutable(),
							s.getFileExtensions());
			/* @formatter:on */

			final SingleConfigurationEntryDialog dialog = new SingleConfigurationEntryDialog(createNewConfigurationId, getPreferenceStore(), config);

			if (openFlexeLintConfigurationDialogWizard(dialog) == Window.OK) {
				addNewFlexeLintConfiguration(dialog.getConfiguration());
			}
		}
		if (newEntries.size() > 0) {
			fFlexeLintsList.setSelection(new StructuredSelection(newEntries.toArray()));
		} else {
			fFlexeLintsList.setSelection(fFlexeLintsList.getSelection());
		}
	}

	/**
	 * Fire current selection
	 */
	private void fireSelectionChanged() {
		final SelectionChangedEvent event = new SelectionChangedEvent(this, getSelection());
		final Object[] listeners = fSelectionListeners.getListeners();
		for (final Object listener2 : listeners) {
			final ISelectionChangedListener listener = (ISelectionChangedListener) listener2;
			listener.selectionChanged(event);
		}
	}

	/**
	 * Sorts by name.
	 */
	private void sortByName() {
		fFlexeLintsList.setComparator(new ViewerComparator() {
			@Override
			public int compare(final Viewer viewer, final Object e1, final Object e2) {
				if ((e1 instanceof ConfigurationEntry) && (e2 instanceof ConfigurationEntry)) {
					final ConfigurationEntry left = (ConfigurationEntry) e1;
					final ConfigurationEntry right = (ConfigurationEntry) e2;
					return left.getName().compareToIgnoreCase(right.getName());
				}
				return super.compare(viewer, e1, e2);
			}

			@Override
			public boolean isSorterProperty(final Object element, final String property) {
				return true;
			}
		});
		// fSortColumn = 1;
	}

	/**
	 * Sorts by location.
	 */
	private void sortByLocation() {
		fFlexeLintsList.setComparator(new ViewerComparator() {
			@Override
			public int compare(final Viewer viewer, final Object e1, final Object e2) {
				if ((e1 instanceof ConfigurationEntry) && (e2 instanceof ConfigurationEntry)) {
					final ConfigurationEntry left = (ConfigurationEntry) e1;
					final ConfigurationEntry right = (ConfigurationEntry) e2;
					return left.getInstallLocation().getAbsolutePath().compareToIgnoreCase(right.getInstallLocation().getAbsolutePath());
				}
				return super.compare(viewer, e1, e2);
			}

			@Override
			public boolean isSorterProperty(final Object element, final String property) {
				return true;
			}
		});
	}

	/**
	 * Enables the buttons based on selected items counts in the viewer
	 */
	private void enableButtons() {
		final IStructuredSelection selection = (IStructuredSelection) fFlexeLintsList.getSelection();
		final int selectionCount = selection.size();
		fEditButton.setEnabled(selectionCount == 1);
		fDuplicateButton.setEnabled(selectionCount > 0);
		if (selectionCount > 0 && selectionCount < fFlexeLintsList.getTable().getItemCount()) {
			fRemoveButton.setEnabled(true);
		} else {
			fRemoveButton.setEnabled(false);
		}
	}

	/**
	 * Returns this block's control
	 *
	 * @return control
	 */
	public Control getControl() {
		return fControl;
	}

	protected void displayConfigurations(final Collection<ConfigurationEntry> list) {
		fFlexeLintConfigurations.clear();
		fFlexeLintConfigurations.addAll(list);
		fFlexeLintsList.setInput(fFlexeLintConfigurations);
		fFlexeLintsList.refresh();
	}

	private int openFlexeLintConfigurationDialogWizard(final SingleConfigurationEntryDialog entry) {
		final Wizard wizard = new Wizard() {

			{
				addPage(entry);
			}

			@Override
			public boolean performFinish() {
				return entry.performOk();
			}
		};

		return new WizardDialog(getShell(), wizard).open();
	}

	private Collection<String> getConfigIds() {
		final IPreferenceStore preferenceStore = getPreferenceStore();
		final String ids = preferenceStore.getString(PreferenceConstants.LINT_CONFIG_IDS);
		if (ids.isEmpty()) {
			return Collections.emptyList();
		} else {
			return Arrays.asList(ids.split(":"));
		}
	}

	private void setConfigIds(final Collection<String> ids) {
		getPreferenceStore().setValue(PreferenceConstants.LINT_CONFIG_IDS, StringUtil.join(":", ids));
	}

	private void addConfigId(final String id) {
		final ArrayList<String> list = new ArrayList<String>();
		list.addAll(getConfigIds());
		list.add(id);
		setConfigIds(list);
	}

	private void removeConfigId(final String id) {
		final ArrayList<String> list = new ArrayList<String>();
		list.addAll(getConfigIds());
		list.remove(id);
		setConfigIds(list);
	}

	private void addNewFlexeLintConfiguration(final ConfigurationEntry config) {
		addConfigId(config.getUniqueId());
		fFlexeLintConfigurations.add(config);
		fFlexeLintsList.refresh();
		fFlexeLintsList.setSelection(new StructuredSelection(config));
		if (noConfigurationIsDefault()) {
			checkFlexeLintConfiguration(fFlexeLintsList.getElementAt(0));
			setCheckedConfiguration(config);
		}
	}

	private void editFlexeLintConfiguration() {
		final IStructuredSelection selection = (IStructuredSelection) fFlexeLintsList.getSelection();

		final ConfigurationEntry config = (ConfigurationEntry) selection.getFirstElement();
		if (config == null) {
			return;
		}

		final SingleConfigurationEntryDialog entry = new SingleConfigurationEntryDialog(config.getUniqueId(), getPreferenceStore(), config);

		if (openFlexeLintConfigurationDialogWizard(entry) == Window.OK) {
			final int index = fFlexeLintConfigurations.indexOf(config);
			fFlexeLintConfigurations.remove(index);
			final ConfigurationEntry result = entry.getConfiguration();
			fFlexeLintConfigurations.add(index, result);
			fFlexeLintsList.refresh();
			fFlexeLintsList.setSelection(new StructuredSelection(result));
		}
	}

	private void removeFlexeLintConfigurations() {
		final IStructuredSelection selection = (IStructuredSelection) fFlexeLintsList.getSelection();
		final IStructuredSelection prev = (IStructuredSelection) getSelection();
		final Iterator<?> iter = selection.iterator();

		while (iter.hasNext()) {
			final ConfigurationEntry config = (ConfigurationEntry) iter.next();
			fFlexeLintConfigurations.remove(config);
			removeConfigId(config.getUniqueId());
		}

		fFlexeLintsList.refresh();
		final IStructuredSelection curr = (IStructuredSelection) getSelection();
		if (!curr.equals(prev)) {
			if (curr.size() == 0 && !fFlexeLintConfigurations.isEmpty()) {
				// pick a default automatically
				setSelection(new StructuredSelection(fFlexeLintConfigurations.get(0)));
			} else {
				fireSelectionChanged();
			}
		}
	}

	protected void addNewConfiguration() {

		final String newConfigurationId = WorkspaceConfiguration.createNewConfigurationId();
		final IPreferenceStore preferenceStore = getPreferenceStore();

		if (LintSetupWizard.open(newConfigurationId, getPreferenceStore()) == Window.OK) {
			addNewFlexeLintConfiguration(ConfigurationEntry.fromId(newConfigurationId, preferenceStore));
		}

		return;
	}

	private Shell getShell() {
		return getControl().getShell();
	}

	public void setCheckedConfiguration(final ConfigurationEntry config) {
		setDefaultConfigCheckMark(config);
		refreshLintConfigurations(config);
	}

	private void refreshLintConfigurations(final ConfigurationEntry config) {
		if (config != null) {
			new BuildAllLintConfigsJob().schedule();
		}
	}

	private void setDefaultConfigCheckMark(final ConfigurationEntry config) {
		if (config == null) {
			setSelection(new StructuredSelection());
		} else {
			getPreferenceStore().setValue(PreferenceConstants.LINT_DEFAULT_CONFIG, config.getUniqueId());
			setSelection(new StructuredSelection(config));
		}
	}

	public ConfigurationEntry getCheckedConfiguration() {
		final Object[] objects = fFlexeLintsList.getCheckedElements();
		if (objects.length == 0) {
			return null;
		}
		return (ConfigurationEntry) objects[0];
	}

	public void setEnabled(final boolean enabled) {
		fControl.setEnabled(enabled);
		fAddButton.setEnabled(enabled);
		fEditButton.setEnabled(enabled);
		fDuplicateButton.setEnabled(enabled);
		fRemoveButton.setEnabled(enabled);
		fTable.setEnabled(enabled);
	}
}
