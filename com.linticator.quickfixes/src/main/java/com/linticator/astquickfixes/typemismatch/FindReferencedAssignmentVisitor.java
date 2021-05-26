package com.linticator.astquickfixes.typemismatch;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;

public class FindReferencedAssignmentVisitor extends ASTVisitor {
	private String fileName;
	private int line;
	private IASTBinaryExpression binaryExpression;

	public FindReferencedAssignmentVisitor(String fileName, int line) {
		shouldVisitArrayModifiers = true;
		shouldVisitDeclarations = true;
		shouldVisitDeclarators = true;
		shouldVisitEnumerators = true;
		shouldVisitExpressions = true;
		shouldVisitInitializers = true;
		shouldVisitParameterDeclarations = true;
		shouldVisitStatements = true;
		this.fileName = fileName;
		this.line = line;
	}

	public int visit(IASTExpression expression) {
		if (expression instanceof IASTBinaryExpression) {
			IASTFileLocation location = expression.getFileLocation();
			if (location.getFileName().equals(fileName)
					&& location.getStartingLineNumber() <= line
					&& location.getEndingLineNumber() >= line) {
				IASTBinaryExpression binaryExpression = (IASTBinaryExpression) expression;
				if (isAssignment(binaryExpression.getOperator())) {
					this.binaryExpression = binaryExpression;
					return ASTVisitor.PROCESS_ABORT;
				}
			}
		}
		return ASTVisitor.PROCESS_CONTINUE;
	}

	private static boolean isAssignment(int op) {
		return op == IASTBinaryExpression.op_assign;
	}

	public IASTBinaryExpression getBinaryExpression() {
		return binaryExpression;
	}

}
