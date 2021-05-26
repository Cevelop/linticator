package com.linticator.view.preferences.addnewwizard;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import com.linticator.config.ConfigurationEntry;
import com.linticator.config.PreferenceConstants;
import com.linticator.functional.Function1;
import com.linticator.view.preferences.fieldeditors.FileAndVariablesFieldEditor;

public class LocateLintInstallation extends WizardPage {

	private final String configId;
	private StringFieldEditor execField;
	private StringFieldEditor docField;
	private StringFieldEditor nameField;
	private StringFieldEditor extField;
	private final AtomicBoolean canFinish;
	private IPreferenceStore preferenceStore;

	public LocateLintInstallation(final String configId, IPreferenceStore preferenceStore, final AtomicBoolean canFinish) {
		super("wizardPage");
		this.preferenceStore = preferenceStore;
		this.canFinish = canFinish;
		setTitle("Lint Installation Location");
		setDescription("");
		this.configId = configId;
		setPageComplete(false);
	}
	
	@Override
	public void setVisible(final boolean visible) {
		super.setVisible(visible);
		canFinish.set(false);
		getContainer().updateButtons();
	}

	@Override
	public void createControl(final Composite parent) {
		final Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(3, false));

		final Label label = new Label(container, SWT.NONE);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		label.setText("Please give the location of the following files from your PC-/FlexeLint installation:");

		final Button btnDetectAutomatically = new Button(container, SWT.NONE);
		btnDetectAutomatically.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnDetectAutomatically.setText("Search ...");
		btnDetectAutomatically.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(final Event evt) {
				search();
			}
		});

		final Composite c1 = new Composite(container, SWT.NULL);
		nameField = createNameField(c1, configId + PreferenceConstants.LINT_CONFIG_NAME,
				"Name this configuration");
		nameField.setStringValue("Lint");


		final Composite c2 = new Composite(container, SWT.NULL);
		execField = FileAndVariablesFieldEditor.create(c2, configId + PreferenceConstants.LINT_EXECUTABLE, preferenceStore,
				"Lint executable", this, new Function1<String, Void>() {
					
					@Override
					public Void apply(final String t) {
						saveSettings();
						return null;
					}
				});

		final Composite c3 = new Composite(container, SWT.NULL);
		docField = FileAndVariablesFieldEditor.create(c3, configId + PreferenceConstants.LINT_DOCUMENTATION, preferenceStore,
				"Documentation (msg.txt, msg.xml)", this, new Function1<String, Void>() {
					
					@Override
					public Void apply(final String t) {
						saveSettings();
						return null;
					}
				});

		final Composite c4 = new Composite(container, SWT.NULL);
		extField = createNameField(c4, configId + PreferenceConstants.FILE_EXTENSIONS,
				"Lintable file extensions");
		extField.setStringValue("c cc cpp cxx");

		final int width = docField.getLabelControl(c3).computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
		
		((GridData) nameField.getLabelControl(c1).getLayoutData()).widthHint = width;
		((GridData) execField.getLabelControl(c2).getLayoutData()).widthHint = width;
		((GridData) docField.getLabelControl(c3).getLayoutData()).widthHint = width;
		((GridData) extField.getLabelControl(c4).getLayoutData()).widthHint = width;
	}

	private StringFieldEditor createNameField(final Composite container, final String preferenceConstant, final String displayName) {
		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		final StringFieldEditor fieldEditor = new StringFieldEditor(preferenceConstant, displayName, container);
		fieldEditor.getLabelControl(container).setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		fieldEditor.getTextControl(container).setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		fieldEditor.setEmptyStringAllowed(false);
		fieldEditor.setPage(this);
		fieldEditor.setPreferenceStore(preferenceStore);
		return fieldEditor;
	}
	
	@Override
	public void setErrorMessage(final String newMessage) {
		super.setErrorMessage(newMessage);
		saveSettings();
	}
	
	@Override
	public boolean isPageComplete() {
		
		if(nameField != null) {
			nameField.store();
		}
		
		if(execField != null) {
			execField.store();
		}
		
		if(docField != null) {
			docField.store();
		}
		
		if(extField != null) {
			extField.store();
		}
		
		return nameField.isValid() && execField.isValid() && docField.isValid() && extField.isValid();
	}

	
	protected void search() {

		// choose a root directory for the search
		final DirectoryDialog dialog = new DirectoryDialog(getShell());
		dialog.setMessage("Select a directory to search in:");
		dialog.setText("Directory Selection");
		final String path = dialog.open();
		if (path == null) {
			return;
		}

		// search
		final File rootDir = new File(path);
		final List<File> locations = new ArrayList<File>();

		final IRunnableWithProgress r = new IRunnableWithProgress() {
			@Override
			public void run(final IProgressMonitor monitor) {
				monitor.beginTask("Searching", IProgressMonitor.UNKNOWN);
				if (InstallationDiscovery.isValidLocation(rootDir)) {
					locations.add(rootDir);
				}
				searchRecursively(rootDir, locations, new LinkedHashSet<File>(), monitor);
				monitor.done();
			}
		};

		try {
			new ProgressMonitorDialog(getShell()).run(true, true, r);
		} catch (final InvocationTargetException e) {
			e.printStackTrace();
		} catch (final InterruptedException e) {
			// canceled
			return;
		}

		if (locations.isEmpty()) {
			final String messagePath = path.replaceAll("&", "&&"); // @see bug 29855  //$NON-NLS-1$//$NON-NLS-2$
			MessageDialog.openInformation(getShell(), "Information",
					MessageFormat.format("No FlexeLint installation found at {0}.", new Object[] { messagePath })); //
		} else {
			final ConfigurationEntry entry = InstallationDiscovery.createConfigurationFromLocation(
					locations.get(0), configId);
			execField.setStringValue(entry.getExecutable());
			docField.setStringValue(entry.getDocumentationFile());
			saveSettings();
		}
	}

	private void searchRecursively(final File directory, final List<File> found, final Set<File> ignore,
			final IProgressMonitor monitor) {
		if (monitor.isCanceled()) {
			return;
		}

		final String[] names = directory.list();
		if (names == null) {
			return;
		}
		final List<File> subDirs = new ArrayList<File>();
		for (int i = 0; i < names.length; i++) {
			if (monitor.isCanceled()) {
				return;
			}
			final File file = new File(directory, names[i]);
			try {
				monitor.subTask(MessageFormat.format("Found {0} - Searching {1}",
						new Object[] { Integer.toString(found.size()), file.getCanonicalPath().replaceAll("&", "&&") })); // @see bug 29855 //$NON-NLS-1$ //$NON-NLS-2$
			} catch (final IOException e) {
			}
			if (file.isDirectory() && !ignore.contains(file)) {
				if (monitor.isCanceled()) {
					return;
				}
				if (InstallationDiscovery.isValidLocation(file)) {
					found.add(file);
				} else {
					subDirs.add(file);
				}
			}
		}
		while (!subDirs.isEmpty()) {
			final File subDir = subDirs.remove(0);
			searchRecursively(subDir, found, ignore, monitor);
			if (monitor.isCanceled()) {
				return;
			}
		}
	}

	private void saveSettings() {
		setPageComplete(isPageComplete());
		getContainer().updateButtons();
	}
}
