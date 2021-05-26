package com.linticator;

import org.eclipse.cdt.core.templateengine.TemplateCore;
import org.eclipse.cdt.core.templateengine.process.ProcessArgument;
import org.eclipse.cdt.core.templateengine.process.ProcessFailureException;
import org.eclipse.cdt.core.templateengine.process.ProcessRunner;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;



public class AddLintNatureProcessRunner extends ProcessRunner {

	private static final String PROJECT_PARAM_NAME = "project"; //$NON-NLS-1$

	@Override
	public void process(TemplateCore template, ProcessArgument[] args, String processId, IProgressMonitor monitor)
	throws ProcessFailureException {
		if(args.length !=1)
			throw new ProcessFailureException(getArgumentsMismatchMessage(args));

		String name = ""; //$NON-NLS-1$
		for (ProcessArgument processArgument : args) {
			if(processArgument.getName().equals(PROJECT_PARAM_NAME)) {
				name = processArgument.getSimpleValue();
			}
		}
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(name);
		try {
			LintNature.addLintNature(project, monitor);
		} catch (CoreException e) {
			throw new ProcessFailureException(e.getLocalizedMessage(), e);
		}

	}

}
