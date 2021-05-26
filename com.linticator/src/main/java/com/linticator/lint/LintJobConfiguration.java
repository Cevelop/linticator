package com.linticator.lint;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

import com.linticator.config.LaunchConfig;
import com.linticator.config.PluginConfig;
import com.linticator.config.ProjectConfig;
import com.linticator.view.console.LintConsole;

public class LintJobConfiguration {
	private final ProjectConfig projectConfig;
	private final LaunchConfig launchConfig;

	public LintJobConfiguration(IProject project) {
		this(new ProjectConfig(project), project);
	}
	
	public LintJobConfiguration(IProject project, LaunchConfig launchConfig) {
		this(new ProjectConfig(project), project, launchConfig);
	}

	public LintJobConfiguration(PluginConfig pluginConfig, IProject project) {
		this(pluginConfig, project, new LaunchConfig());
	}
	
	public LintJobConfiguration(PluginConfig pluginConfig, IProject project, LaunchConfig launchConfig) {
		this.launchConfig = launchConfig;
		projectConfig = new ProjectConfig(project);
	}

	public AbstractLintRunner createLintRunner() {
		return new LintProjectRunner(projectConfig, launchConfig, LintConsole.findConsole());
	}

	public AbstractLintRunner createLintRunner(IProject project, IFile file) {
		return new LintFileRunner(projectConfig, LintConsole.findConsole(), file);
	}

	public PluginConfig getPluginConfig() {
		return projectConfig;
	}

	public ProjectConfig getProjectConfig() {
		return projectConfig;
	}

	public LaunchConfig getLaunchConfig() {
		return launchConfig;
	}

}
