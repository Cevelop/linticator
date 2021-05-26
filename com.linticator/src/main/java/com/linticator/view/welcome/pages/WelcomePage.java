package com.linticator.view.welcome.pages;

import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.linticator.Linticator;
import com.linticator.base.HelpUtil;

public class WelcomePage extends WizardPage {
	public WelcomePage() {
		super("Welcome to Linticator");
	}

	@Override
	public void createControl(final Composite parent) {

		setTitle("Welcome to Linticator");
		setMessage("Using Linticator");

		final Composite composite = new Composite(parent, SWT.NONE);

		final GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);

		addText(composite,
				"This wizard will guide you through the configuration of Linticator.\n\n"
						+ "To enable Linticator for a CDT project, open the project's context menu and select Linticator → Enable Linticator:");

		addImage(composite, "/resources/image/enable_linticator.png");

		addText(composite,
				"To lint the full project, you can either use \"Run Linticator on Project\" from the Linticator context menu or use the toolbar action:");

		addImage(composite, "/resources/image/run_linticator.png");

		addText(composite, "Linticator automatically runs Lint when a source file is changed.");

		setControl(composite);
		setPageComplete(true);
		HelpUtil.setFlexeLintConfigurationHelp(getControl());
	}

	private void addImage(final Composite composite, final String file) {
		final URL url = Linticator.getDefault().getBundle().getEntry(file);
		final Label imageLabel = new Label(composite, SWT.NONE);
		imageLabel.setImage(ImageDescriptor.createFromURL(url).createImage());
		imageLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private void addText(final Composite composite, final String text) {
		final Label l = new Label(composite, SWT.NONE | SWT.WRAP);
		l.setFont(composite.getFont());
		l.setText(text);
		final GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.widthHint = 300;
		l.setLayoutData(layoutData);
	}
}