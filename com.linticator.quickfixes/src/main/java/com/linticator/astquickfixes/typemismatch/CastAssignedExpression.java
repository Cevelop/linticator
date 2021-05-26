package com.linticator.astquickfixes.typemismatch;

import org.eclipse.cdt.core.dom.ast.ASTTypeUtil;
import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IASTTypeId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCastExpression;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTCastExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTDeclarator;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTName;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTSimpleDeclSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTTypeId;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.linticator.quickfixes.LinticatorQuickfix;

@SuppressWarnings("restriction")
public class CastAssignedExpression extends LinticatorQuickfix {

	public CastAssignedExpression(final IMarker marker, final int code) {
		super("Cast assigned expression", marker, code);
	}

	@Override
	protected boolean runOnFile(final IMarker marker, final IFile file) throws Exception {
		final int line = getLineNumberFromMarker();
		final IASTTranslationUnit unit = getCurrentTranslationUnit(file, marker);
		final IASTBinaryExpression expr = getReferencedExpression(unit, file.getLocation().toOSString(), line);
		final String typeName = ASTTypeUtil.getType(expr.getOperand1().getExpressionType());
		final IASTTypeId typeId = new CPPASTTypeId(new CPPASTSimpleDeclSpecifier(), new CPPASTDeclarator(
				new CPPASTName(typeName.toCharArray())));
		final ICPPASTCastExpression cast = new CPPASTCastExpression(ICPPASTCastExpression.op_static_cast, typeId, expr
				.getOperand2().copy());
		final ASTRewrite r = ASTRewrite.create(unit);
		r.replace(expr.getOperand2(), cast, null);
		r.rewriteAST().perform(new NullProgressMonitor());
		return true;
	}

	private IASTBinaryExpression getReferencedExpression(final IASTTranslationUnit unit, final String fileName,
			final int line) {
		final FindReferencedAssignmentVisitor visitor = new FindReferencedAssignmentVisitor(fileName, line);
		unit.accept(visitor);
		return visitor.getBinaryExpression();
	}
}