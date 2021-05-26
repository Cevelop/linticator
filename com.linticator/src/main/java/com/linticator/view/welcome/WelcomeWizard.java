package com.linticator.view.welcome;

import com.linticator.config.WorkspaceConfiguration;
import com.linticator.view.welcome.pages.LintConfigurationPage;
import com.linticator.view.welcome.pages.WelcomePage;
import com.linticator.view.welcome.pages.testconfig.TestConfigurationPage;

public class WelcomeWizard extends TestConfigWizard {

	@Override
	public void addPages() {
		addPage(new WelcomePage());
		addPage(new LintConfigurationPage());
		testConfigPage = new TestConfigurationPage("Welcome to Linticator");
		addPage(testConfigPage);
	}

	@Override
	public boolean performCancel() {
		WorkspaceConfiguration.disableAskingForConfiguration();
		return super.performCancel();
	}

	@Override
	public boolean performFinish() {
		
		return super.performFinish();
	}
}