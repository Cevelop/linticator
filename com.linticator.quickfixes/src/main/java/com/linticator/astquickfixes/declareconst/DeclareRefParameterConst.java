package com.linticator.astquickfixes.declareconst;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTParameterDeclaration;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.linticator.quickfixes.LinticatorQuickfix;

public class DeclareRefParameterConst extends LinticatorQuickfix {

	public DeclareRefParameterConst(final IMarker marker, final int code) {
		super("Declare reference parameter const", marker, code);
	}

	private static final Pattern PARAMETER_NAME = Pattern.compile("'(.*)'"); //$NON-NLS-1$

	@Override
	protected boolean runOnFile(final IMarker marker, final IFile file) throws Exception {
		final IProject project = file.getProject();
		final int line = getLineNumberFromMarker();
		final String parameterName = getParameterName(marker);
		final IASTTranslationUnit sourceTranslationUnit = getCurrentTranslationUnitWithIndex(file, marker);
		final FindReferencedFunctionVisitor visitor = new FindReferencedFunctionVisitor(project, file.getLocation()
				.toOSString(), line);
		sourceTranslationUnit.accept(visitor);
		final IASTTranslationUnit headerTranslationUnit = visitor.getHeaderTranslationUnit();
		final int parameterIndex = declareConst(visitor.getDefinition(), sourceTranslationUnit, parameterName);
		declareConst(visitor.getDeclaration(), headerTranslationUnit, parameterIndex);
		return true;
	}

	private String getParameterName(final IMarker marker) throws NoParameterNameAvailableException {
		final String message = marker.getAttribute(IMarker.MESSAGE, ""); //$NON-NLS-1$
		final Matcher matcher = PARAMETER_NAME.matcher(message);
		if (matcher.find())
			return matcher.group(1);
		throw new NoParameterNameAvailableException();
	}

	private void declareConst(final IASTSimpleDeclaration declaration, final IASTTranslationUnit headerTranslationUnit,
			final int parameterIndex) throws CoreException {
		final IASTSimpleDeclaration constDeclaration = declaration.copy();
		for (final IASTDeclarator decl : constDeclaration.getDeclarators())
			if (decl instanceof ICPPASTFunctionDeclarator) {
				final ICPPASTFunctionDeclarator fDecl = (ICPPASTFunctionDeclarator) decl;
				fDecl.getParameters()[parameterIndex].getDeclSpecifier().setConst(true);
			}
		final ASTRewrite r = ASTRewrite.create(headerTranslationUnit);
		r.replace(declaration, constDeclaration, null);
		r.rewriteAST().perform(new NullProgressMonitor());
	}

	private int declareConst(final ICPPASTFunctionDefinition definition,
			final IASTTranslationUnit sourceTranslationUnit, final String parameterName) throws CoreException {
		final ICPPASTFunctionDefinition constDefinition = definition.copy();
		final IASTDeclarator declarator = constDefinition.getDeclarator();
		if (declarator instanceof ICPPASTFunctionDeclarator) {
			final ICPPASTFunctionDeclarator fDecl = (ICPPASTFunctionDeclarator) declarator;
			int index = 0;
			for (final ICPPASTParameterDeclaration parameter : fDecl.getParameters()) {
				if (parameter.getDeclarator().getName().toString().equals(parameterName)) {
					parameter.getDeclSpecifier().setConst(true);
					final ASTRewrite r = ASTRewrite.create(sourceTranslationUnit);
					r.replace(definition, constDefinition, null);
					r.rewriteAST().perform(new NullProgressMonitor());
					return index;
				}
				++index;
			}
		}
		return 0;
	}
}