package com.linticator.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;

import com.linticator.functional.Function1;
import com.linticator.lint.LintJob;
import com.linticator.lint.LintProjectJob;

public class RunOnProjectAction extends WithSelectedProjectAction {

	@Override
	public void run(final IAction action) {
		withProject(new Function1<IProject, Void>() {

			@Override
			public Void apply(final IProject p) {
				final LintJob job = new LintProjectJob(p);
				job.schedule();
				return null;
			}
		});
	}
}
