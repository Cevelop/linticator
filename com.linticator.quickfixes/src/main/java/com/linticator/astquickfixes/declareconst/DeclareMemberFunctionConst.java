package com.linticator.astquickfixes.declareconst;

import java.io.IOException;

import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.linticator.quickfixes.LinticatorQuickfix;

public class DeclareMemberFunctionConst extends LinticatorQuickfix {

	public DeclareMemberFunctionConst(final IMarker marker, final int code) {
		super("Declare member function const", marker, code);
	}

	@Override
	protected boolean runOnFile(final IMarker marker, final IFile file) throws CoreException, IOException {
		final IProject project = file.getProject();
		final int line = getLineNumberFromMarker();
		final IASTTranslationUnit sourceTranslationUnit = getCurrentTranslationUnit(file, marker);
		final FindReferencedFunctionVisitor visitor = new FindReferencedFunctionVisitor(project, file.getLocation()
				.toOSString(), line);
		sourceTranslationUnit.accept(visitor);
		final IASTTranslationUnit headerTranslationUnit = visitor.getHeaderTranslationUnit();
		if (visitor.getDeclaration() != null) {
			declareConst(visitor.getDeclaration(), headerTranslationUnit);
		}
		if (visitor.getDefinition() != null) {
			declareConst(visitor.getDefinition(), sourceTranslationUnit);
		}
		return true;
	}

	private void declareConst(final IASTSimpleDeclaration declaration, final IASTTranslationUnit headerTranslationUnit)
			throws CoreException {
		final IASTSimpleDeclaration constDeclaration = declaration.copy();
		for (final IASTDeclarator decl : constDeclaration.getDeclarators()) {
			if (decl instanceof ICPPASTFunctionDeclarator) {
				((ICPPASTFunctionDeclarator) decl).setConst(true);
			}
		}
		final ASTRewrite r = ASTRewrite.create(headerTranslationUnit);
		r.replace(declaration, constDeclaration, null);
		r.rewriteAST().perform(new NullProgressMonitor());
	}

	private void declareConst(final ICPPASTFunctionDefinition definition,
			final IASTTranslationUnit sourceTranslationUnit) throws CoreException {
		final ICPPASTFunctionDefinition constDefinition = definition.copy();
		final IASTDeclarator declarator = constDefinition.getDeclarator();
		if (declarator instanceof ICPPASTFunctionDeclarator) {
			((ICPPASTFunctionDeclarator) declarator).setConst(true);
		}
		final ASTRewrite r = ASTRewrite.create(sourceTranslationUnit);
		r.replace(definition, constDefinition, null);
		r.rewriteAST().perform(new NullProgressMonitor());
	}
}