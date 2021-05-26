package com.linticator.quickfixes.includetypeinfo;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;

import com.linticator.quickfixes.LinticatorQuickfix;

public class IncludeTypeInfo extends LinticatorQuickfix {

	public IncludeTypeInfo(final IMarker marker, final int code) {
		super("Include <typeinfo>", marker, code);
	}

	@Override
	protected boolean runOnFile(final IMarker marker, final IFile file) throws Exception {
		final TextFileChange change = new TextFileChange("addTypeInfo", file); //$NON-NLS-1$
		change.setEdit(new InsertEdit(0, "#include <typeinfo>" //$NON-NLS-1$
				+ System.getProperty("line.separator"))); //$NON-NLS-1$
		change.perform(new NullProgressMonitor());
		return true;
	}
}