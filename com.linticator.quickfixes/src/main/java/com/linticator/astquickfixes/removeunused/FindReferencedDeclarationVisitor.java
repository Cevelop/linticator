package com.linticator.astquickfixes.removeunused;

import java.util.Arrays;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;

public class FindReferencedDeclarationVisitor extends ASTVisitor {
	private String fileName;
	private int line;
	private IASTSimpleDeclaration declaration;
	private final String variableName;

	public FindReferencedDeclarationVisitor(String fileName, int line, String variableName) {
		this.fileName = fileName;
		this.variableName = variableName;
		shouldVisitDeclarations = true;
		this.line = line;
	}

	public int visit(IASTDeclaration declaration) {
		if (declaration instanceof IASTSimpleDeclaration) {
			IASTSimpleDeclaration simpleDeclaration = (IASTSimpleDeclaration) declaration;
			for (IASTDeclarator declarator : simpleDeclaration.getDeclarators()) {
				IASTFileLocation location = declarator.getFileLocation();
				if (location.getFileName().equals(fileName) && location.getStartingLineNumber() <= line
						&& location.getEndingLineNumber() >= line) {
					if (isNameWeSearch(declarator)) {
						this.declaration = simpleDeclaration;
						return ASTVisitor.PROCESS_ABORT;
					}
				}
			}
		}
		return ASTVisitor.PROCESS_CONTINUE;
	}

	public boolean isNameWeSearch(IASTDeclarator declarator) {
		return Arrays.equals(declarator.getName().getSimpleID(), variableName.toCharArray());
	}

	public IASTSimpleDeclaration getDeclaration() {
		return declaration;
	}

}
