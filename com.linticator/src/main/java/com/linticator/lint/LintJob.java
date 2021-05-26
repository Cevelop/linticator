package com.linticator.lint;

import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.linticator.Linticator;
import com.linticator.lint.configurator.BuildLintProjectConfigJob;

public abstract class LintJob extends Job {

	private final IProject project;
	private final LintJobConfiguration config;

	public LintJob(final String name, final IProject project, final LintJobConfiguration config) {
		super(name);
		this.project = project;
		this.config = config;
	}

	private void buildConfigIfNecessary(final IProgressMonitor monitor) {
		final Job[] configBuildJobs = Job.getJobManager().find(BuildLintProjectConfigJob.BUILD_LINT_CONFIG_JOB_FAMILY);
		try {
			if (hasActiveConfigurationJobs(configBuildJobs)) {
				waitForConfiguratiobJobsToFinish(configBuildJobs);
			} else {
				rebuildConfiguration(monitor);
			}
		} catch (final InterruptedException e) {
		}
	}

	private void rebuildConfiguration(final IProgressMonitor monitor) throws InterruptedException {
		final BuildLintProjectConfigJob job = new BuildLintProjectConfigJob(getProject());
		// we don't run the job because it could deadlock on Eclipe Ganymede where yieldRule
		// is not available.
		// TODO Ganymede no longer supported
		job.tryToUpdateConfig(monitor);
	}

	private void waitForConfiguratiobJobsToFinish(final Job[] jobs) throws InterruptedException {
		for (final Job job : jobs) {
			job.join();
		}
	}

	private boolean hasActiveConfigurationJobs(final Job[] configBuildJobs) {
		return configBuildJobs.length != 0;
	}

	private void refreshConfigurationDirectory(final IProgressMonitor monitor) {
		try {
			config.getPluginConfig().getConfigurationDirectory(config.getProjectConfig().getProject()).refreshLocal(IResource.DEPTH_INFINITE, monitor);
		} catch (final CoreException Ignored) {
			// Refreshing failed, this is not fatal.
		}
	}

	protected LintJobConfiguration getConfig() {
		return config;
	}

	protected IProject getProject() {
		return project;
	}

	protected Status createStatus(final Exception e) {
		return new Status(IStatus.ERROR, Linticator.PLUGIN_ID, e.getLocalizedMessage(), e);
	}

	abstract void runLint(final IProgressMonitor monitor) throws CoreException, IOException;

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		try {
			buildConfigIfNecessary(monitor);
			refreshConfigurationDirectory(monitor);
			runLint(monitor);
		} catch (final IOException e) {
			return createStatus(e);
		} catch (final CoreException e) {
			return createStatus(e);
		}
		return new Status(IStatus.OK, Linticator.PLUGIN_ID, "ok"); //$NON-NLS-1$
	}

}