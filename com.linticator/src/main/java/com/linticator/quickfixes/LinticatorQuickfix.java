package com.linticator.quickfixes;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.core.model.IWorkingCopy;
import org.eclipse.cdt.internal.ui.CPluginImages;
import org.eclipse.cdt.ui.CDTSharedImages;
import org.eclipse.cdt.ui.CUIPlugin;
import org.eclipse.cdt.ui.IWorkingCopyManager;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.texteditor.ITextEditor;

import com.linticator.Linticator;
import com.linticator.documentation.DocumentationFromMsgTxt;
import com.linticator.documentation.EntryNotFoundException;
import com.linticator.markers.Message;

@SuppressWarnings("restriction")
abstract public class LinticatorQuickfix implements IMarkerResolution2 {

	private final String label;
	private final IMarker marker;
	protected final int code;

	public LinticatorQuickfix(final String label, final IMarker marker, final int code) {
		this.label = label;
		this.marker = marker;
		this.code = code;
	}

	@Override
	public void run(final IMarker marker) {
		final IResource resource = marker.getResource();
		if (resource instanceof IFile) {
			try {
				if (runOnFile(marker, (IFile) resource)) {
					marker.delete();
				}
			} catch (final Exception e) {
				e.printStackTrace();
				Linticator.getDefault().handleError("Quickfix", e); //$NON-NLS-1$
			}
		}
	}

	abstract protected boolean runOnFile(IMarker marker, IFile file) throws Exception;

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public String getDescription() {
		try {
			return Linticator.getBeans().getDocumentation().documentationForMarker(code, marker, DocumentationFromMsgTxt.HTML_SEPARATOR);
		} catch (final EntryNotFoundException e) {
			return Messages.LinticatorQuickfix_1 + code + Messages.LinticatorQuickfix_2;
		}
	}

	@Override
	public Image getImage() {
		return CDTSharedImages.getImage(CPluginImages.IMG_CORRECTION_CHANGE);
	}

	public static IASTTranslationUnit getCurrentTranslationUnit(final IFile file, final IMarker marker) throws CoreException {
		return getTranslationUnit(file, marker);
	}

	private static IASTTranslationUnit getTranslationUnit(final IFile file, final IMarker marker) throws CoreException {

		if (marker == null) {
			return loadUnchangedTranslationUnit(file);
		}

		final ITextEditor editor = Message.getTextEditor(marker);
		if (editor == null || !editor.isDirty()) {
			return loadUnchangedTranslationUnit(file);
		}

		final IWorkingCopyManager manager = CUIPlugin.getDefault().getWorkingCopyManager();

		final IWorkingCopy workingCopy = manager.getWorkingCopy(editor.getEditorInput());
		if (workingCopy == null) {
			return loadUnchangedTranslationUnit(file);
		}

		synchronized (workingCopy) {
			return workingCopy.reconcile(true, true, new NullProgressMonitor());
		}
	}

	private static IASTTranslationUnit loadUnchangedTranslationUnit(final IFile file) throws CoreException {
		if (file == null) {
			return null;
		}

		IIndex index = null;
		try {
			ICProject[] projects = CoreModel.getDefault().getCModel().getCProjects();
			// TODO might be unnecessary, better to use only the index of the project + dependencies
			index = CCorePlugin.getIndexManager().getIndex(projects);
			try {
				index.acquireReadLock();
			} catch (InterruptedException e) {
				// no lock was acquired
				index = null;
				throw e;
			}
			ITranslationUnit tu = (ITranslationUnit) CCorePlugin.getDefault().getCoreModel().create(file);
			return tu.getAST(index, ITranslationUnit.AST_CONFIGURE_USING_SOURCE_CONTEXT | ITranslationUnit.AST_SKIP_INDEXED_HEADERS);
		} catch (InterruptedException e) {
			CUIPlugin.log(e);
		} finally {
			if (index != null) {
				index.releaseReadLock();
			}
		}
		return null;
	}

	public static IASTTranslationUnit getCurrentTranslationUnitWithIndex(final IFile file, final IMarker marker) throws CoreException {
		return getTranslationUnit(file, marker);
	}

	protected int getLineNumberFromMarker() {
		return Message.getLintLineNumberFromMarker(marker);
	}

}
