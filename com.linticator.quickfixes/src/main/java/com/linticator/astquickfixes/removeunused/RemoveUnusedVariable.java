package com.linticator.astquickfixes.removeunused;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.linticator.quickfixes.LinticatorQuickfix;

public class RemoveUnusedVariable extends LinticatorQuickfix {

	public RemoveUnusedVariable(final IMarker marker, final int code) {
		super("Remove unused variable", marker, code);
	}

	@Override
	protected boolean runOnFile(final IMarker marker, final IFile file) throws Exception {
		final int line = getLineNumberFromMarker();
		final String variableName = extractVariableNameFromMessage((String) marker.getAttribute(IMarker.MESSAGE));
		final IASTTranslationUnit unit = getCurrentTranslationUnit(file, marker);
		final IASTSimpleDeclaration declaration = getReferencedDeclaration(unit, file.getLocation().toOSString(), line,
				variableName);
		final ASTRewrite r = ASTRewrite.create(unit);
		r.remove(declaration, null);
		r.rewriteAST().perform(new NullProgressMonitor());
		return true;
	}

	private String extractVariableNameFromMessage(final String attribute) {
		final Matcher matcher = Pattern.compile(".* Symbol '(.*?)'.*").matcher(attribute);
		matcher.matches();
		return matcher.group(1);
	}

	private IASTSimpleDeclaration getReferencedDeclaration(final IASTTranslationUnit unit, final String fileName,
			final int line, final String variableName) {
		final FindReferencedDeclarationVisitor visitor = new FindReferencedDeclarationVisitor(fileName, line,
				variableName);
		unit.accept(visitor);
		return visitor.getDeclaration();
	}
}
