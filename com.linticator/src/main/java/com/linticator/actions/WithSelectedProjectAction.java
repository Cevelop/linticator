package com.linticator.actions;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.linticator.functional.Function1;

abstract public class WithSelectedProjectAction implements IWorkbenchWindowActionDelegate {

	public void withProject(final Function1<IProject, Void> f) {
		if (project != null) {
			f.apply(project);
			project = null;
		}
	}

	private IProject project;

	@Override
	public void selectionChanged(final IAction action, final ISelection selection) {
		if (selection instanceof TreeSelection) {
			final TreeSelection treeSel = (TreeSelection) selection;
			final Object firstElement = treeSel.getFirstElement();
			if (firstElement instanceof IProject) {
				project = (IProject) firstElement;
				return;
			} else if (firstElement instanceof ICElement) {
				project = ((ICElement) firstElement).getCProject().getProject();
				return;
			}
		}
		project = null;
	}

	@Override
	public void dispose() {
	}

	@Override
	public void init(final IWorkbenchWindow window) {
	}
}
