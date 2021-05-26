package com.linticator.astquickfixes.declareconst;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNodeSelector;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexFile;
import org.eclipse.cdt.core.index.IIndexName;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

import com.linticator.Linticator;
import com.linticator.quickfixes.LinticatorQuickfix;


public class FindReferencedFunctionVisitor extends ASTVisitor {
	private final IProject project;
	private final String fileName;
	private final int line;
	private IASTTranslationUnit headerTranslationUnit;
	private IASTSimpleDeclaration declaration;
	private ICPPASTFunctionDefinition definition;

	public FindReferencedFunctionVisitor(final IProject project, final String fileName,
			final int line) {
		this.project = project;
		this.fileName = fileName;
		this.line = line;
		shouldVisitDeclarations = true;
	}

	@Override
	public int visit(final IASTDeclaration declaration) {
		if (declaration instanceof ICPPASTFunctionDefinition) {
			final IASTFileLocation location = declaration.getFileLocation();
			if (location.getFileName().equals(fileName)
					&& location.getStartingLineNumber() <= line
					&& location.getEndingLineNumber() >= line) {
				this.definition = (ICPPASTFunctionDefinition) declaration;
				findCorrespondingDeclaration();
				return ASTVisitor.PROCESS_ABORT;
			}
		}
		return ASTVisitor.PROCESS_CONTINUE;
	}

	private void findCorrespondingDeclaration() {
		final IASTFunctionDeclarator declarator = definition.getDeclarator();
		final IBinding binding = declarator.getName().resolveBinding();
		try {
			final ICProject[] projects = CoreModel.getDefault().getCModel()
					.getCProjects();
			final IIndex index = CCorePlugin.getIndexManager().getIndex(projects);
			try {
				index.acquireReadLock();
				for (final IIndexName indexName : index.findDeclarations(binding)) {
					final IIndexFile indexFile = indexName.getFile();
					final String path = indexFile.getLocation().getFullPath();
					final IFile file = project.getProject().getFile(
							new Path(path).removeFirstSegments(1));
					headerTranslationUnit = LinticatorQuickfix.getCurrentTranslationUnitWithIndex(file, null);
					final IASTFileLocation fileLocation = indexName.getFileLocation();
					final IASTNodeSelector selector = headerTranslationUnit
							.getNodeSelector(null);
					final IASTNode name = selector.findNode(
							fileLocation.getNodeOffset(),
							fileLocation.getNodeLength());
					final IASTNode declaration = name.getParent().getParent();
					if (declaration instanceof IASTSimpleDeclaration) {
						this.declaration = (IASTSimpleDeclaration) declaration;
						break;
					}
				}
			} catch (final CoreException e) {
			} catch (final InterruptedException e) {
			} finally {
				index.releaseReadLock();
			}
		} catch (final CoreException e) {
			Linticator.getDefault().handleError(getClass().getName(), e);
		}
	}

	public IASTTranslationUnit getHeaderTranslationUnit() {
		return headerTranslationUnit;
	}

	public IASTSimpleDeclaration getDeclaration() {
		return declaration;
	}

	public ICPPASTFunctionDefinition getDefinition() {
		return definition;
	}

}
