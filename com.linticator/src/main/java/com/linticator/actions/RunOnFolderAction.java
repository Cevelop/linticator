package com.linticator.actions;

import java.util.ArrayList;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import com.linticator.functional.Function1;
import com.linticator.lint.LintFolderJob;
import com.linticator.lint.LintJob;

public class RunOnFolderAction extends WithSelectedProjectAction {

	private ArrayList<IResource> selectedPaths;

	@Override
	public void run(final IAction action) {

		if (selectedPaths.isEmpty())
			return;

		withProject(new Function1<IProject, Void>() {

			@Override
			public Void apply(final IProject p) {

				final LintJob job = new LintFolderJob(p, selectedPaths);
				job.schedule();
				return null;
			}
		});
	}

	@Override
	public void selectionChanged(final IAction action, final ISelection selection) {
		super.selectionChanged(action, selection);
		selectedPaths = new ArrayList<IResource>();
		if (selection instanceof IStructuredSelection) {
			extractPathsFromSelection(((IStructuredSelection) selection).toArray());
		}
	}

	private void extractPathsFromSelection(final Object[] selection) {
		for (final Object o : selection) {
			if (o instanceof ICElement) {
				selectedPaths.add(((ICElement) o).getResource());
			}
		}
	}
}
