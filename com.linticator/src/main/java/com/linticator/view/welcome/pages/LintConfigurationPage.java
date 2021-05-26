package com.linticator.view.welcome.pages;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import com.linticator.base.HelpUtil;
import com.linticator.config.WorkspaceConfiguration;
import com.linticator.view.preferences.helpers.PropAndPrefHelper;
import com.linticator.view.preferences.uicomponents.AvailableConfigurationsComposite;

public class LintConfigurationPage extends WizardPage {
	public LintConfigurationPage() {
		super("Initial Linticator Configuration");
	}

	@Override
	public void createControl(final Composite parent) {

		setTitle("Welcome to Linticator");
		setMessage("Linticator Configuration");

		final Composite composite = new Composite(parent, SWT.NONE);

		final GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);

		final Label l = new Label(composite, SWT.NONE | SWT.WRAP);
		l.setFont(composite.getFont());
		l.setText("Before Linticator can be used, you need to configure at least one Flexe/Pc-Lint installation.");
		final GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		gd.widthHint = 300;
		l.setLayoutData(gd);

		final Label lbl = new Label(composite, SWT.NONE);
		final GridData gd1 = new GridData(GridData.FILL_HORIZONTAL);
		gd1.horizontalSpan = layout.numColumns;
		gd1.heightHint = 1;
		lbl.setLayoutData(gd1);

		final AvailableConfigurationsComposite flexeLintConfigurationsBlock = new AvailableConfigurationsComposite();
		flexeLintConfigurationsBlock.createControl(composite);
		flexeLintConfigurationsBlock.setPreferenceStore(new PropAndPrefHelper().getWorkspacePreferences());
		final Control control = flexeLintConfigurationsBlock.getControl();
		final GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 1;
		control.setLayoutData(data);

		flexeLintConfigurationsBlock.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				pageComplete();
			}
		});

		setControl(composite);
		pageComplete();
		HelpUtil.setFlexeLintConfigurationHelp(getControl());
	}

	private void pageComplete() {
		setPageComplete(WorkspaceConfiguration.hasDefaultConfiguration());
	}
}