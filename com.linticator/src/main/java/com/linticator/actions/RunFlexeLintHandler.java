package com.linticator.actions;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.linticator.LintNature;
import com.linticator.lint.LintJob;
import com.linticator.lint.LintProjectJob;

public class RunFlexeLintHandler extends AbstractHandler {

	public RunFlexeLintHandler() {
	}

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {

		for (final IProject project : getCurrentlyActiveFlexeLintProjects()) {
			final LintJob job = new LintProjectJob(project);
			job.schedule();
		}

		return null;
	}

	public static Collection<IProject> getCurrentlyActiveFlexeLintProjects() {

		final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

		final ISelection selection = window.getActivePage().getSelection();

		final Collection<IProject> flexeLintProjects = new LinkedHashSet<IProject>();

		boolean isProjectViewSelection = selection instanceof IStructuredSelection;
		
		if (isProjectViewSelection) {
			final IStructuredSelection structuredSelection = (IStructuredSelection) selection;

			for (final Object object : structuredSelection.toList()) {
				final IProject project = getProject(object);
				if (project != null && isOpenFlexeLintProject(project)) {
					flexeLintProjects.add(project);
				} else if (project != null) {
					if (askEnableLint(project)) {
						enableLint(project);
					}
				}
			}
		} else {
			final IProject project = getActiveProject();
			if (project != null) {
				if (isOpenFlexeLintProject(project)) {
					return Arrays.asList(project);
				} else {
					if (askEnableLint(project)) {
						enableLint(project);
					}
				}
			}
		}

		return flexeLintProjects;
	}

	private static void enableLint(final IProject project) {
		try {
			LintNature.addLintNature(project, new NullProgressMonitor());
		} catch (final CoreException e) {
		}
	}

	private static boolean askEnableLint(final IProject project) {
		return MessageDialog.openQuestion(null, "Enable Linticator?", "Linticator is not enabled for '" + project.getName() + "'. Do you want to enable it?");
	}

	private static IProject getActiveProject() {
		final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			final IWorkbenchPage page = window.getActivePage();
			if (page != null) {
				final IEditorPart editor = page.getActiveEditor();
				if (editor != null) {
					final IEditorInput input = editor.getEditorInput();
					if (input instanceof IFileEditorInput) {
						return ((IFileEditorInput) input).getFile().getProject();
					}
				}
			}
		}
		return null;
	}

	private static IProject getProject(final Object object) {

		if (!(object instanceof IAdaptable))
			return null;

		final IAdaptable adaptable = (IAdaptable) object;

		final Object adapter = adaptable.getAdapter(IResource.class);

		if (adapter == null)
			return null;

		return ((IResource) adapter).getProject();
	}

	private static boolean isOpenFlexeLintProject(final IProject project) {
		try {
			return project != null && project.isOpen() && project.hasNature(LintNature.NATURE_ID);
		} catch (final CoreException e) {
			return false;
		}
	}
}
