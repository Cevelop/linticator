package com.linticator.astquickfixes.addreturnstatement;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;

public class FindReferencedFunctionImplementationVisitor extends ASTVisitor {
	private String fileName;
	private int line;
	private ICPPASTFunctionDefinition definition;

	public FindReferencedFunctionImplementationVisitor(String fileName, int line) {
		this.fileName = fileName;
		this.line = line;
		shouldVisitDeclarations = true;
	}

	public int visit(IASTDeclaration declaration) {
		if (declaration instanceof ICPPASTFunctionDefinition) {
			IASTFileLocation location = declaration.getFileLocation();
			if (location.getFileName().equals(fileName)
					&& location.getStartingLineNumber() <= line
					&& location.getEndingLineNumber() >= line) {
				this.definition = (ICPPASTFunctionDefinition) declaration;
				return ASTVisitor.PROCESS_ABORT;
			}
		}
		return ASTVisitor.PROCESS_CONTINUE;
	}

	public ICPPASTFunctionDefinition getDefinition() {
		return definition;
	}

}
