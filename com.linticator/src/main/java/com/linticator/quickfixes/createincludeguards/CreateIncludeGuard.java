package com.linticator.quickfixes.createincludeguards;

import java.io.IOException;
import java.net.URI;

import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;

import com.linticator.base.FileUtil;
import com.linticator.quickfixes.LinticatorQuickfix;

public class CreateIncludeGuard extends LinticatorQuickfix {

	public CreateIncludeGuard(final IMarker marker, final int code) {
		super("Create include guard", marker, code);
	}

	private static final String NEWLINE = System.getProperty("line.separator"); //$NON-NLS-1$

	@Override
	protected boolean runOnFile(final IMarker marker, final IFile file) throws CoreException, IOException {
		final IASTTranslationUnit unit = getCurrentTranslationUnit(file, marker);
		for (final IASTPreprocessorIncludeStatement include : unit.getIncludeDirectives())
			if (include.isResolved()) {
				final IASTFileLocation location = include.getFileLocation();
				final int line = getLineNumberFromMarker();
				if (location.getFileName().equals(file.getLocation().toOSString())
						&& location.getStartingLineNumber() <= line && location.getEndingLineNumber() >= line) {
					final IPath path = new Path(include.getPath());
					final URI uri = path.toFile().toURI();
					final IFile[] files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(uri);
					for (final IFile header : files) {
						TextFileChange change = new TextFileChange("addTypeInfo", header); //$NON-NLS-1$
						change.setEdit(new InsertEdit(0, getGuardBeginning(header.getName())));
						change.perform(new NullProgressMonitor());
						change = new TextFileChange("addTypeInfo", //$NON-NLS-1$
								header);
						final int offset = getFileLength(header);
						change.setEdit(new InsertEdit(offset, getGuardEnding(header.getName())));
						change.perform(new NullProgressMonitor());
					}
				}
			}
		return true;
	}

	private int getFileLength(final IFile header) throws IOException, CoreException {
		return FileUtil.read(header.getContents()).length();
	}

	private static String getGuardBeginning(final String fileName) {
		final String guardName = toGuardName(fileName);
		final StringBuilder guard = new StringBuilder("#ifndef "); //$NON-NLS-1$
		guard.append(guardName);
		guard.append(NEWLINE);
		guard.append("#define "); //$NON-NLS-1$
		guard.append(guardName);
		guard.append(NEWLINE);
		guard.append(NEWLINE);
		return guard.toString().replaceAll("\\.", "_"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private static String getGuardEnding(final String fileName) {
		final String guardName = toGuardName(fileName);
		final StringBuilder ending = new StringBuilder();
		ending.append(NEWLINE);
		ending.append(NEWLINE);
		ending.append("#endif /* "); //$NON-NLS-1$
		ending.append(guardName);
		ending.append(" */"); //$NON-NLS-1$
		return ending.toString();
	}

	private static String toGuardName(final String name) {
		
		// Copied from CDT's NewSourceFileGenerator#generateIncludeGuardSymbol
		
        //convert to upper case and remove invalid characters
        //eg convert foo.h --> _FOO_H_
        final StringBuffer buf = new StringBuffer();
        // Do not do this, leading underscores are discourage by the std.
        //buf.append('_');
        for (int i = 0; i < name.length(); ++i) {
            final char ch = name.charAt(i);
            if (Character.isLetterOrDigit(ch)) {
                buf.append(Character.toUpperCase(ch));
            } else if (ch == '.' || ch == '_') {
                buf.append('_');
            }
        }
        buf.append('_');
        return buf.toString();
	}
}