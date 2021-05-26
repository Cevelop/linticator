package com.linticator.lint;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import com.linticator.markers.Message;

public class LintFileJob extends LintJob {

	private final IFile file;

	public LintFileJob(final IFile file, final IProject project, final LintJobConfiguration config) {
		super("Lint File Job: " + file.getName(), project, config); //$NON-NLS-1$
		this.file = file;

		// Mirko: We need to set the rule to project because we have to refresh the configuration
		//        directory, which apparently requires the whole project, so this does not work:
		//        setRule(MultiRule.combine(file, config.getPluginConfig().getConfigurationDirectory(project)));
		setRule(project);
	}

	@Override
	void runLint(final IProgressMonitor monitor) throws CoreException, IOException {
		Message.eraseLintMarkers(file);
		getConfig().getProjectConfig().updateConfig(new IResource[] { file });
		final AbstractLintRunner runner = getConfig().createLintRunner(getProject(), file);
		runner.run(monitor);
	}
}
