package com.linticator.astquickfixes.addreturnstatement;

import org.eclipse.cdt.core.dom.ast.IASTReturnStatement;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTConstructorInitializer;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTReturnStatement;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTSimpleTypeConstructorExpression;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.linticator.quickfixes.LinticatorQuickfix;

@SuppressWarnings("restriction")
public class AddReturnStatement extends LinticatorQuickfix {

	public AddReturnStatement(final IMarker marker, final int code) {
		super("Add return statement", marker, code);
	}

	@Override
	protected boolean runOnFile(final IMarker marker, final IFile file) throws CoreException {
		final int line = getLineNumberFromMarker();
		final IASTTranslationUnit unit = getCurrentTranslationUnit(file, marker);
		final ICPPASTFunctionDefinition method = getReferencedDeclaration(unit, file.getLocation().toOSString(), line);
		final ICPPASTDeclSpecifier declSpec = (ICPPASTDeclSpecifier) method.getDeclSpecifier().copy();
		final CPPASTSimpleTypeConstructorExpression construct = new CPPASTSimpleTypeConstructorExpression(declSpec,
				new CPPASTConstructorInitializer());
		final IASTReturnStatement ret = new CPPASTReturnStatement(construct);
		final IASTStatement body = method.getBody();
		final ASTRewrite r = ASTRewrite.create(unit);
		r.insertBefore(body, null, ret, null);
		r.rewriteAST().perform(new NullProgressMonitor());
		return true;
	}

	private ICPPASTFunctionDefinition getReferencedDeclaration(final IASTTranslationUnit unit, final String fileName,
			final int line) {
		final FindReferencedFunctionImplementationVisitor visitor = new FindReferencedFunctionImplementationVisitor(
				fileName, line);
		unit.accept(visitor);
		return visitor.getDefinition();
	}
}