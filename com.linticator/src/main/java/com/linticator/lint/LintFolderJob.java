package com.linticator.lint;

import java.io.IOException;
import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import com.linticator.base.StringUtil;
import com.linticator.markers.Message;

public class LintFolderJob extends LintJob {

	private final Collection<IResource> selectedPaths;

	public LintFolderJob(final IProject project, final Collection<IResource> selectedPaths) {
		super("Run Lint on folder: " + StringUtil.join(", ", selectedPaths), project, new LintJobConfiguration(project));
		this.selectedPaths = selectedPaths;
		setRule(project);
	}

	private void removeMarkers(final IResource resource) throws CoreException {
		resource.accept(new IResourceVisitor() {

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
		final AbstractLintRunner runner = createRunnerForSelectedFiles();

		getConfig().getProjectConfig().updateConfig(selectedPaths.toArray(new IResource[0]));
		for (final IResource resource : selectedPaths) {
			removeMarkers(resource);
		}

		runner.run(monitor);
	}

	private AbstractLintRunner createRunnerForSelectedFiles() {
		if (selectedPaths.size() == 1) {
			final IResource resource = selectedPaths.iterator().next();
			if (resource instanceof IFile) {
				final IFile file = (IFile) resource;
				return getConfig().createLintRunner(getProject(), file);
			}
		}
		return getConfig().createLintRunner();
	}
}
