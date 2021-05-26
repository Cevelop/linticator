package com.linticator.lint.configurator;

import java.util.Map;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import com.linticator.Linticator;
import com.linticator.config.ProjectConfig;

public class LintConfigBuilder extends IncrementalProjectBuilder {

	@Override
	protected void clean(final IProgressMonitor monitor) throws CoreException {
		super.clean(monitor);
		final IFolder configFolder = getConfigFolder(monitor);
		configFolder.delete(true, false, monitor);
	}

	public static final String ID = Linticator.PLUGIN_ID + ".lintConfigBuilder"; //$NON-NLS-1$

	@SuppressWarnings("rawtypes")
	@Override
	protected IProject[] build(final int kind, final Map args, final IProgressMonitor monitor) throws CoreException {
		if (kind == IncrementalProjectBuilder.FULL_BUILD)
			return fullBuild(monitor);
		return new IProject[] { getProject() };
	}

	private IProject[] fullBuild(final IProgressMonitor monitor) {

		final BuildLintProjectConfigJob job = new BuildLintProjectConfigJob(getProject());
		job.schedule();
		monitor.done();
		return new IProject[] { getProject() };

	}

	private IFolder getConfigFolder(final IProgressMonitor monitor) throws CoreException {
		final IFolder folder = new ProjectConfig(getProject()).getConfigurationDirectory(getProject());
		if (!folder.exists()) {
			folder.create(true, true, monitor);
		}
		folder.setDerived(true, monitor);
		return folder;

	}
}
