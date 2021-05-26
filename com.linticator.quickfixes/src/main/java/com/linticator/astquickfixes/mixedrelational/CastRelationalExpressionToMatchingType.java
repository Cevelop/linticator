package com.linticator.astquickfixes.mixedrelational;

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
public class CastRelationalExpressionToMatchingType extends LinticatorQuickfix {

	public CastRelationalExpressionToMatchingType(final IMarker marker, final int code) {
		super("Cast relational expression to matching type", marker, code);
	}

	@Override
	protected boolean runOnFile(final IMarker marker, final IFile file) throws Exception {
		final int line = getLineNumberFromMarker();
		final IASTTranslationUnit unit = getCurrentTranslationUnit(file, marker);
		final IASTBinaryExpression relation = getReferencedExpression(unit, file.getLocation().toOSString(), line);
		final String typeName = ASTTypeUtil.getType(relation.getOperand1().getExpressionType());
		final IASTTypeId typeId = new CPPASTTypeId(new CPPASTSimpleDeclSpecifier(), new CPPASTDeclarator(
				new CPPASTName(typeName.toCharArray())));
		final ICPPASTCastExpression cast = new CPPASTCastExpression(ICPPASTCastExpression.op_static_cast, typeId,
				relation.getOperand2().copy());
		final ASTRewrite r = ASTRewrite.create(unit);
		r.replace(relation.getOperand2(), cast, null);
		r.rewriteAST().perform(new NullProgressMonitor());
		return true;
	}

	private IASTBinaryExpression getReferencedExpression(final IASTTranslationUnit unit, final String fileName,
			final int line) {
		final FindReferencedNodeVisitor visitor = new FindReferencedNodeVisitor(fileName, line);
		unit.accept(visitor);
		return visitor.getBinaryExpression();
	}
}