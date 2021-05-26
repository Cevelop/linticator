package com.linticator.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;

import com.linticator.LintNature;
import com.linticator.functional.Function1;

public class DisableLint extends WithSelectedProjectAction {

	@Override
	public void run(final IAction action) {
		withProject(new Function1<IProject, Void>() {
			@Override
			public Void apply(final IProject p) {
				try {
					LintNature.removeLintNature(p, new NullProgressMonitor());
				} catch (final CoreException e) {
				}
				return null;
			}
		});
	}
}
