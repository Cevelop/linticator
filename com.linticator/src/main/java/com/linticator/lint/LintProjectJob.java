package com.linticator.lint;

import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import com.linticator.config.LaunchConfig;
import com.linticator.markers.Message;

public class LintProjectJob extends LintJob {

	public LintProjectJob(final IProject project) {
		super("Run Lint on project: " + project.getName(), project, new LintJobConfiguration(project));
		setRule(project);
	}

	public LintProjectJob(final IProject project, LaunchConfig launchConfig) {
		super("Run Lint on project: " + project.getName(), project, new LintJobConfiguration(project, launchConfig));
		setRule(project);
	}
	
	private void removeMarkers() throws CoreException {
		getProject().accept(new IResourceVisitor() {

			@Override
			public boolean visit(final IResource resource) throws CoreException {
				Message.eraseLintMarkers(resource);
				Message.eraseLintLibraryMarkers(resource);
				return true;
			}
		});
	}

	@Override
	void runLint(final IProgressMonitor monitor) throws CoreException, IOException {
		final AbstractLintRunner runner = getConfig().createLintRunner();
		getConfig().getProjectConfig().updateConfig(new IResource[] { getProject() });
		removeMarkers();
		runner.run(monitor);
	}
}
