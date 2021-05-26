package com.linticator.lint.configurator;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.cdt.core.cdtvariables.CdtVariableException;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.osgi.framework.Bundle;

import com.linticator.Linticator;
import com.linticator.base.FileUtil;
import com.linticator.base.StringUtil;
import com.linticator.config.GccPluginConfig;
import com.linticator.config.PluginConfig;
import com.linticator.config.ProjectConfig;
import com.linticator.config.WorkspaceConfiguration;
import com.linticator.view.console.LintConsole;

public class BuildLintProjectConfigJob extends Job {

	public static final String BUILD_LINT_CONFIG_JOB_FAMILY = "build.lint.config.job"; //$NON-NLS-1$
	protected ProjectConfig config;
	protected IProject project;

	public BuildLintProjectConfigJob(final IProject project) {
		super("Build Lint config for " + project.getName());
		config = new ProjectConfig(project);
		setRule(project);
		this.project = project;
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		return tryToUpdateConfig(monitor);
	}

	public IStatus tryToUpdateConfig(final IProgressMonitor monitor) {
		if (WorkspaceConfiguration.hasDefaultConfiguration()) {
			try {
				updateFormatFile();
				createAndGetConfigFolder();
			} catch (final IOException e) {
				return new Status(IStatus.ERROR, Linticator.PLUGIN_ID, e.getMessage());
			} catch (final CoreException e) {
				return new Status(IStatus.ERROR, Linticator.PLUGIN_ID, e.getMessage());
			}

			try {
				PluginConfig underlyingConfig = config.getUnderlyingConfig();
				if (underlyingConfig instanceof GccPluginConfig && !((GccPluginConfig) underlyingConfig).getMakeFile().isEmpty()) {
					return runMakeForGccConfig(monitor, (GccPluginConfig) underlyingConfig);
				}
			} catch (final CdtVariableException e) {
				return new Status(IStatus.ERROR, Linticator.PLUGIN_ID, e.getMessage());
			}
		}

		return new Status(IStatus.OK, Linticator.PLUGIN_ID, "OK"); //$NON-NLS-1$
	}

	private Status runMakeForGccConfig(final IProgressMonitor monitor, final GccPluginConfig config) {
		try {
			final IPath compMakeFile = config.getMakeFile();
			final IFolder workingDir = createAndGetConfigFolder();

			final String[] command = { "make", "-f", compMakeFile.toOSString() }; //$NON-NLS-1$ //$NON-NLS-2$

			LintConsole.findConsole().print("Building the lint configuration for GCC:\n  ");
			LintConsole.findConsole().print(StringUtil.join(" ", (Object[]) command) + "\n\n");

			final String[] envp = null;

			Process process = null;
			try {
				process = Runtime.getRuntime().exec(command, envp, workingDir.getLocation().toFile());
			} catch (final IOException e) {
				String errorMessage = "An error occurred when building the configuration.";

				if (e.getMessage().contains("The system cannot find the file specified")) {
					errorMessage += " Please make sure that 'make', 'g++', etc. are in your environment PATH.";
				}

				return new Status(IStatus.ERROR, Linticator.PLUGIN_ID, errorMessage, e);
			}

			final StringBuilder errorOut = new StringBuilder();

			if (doWaitFor(process, errorOut) != 0)
				return new Status(IStatus.ERROR, Linticator.PLUGIN_ID, errorOut.toString());

			workingDir.refreshLocal(IResource.DEPTH_INFINITE, monitor);

		} catch (final CoreException e) {
			return new Status(IStatus.ERROR, Linticator.PLUGIN_ID, e.getMessage());
		}

		return new Status(IStatus.OK, Linticator.PLUGIN_ID, "OK"); //$NON-NLS-1$
	}

	private static int doWaitFor(final Process p, final StringBuilder errorOutput) {
		// Oracle Bug #4254231
		int exitValue = -1;

		try {
			// final AbstractLintConsole console = LintConsole.findConsole();

			final InputStream in = p.getInputStream();
			final InputStream err = p.getErrorStream();

			boolean finished = false;

			while (!finished) {

				try {
					while (in.available() > 0) {
						// console.print("" + (char) in.read());
						in.read();
					}

					while (err.available() > 0) {
						// console.print("" + (char) err.read());
						errorOutput.append((char) err.read());
					}

					exitValue = p.exitValue();
					finished = true;
				} catch (final IllegalThreadStateException e) {
					Thread.sleep(100);
				}
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return exitValue;
	}

	@Override
	public boolean belongsTo(final Object family) {
		return family.equals(BUILD_LINT_CONFIG_JOB_FAMILY);
	}

	protected IFolder createAndGetConfigFolder() throws CoreException {
		final IFolder folder = config.getConfigurationDirectory(project);
		if (!folder.exists()) {
			folder.create(true, true, new NullProgressMonitor());
		}
		folder.setDerived(true, new NullProgressMonitor());
		return folder;
	}

	private static void updateFormatFile() throws IOException {
		final Bundle bundle = Linticator.getDefault().getBundle();
		final Path path = new Path("resources/lint/format.lnt");
		final URL url = FileLocator.toFileURL(FileLocator.find(bundle, path, null));
		final String outputFormatContent = FileUtil.readFile(url);
		FileUtil.writeFile(Linticator.getFormatFile().toFile(), outputFormatContent);
	}
}
