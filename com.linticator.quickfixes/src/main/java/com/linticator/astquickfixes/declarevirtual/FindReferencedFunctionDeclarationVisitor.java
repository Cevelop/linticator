package com.linticator.astquickfixes.declarevirtual;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;

public class FindReferencedFunctionDeclarationVisitor extends ASTVisitor {
	private String fileName;
	private int line;
	private IASTSimpleDeclaration declaration;

	public FindReferencedFunctionDeclarationVisitor(String fileName, int line) {
		this.fileName = fileName;
		this.line = line;
		shouldVisitDeclarations = true;
	}

	public int visit(IASTDeclaration declaration) {
		if (declaration instanceof IASTSimpleDeclaration) {
			IASTSimpleDeclaration simpleDeclaration = (IASTSimpleDeclaration) declaration;
			for (IASTDeclarator declarator : simpleDeclaration.getDeclarators()) {
				if (declarator instanceof ICPPASTFunctionDeclarator) {
					ICPPASTFunctionDeclarator fdecl = (ICPPASTFunctionDeclarator) declarator;
					IASTFileLocation location = fdecl.getFileLocation();
					if (location.getFileName().equals(fileName)
							&& location.getStartingLineNumber() <= line
							&& location.getEndingLineNumber() >= line) {
						this.declaration = simpleDeclaration;
						return ASTVisitor.PROCESS_ABORT;
					}
				}
			}
		}
		return ASTVisitor.PROCESS_CONTINUE;
	}

	public IASTSimpleDeclaration getDeclaration() {
		return declaration;
	}

}
