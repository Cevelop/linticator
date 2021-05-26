package com.linticator.astquickfixes.declarevirtual;

import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclSpecifier;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.linticator.quickfixes.LinticatorQuickfix;

public class DeclareVirtual extends LinticatorQuickfix {

	public DeclareVirtual(final IMarker marker, final int code) {
		super("Declare function virtual", marker, code);
	}

	@Override
	protected boolean runOnFile(final IMarker marker, final IFile file) throws Exception {
		final int line = getLineNumberFromMarker();
		final IASTTranslationUnit unit = getCurrentTranslationUnit(file, marker);
		final IASTSimpleDeclaration method = getReferencedDeclaration(unit, file.getLocation().toOSString(), line);
		final IASTSimpleDeclaration virtualMethod = method.copy();
		((ICPPASTDeclSpecifier) virtualMethod.getDeclSpecifier()).setVirtual(true);
		final ASTRewrite r = ASTRewrite.create(unit);
		r.replace(method, virtualMethod, null);
		r.rewriteAST().perform(new NullProgressMonitor());
		return true;
	}

	private IASTSimpleDeclaration getReferencedDeclaration(final IASTTranslationUnit unit, final String fileName,
			final int line) {
		final FindReferencedFunctionDeclarationVisitor visitor = new FindReferencedFunctionDeclarationVisitor(fileName,
				line);
		unit.accept(visitor);
		return visitor.getDeclaration();
	}
}