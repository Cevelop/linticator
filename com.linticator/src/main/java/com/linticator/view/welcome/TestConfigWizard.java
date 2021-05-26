package com.linticator.view.welcome;

import org.eclipse.jface.wizard.Wizard;

import com.linticator.view.welcome.pages.testconfig.TestConfigurationPage;

public class TestConfigWizard extends Wizard {

	TestConfigurationPage testConfigPage;

	public TestConfigWizard() {
		super();
		setWindowTitle("Test Linticator Configuration");
		setNeedsProgressMonitor(false);
	}

	@Override
	public void addPages() {
		testConfigPage = new TestConfigurationPage("Linticator Configuration");
		addPage(testConfigPage);
	}

	@Override
	public boolean performCancel() {
		deleteCreatedProjectIfEnabled();
		return true;
	}

	@Override
	public boolean performFinish() {
		deleteCreatedProjectIfEnabled();
		return true;
	}

	private void deleteCreatedProjectIfEnabled() {
		if (testConfigPage != null) {
			testConfigPage.deleteCreatedProjectIfEnabled();
		}
	}
}