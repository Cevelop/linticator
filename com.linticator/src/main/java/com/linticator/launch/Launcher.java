package com.linticator.launch;

import org.eclipse.cdt.debug.core.ICDTLaunchConfigurationConstants;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.debug.ui.IDebugUIConstants;

import com.linticator.LintNature;
import com.linticator.Linticator;
import com.linticator.base.VariablesUtil;
import com.linticator.config.LaunchConfig;
import com.linticator.lint.LintJob;
import com.linticator.lint.LintProjectJob;

public class Launcher extends LaunchConfigurationDelegate {

	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {

		LaunchConfig linticatorLaunchConfig = new LaunchConfig();

		boolean captureOutput = configuration.getAttribute(DebugPlugin.ATTR_CAPTURE_OUTPUT, true);

		if (captureOutput) {
			String outputFileConfig = configuration.getAttribute(IDebugUIConstants.ATTR_CAPTURE_IN_FILE, (String) null);
			String outputFile = VariablesUtil.resolveWorkspaceVariables(outputFileConfig);
			boolean append = configuration.getAttribute(IDebugUIConstants.ATTR_APPEND_TO_FILE, false);

			linticatorLaunchConfig.setCaptureOutput(true);
			linticatorLaunchConfig.setAppend(append);
			linticatorLaunchConfig.setCaptureFile(outputFile);
		}

		String projectName = configuration.getAttribute(ICDTLaunchConfigurationConstants.ATTR_PROJECT_NAME, (String) null);

		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);

		if (!project.hasNature(LintNature.NATURE_ID)) {
			LintNature.addLintNatureWithoutBuilding(project, monitor);
		}

		if (project != null && project.exists()) {
			LintJob job = new LintProjectJob(project, linticatorLaunchConfig);
			job.schedule();
		} else {
			Linticator.getDefault().handleWarning("Could not launch Linticator. No project named " + projectName + " found.");
		}
	}
}
