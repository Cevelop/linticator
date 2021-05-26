package com.linticator.base;

import org.eclipse.core.resources.IMarker;

public interface ILintMarker extends IMarker {

	public static final String QUICKFIX_LOCATION = "lintQuickfixLocation"; //$NON-NLS-1$
	public static final String PROBLEM_DESCRIPTION = "lintProblemDescription"; //$NON-NLS-1$

}
