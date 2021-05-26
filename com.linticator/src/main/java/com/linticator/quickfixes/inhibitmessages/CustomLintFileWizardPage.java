package com.linticator.quickfixes.inhibitmessages;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;

import com.linticator.config.ProjectConfig;

public class CustomLintFileWizardPage extends WizardPage {
	private Text text;
	private Text txtLinticatorAddsYour;
	private Text txtTheLocationOf;
	private final ProjectConfig projectConfig;

	/**
	 * Create the wizard.
	 */
	public CustomLintFileWizardPage(final ProjectConfig projectConfig) {
		super("wizardPage");
		this.projectConfig = projectConfig;
		setTitle("Custom Lint File Wizard");
		setDescription("");
	}

	/**
	 * Create contents of the wizard.
	 * 
	 * @param parent
	 */
	@Override
	public void createControl(final Composite parent) {
		final Composite container = new Composite(parent, SWT.NONE);

		setControl(container);
		container.setLayout(new GridLayout(2, false));

		txtLinticatorAddsYour = new Text(container, SWT.WRAP);
		txtLinticatorAddsYour.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		txtLinticatorAddsYour.setText("Linticator adds your suppressions to a custom Lint file, but no such file has been configured yet.");
		txtLinticatorAddsYour.setEditable(false);
		txtLinticatorAddsYour.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));

		text = new Text(container, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		final Button btnChooseFile = new Button(container, SWT.NONE);
		btnChooseFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				final FileDialog fd = new FileDialog(container.getShell(), SWT.SAVE);
				fd.setText("Custom Lint File Location");
				final String[] filterExt = { "*.lnt" };
				fd.setFilterExtensions(filterExt);
				final String selected = fd.open();
				text.setText(selected);
				try {
					projectConfig.setCustomLintFilePropertyValue(selected);
				} catch (final CoreException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnChooseFile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnChooseFile.setText("Choose File...");

		txtTheLocationOf = new Text(container, SWT.WRAP);
		txtTheLocationOf.setEditable(false);
		txtTheLocationOf.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		txtTheLocationOf.setText("The location of this file can later be changed in the Linticator project properties.");
		txtTheLocationOf.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
	}
}
