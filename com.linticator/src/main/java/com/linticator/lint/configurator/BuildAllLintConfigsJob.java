package com.linticator.lint.configurator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.linticator.LintNature;
import com.linticator.Linticator;
import com.linticator.config.WorkspaceConfiguration;


public class BuildAllLintConfigsJob extends Job {

	public BuildAllLintConfigsJob() {
		super("Build All Lint Configs");
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		if(WorkspaceConfiguration.hasDefaultConfiguration()) {
			IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
			for (IProject project : projects) {
				try {
					if(project.hasNature(LintNature.NATURE_ID)) {
						new BuildLintProjectConfigJob(project).schedule();
					}
				} catch (CoreException e) {
				}
			}
		}
		return new Status(IStatus.OK, Linticator.PLUGIN_ID, "OK");
	}

}
