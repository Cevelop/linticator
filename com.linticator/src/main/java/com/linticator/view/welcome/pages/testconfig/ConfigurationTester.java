package com.linticator.view.welcome.pages.testconfig;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;

import com.linticator.Linticator;
import com.linticator.config.WorkspaceConfiguration;
import com.linticator.lint.LintJob;
import com.linticator.lint.LintProjectJob;
import com.linticator.lint.configurator.BuildLintProjectConfigJob;
import com.linticator.view.console.AbstractLintConsole;
import com.linticator.view.console.LintConsole;

class ConfigurationTester {

	private final StyledTextPrinter out;
	private final String projectName;
	private final Display display;

	ConfigurationTester(final StyledTextPrinter out, final String projectName, final Display display) {
		this.out = out;
		this.projectName = projectName;
		this.display = display;
	}

	void checkConfiguration(final IProgressMonitor monitor) {
		out.greenprintln("Starting Linticator configuration tests.");

		out.println(Linticator.linticatorVersionInformation());

		out.println("Creating empty test project " + projectName + ".");

		final IProject project;

		try {
			project = Helpers.createEmptyProject(projectName);
		} catch (final Exception e) {
			error(e, "An error occured while creating the project: ");
			return;
		}

		if (monitor.isCanceled())
			return;

		out.println("Converting test project to a CDT project.");
		try {
			Helpers.makeManagedCdtProject(project);
		} catch (final UnsupportedOperationException e) {
			warning("Could not create the CDT test project, expected toolchain not available on this platform. ");
			warning("\nLinticator might still work fine though, if not, please report a bug.");
			return;
		} catch (final Exception e) {
			error(e, "An error occured while converting to CDT: ");
			return;
		}

		if (monitor.isCanceled())
			return;

		out.println("Adding the Linticator Nature to the project.");
		try {
			Helpers.addLinticatorNature(project);
		} catch (final CoreException e) {
			error(e, "An error occured while adding the Linticator nature: ");
			return;
		}
		
		if(WorkspaceConfiguration.hasDefaultConfiguration()) {
			out.println("Lint installation found.");
		} else {
			out.redprintln("No Lint installation found! Please configure Linticator in the Eclipse preferences.");	
			return;		
		}

		if (monitor.isCanceled())
			return;

		out.println("Creating a source file \"main.cpp\" in the project.");
		try {
			Helpers.createMainFile(project);
		} catch (final CoreException e) {
			error(e, "An error occured while creating the source file: ");
			return;
		}

		if (monitor.isCanceled())
			return;

		out.println("Building Linticator config for the project.");

		final AbstractLintConsole console = LintConsole.findConsole();
		console.sendOutputTo(out);

		try {

			if (jobFailed(testConfigJob(project))) {
				out.redprintln("An error occured, your configuration does not seem to be working.");
				return;
			}

			if (monitor.isCanceled())
				return;

			out.println("Running Linticator on the project.");

			if (jobFailed(testLintJob(project))) {
				out.redprintln("An error occured, your configuration does not seem to be working.");
				return;
			}

			if (monitor.isCanceled())
				return;

			out.println("Checking for lint problem markers.");

			display.asyncExec(new Runnable() {

				@Override
				public void run() {
					try {
						final IMarker[] markers = project.findMarkers(null, true, IResource.DEPTH_INFINITE);

						if (markers.length < 1) {
							out.redprintln("No markers were found!");
						} else {
							out.println("The following markers were found: ");
							output(markers);
							out.greenprintln("\nLinticator configuration tests finished successfully.");
						}

					} catch (final CoreException e) {
						error(e, "An error occured while retrieving the markers: ");
					}
				}
			});

		} finally {
			console.stopSendingOutputTo(out);
		}
	}

	private void output(final IMarker[] markers) throws CoreException {
		for (final IMarker m : markers) {
			out.println("  " + m.getAttribute(IMarker.MESSAGE));
		}
	}

	private boolean jobFailed(final IStatus result1) {
		return result1 == null || !result1.isOK();
	}

	private void warning(final String msg) {
		out.boldprintln(msg);
	}
	
	private void error(final Exception e, final String msg) {
		out.redprintln(msg);
		out.printException(e);
	}

	public IStatus testConfigJob(final IProject project) {
		final IStatus[] realStatus = new IStatus[1];

		final BuildLintProjectConfigJob configJob = new BuildLintProjectConfigJob(project) {

			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				realStatus[0] = super.run(monitor);
				out.grayprintln("The result was " + realStatus[0].getMessage());
				return new Status(IStatus.OK, Linticator.PLUGIN_ID, "ok");
			}
		};

		configJob.schedule();

		try {
			configJob.join();
		} catch (final InterruptedException e) {
			error(e, "An error occured while waiting for the configuration builder job: ");
			return null;
		}

		return realStatus[0];
	}

	public IStatus testLintJob(final IProject project) {
		final IStatus[] realStatus = new IStatus[1];

		final LintJob job = new LintProjectJob(project) {

			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				realStatus[0] = super.run(monitor);
				out.grayprintln("The result was " + realStatus[0].getMessage());
				return new Status(IStatus.OK, Linticator.PLUGIN_ID, "ok");
			}
		};

		job.schedule();

		try {
			job.join();
		} catch (final InterruptedException e) {
			error(e, "An error occured while waiting for the lint job: ");
			return null;
		}

		return realStatus[0];
	}
}
