package com.linticator.view.welcome.pages.testconfig;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;


public abstract class TestConfigurationComposite extends Composite {
	private final StyledText styledText;
	private final Button btnDeleteTestProject;
	private final Button btnRunTest;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public TestConfigurationComposite(final Composite parent, final int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));

		final Label lblDoYouWant = new Label(this, SWT.NONE);
		lblDoYouWant.setText("Do you want to test your configuration?");
		lblDoYouWant.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));

		btnRunTest = new Button(this, SWT.NONE);
		btnRunTest.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				runTest();
			}
		});
		btnRunTest.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false, 1, 1));
		btnRunTest.setText("Run Tests");

		styledText = new StyledText(this, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		styledText.setEditable(false);
		styledText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		btnDeleteTestProject = new Button(this, SWT.CHECK);
		btnDeleteTestProject.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				deleteTestProject(btnDeleteTestProject.getSelection());
			}
		});
		btnDeleteTestProject.setSelection(true);
		btnDeleteTestProject.setText("Delete test project when done.");

		final Button btnExportLogfile = new Button(this, SWT.NONE);
		btnExportLogfile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				saveLog();
			}
		});
		btnExportLogfile.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnExportLogfile.setText("Export Logfile");

	}

	protected abstract void saveLog();

	protected abstract void deleteTestProject(boolean selection);

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	protected abstract void runTest();

	protected StyledText getOutputArea() {
		return styledText;
	}

	protected Button doDeleteProject() {
		return btnDeleteTestProject;
	}

	public Button getRunButton() {
		return btnRunTest;
	}
}
