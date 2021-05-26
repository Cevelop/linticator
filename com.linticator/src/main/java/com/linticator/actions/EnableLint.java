package com.linticator.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;

import com.linticator.LintNature;
import com.linticator.functional.Function1;

public class EnableLint extends WithSelectedProjectAction {

	@Override
	public void run(final IAction action) {
		withProject(new Function1<IProject, Void>() {

			@Override
			public Void apply(final IProject p) {

				new WorkspaceJob("Enabling Linticator") {

					@Override
					public IStatus runInWorkspace(final IProgressMonitor monitor) throws CoreException {
						try {
							LintNature.addLintNature(p, monitor);
						} catch (final CoreException e) {
							e.printStackTrace();
						}
						return Status.OK_STATUS;
					}
				}.schedule();

				return null;
			}
		});
	}
}
