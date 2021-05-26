package com.linticator.lint;

import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import com.linticator.Linticator;
import com.linticator.config.ProjectConfig;
import com.linticator.config.WorkspaceConfiguration;
import com.linticator.markers.Message;

public class LintBuilder extends IncrementalProjectBuilder {

	public static final String ID = Linticator.PLUGIN_ID + ".lintBuilder"; //$NON-NLS-1$

	public LintBuilder() {
		super();
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {

		if (WorkspaceConfiguration.hasDefaultConfiguration() && WorkspaceConfiguration.getRunLintAfterBuild()) {
			switch (kind) {
			case IncrementalProjectBuilder.FULL_BUILD:
				return fullBuild(monitor);
			case IncrementalProjectBuilder.AUTO_BUILD:
			case IncrementalProjectBuilder.INCREMENTAL_BUILD:
				return incrementalBuild(monitor);
			}
		}
		return new IProject[] { getProject() };
	}

	private IProject[] incrementalBuild(IProgressMonitor monitor) throws CoreException {
		IResourceDelta delta = getDelta(getProject());
		if (delta == null)
			return fullBuild(monitor);
		final Set<String> ext = new ProjectConfig(getProject()).getFileExtensions();
		delta.accept(new IResourceDeltaVisitor() {

			@Override
			public boolean visit(IResourceDelta delta) throws CoreException {
				IResource re = delta.getResource();
				if (re instanceof IFile) {
					IFile file = (IFile) re;
					if (ext.contains(file.getFileExtension())) {
						runLint(file);
					}
				}
				return true;
			}
		});
		return new IProject[] { getProject() };
	}

	private IProject[] fullBuild(IProgressMonitor monitor) {
		IProject project = getProject();
		runLint();
		return new IProject[] { project };
	}

	@Override
	protected void clean(IProgressMonitor monitor) throws CoreException {
		IProject project = getProject();
		removeAllLintMarkers(project);
	}

	public static void removeAllLintMarkers(IProject project) {
		try {
			Message.eraseLintMarkers(project);
			project.accept(new IResourceVisitor() {

				@Override
				public boolean visit(IResource resource) throws CoreException {
					if (resource instanceof IFile) {
						IFile file = (IFile) resource;
						Message.eraseLintMarkers(file);
					}
					return true;
				}
			});
		} catch (CoreException e) {
		}
	}

	protected void runLint(IFile file) {
		LintJobConfiguration config = new LintJobConfiguration(getProject());
		LintJob job = new LintFileJob(file, getProject(), config);
		job.schedule();
	}

	protected void runLint() {
		LintJob job = new LintProjectJob(getProject());
		job.schedule();
	}

}
