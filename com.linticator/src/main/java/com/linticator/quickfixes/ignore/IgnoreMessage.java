package com.linticator.quickfixes.ignore;

import org.eclipse.cdt.core.dom.ast.IASTComment;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

import com.linticator.Linticator;
import com.linticator.base.ILintMarker;
import com.linticator.quickfixes.LinticatorQuickfix;

public class IgnoreMessage extends LinticatorQuickfix {
	public static final String MSG_NUMBER = " !e";
	public static final String ADDITIONAL_IGNORE_COMMENT = " -e"; //$NON-NLS-1$
	public static final String LINT_RESTORE_COMMENT = "/*lint -restore */"; //$NON-NLS-1$
	public static final String NEW_LINE = System.getProperty("line.separator"); //$NON-NLS-1$
	public static final String LINT_IGNORE_COMMENT_END = " */"; //$NON-NLS-1$
	public static final String LINT_IGNORE_COMMENT_START = "/*lint -e"; //$NON-NLS-1$
	public static final String LINT_IGNORE_SHORT_COMMENT = "//lint !e"; //$NON-NLS-1$
	private IFile sourceFile;
	private int line;
	private IDocument doc;
	private IMarker marker;
	private IASTTranslationUnit unit;
	private IASTComment lineComment;

	public IgnoreMessage(final int code, final IMarker marker) {
		super(String.format("Ignore message %d at this location", code), marker, code);
	}

	@Override
	protected boolean runOnFile(final IMarker marker, final IFile file) throws Exception {
		this.marker = marker;
		sourceFile = file;
		final int originalQuickfixLine = marker.getAttribute(ILintMarker.QUICKFIX_LOCATION, -1);
		if (originalQuickfixLine < 0) {
			line = getLineNumberFromMarker();
		} else {
			final int originalMarkerLine = marker.getAttribute(ILintMarker.LINE_NUMBER, 0);
			final int offsetToMarker = originalQuickfixLine - originalMarkerLine;
			line = getLineNumberFromMarker() + offsetToMarker;
		}

		doc = getDocument(file);
		unit = getCurrentTranslationUnit(file, marker);
		if (!createCombinedComment()) {
			createNewComment();
		}
		return true;
	}

	private void createNewComment() throws CoreException {
		try {
			if (lineHasEndComment()) {
				if (isLintIgnoreComment()) {
					addMessageNumber();
				} else {
					createIgnoreRestoreComments();
				}
			} else {
				final String lineDelimiter = doc.getLineDelimiter(line - 1);
				final int lineLength = doc.getLineLength(line - 1);
				final int offset = doc.getLineOffset(line - 1) + lineLength - lineDelimiter.length();
				final String comment = LINT_IGNORE_SHORT_COMMENT + marker.getAttribute(IMarker.PROBLEM);
				createInsertChange(offset, comment);
			}
		} catch (final BadLocationException e) {
			Linticator.getDefault().handleError(this.getClass().getName(), e);
		}
	}

	private void addMessageNumber() throws BadLocationException, CoreException {
		final String lineDelimiter = doc.getLineDelimiter(line - 1);
		final int lineLength = doc.getLineLength(line - 1);
		final int offset = doc.getLineOffset(line - 1) + lineLength - lineDelimiter.length();
		final String comment = MSG_NUMBER + marker.getAttribute(IMarker.PROBLEM);
		createInsertChange(offset, comment);

	}

	private boolean isLintIgnoreComment() {
		if (lineComment != null && new String(lineComment.getComment()).startsWith("//lint")) //$NON-NLS-1$
			return true;
		return false;
	}

	private void createIgnoreRestoreComments() throws BadLocationException, CoreException {
		int offsetBefore;
		int offsetAfter;
		offsetAfter = doc.getLineOffset(line);
		offsetBefore = doc.getLineOffset(line - 1);
		final String restoreComment = LINT_RESTORE_COMMENT + NEW_LINE;
		final String ignoreComment = LINT_IGNORE_COMMENT_START + marker.getAttribute(IMarker.PROBLEM)
				+ LINT_IGNORE_COMMENT_END + NEW_LINE;
		createInsertChange(offsetAfter, restoreComment);
		createInsertChange(offsetBefore, ignoreComment);
	}

	private boolean lineHasEndComment() {
		final IASTComment[] comments = unit.getComments();
		try {
			final int lineLength = doc.getLineLength(line - 1);
			final int offset = doc.getLineOffset(line - 1);
			for (final IASTComment comment : comments) {
				final IASTFileLocation loc = comment.getFileLocation();
				if (offset < loc.getNodeOffset() && (loc.getNodeLength() + loc.getNodeOffset()) <= offset + lineLength) {
					lineComment = comment;
					return true;
				}
			}
		} catch (final BadLocationException e) {
			return false;
		}
		return false;
	}

	private void createInsertChange(final int offset, final String comment) throws CoreException {
		final TextFileChange change = new TextFileChange("ignoreMessage", sourceFile); //$NON-NLS-1$
		final InsertEdit edit = new InsertEdit(offset, comment);
		change.setEdit(edit);
		change.perform(new NullProgressMonitor());
	}

	private void createReplaceChange(final int offset, final int textLength, final String comment) throws CoreException {
		final TextFileChange change = new TextFileChange("ignoreMessage", sourceFile); //$NON-NLS-1$
		final ReplaceEdit edit = new ReplaceEdit(offset, textLength, comment);
		change.setEdit(edit);
		change.perform(new NullProgressMonitor());
	}

	private boolean createCombinedComment() throws CoreException {
		final Path markerLocation = (Path) marker.getResource().getRawLocation();
		final int priorLine = line - 1;
		for (final IASTComment com : unit.getComments()) {

			if (isLintIgnoreComment(markerLocation, priorLine, com)) {
				int offset = 0;
				try {
					offset = doc.getLineOffset(priorLine - 1);
				} catch (final BadLocationException e) {
					Linticator.getDefault().handleError(this.getClass().getName(), e);
					// return false?
				}
				createReplaceChange(offset, com.getRawSignature().length(), getComment(com, marker));
				return true;
			}
		}
		return false;
	}

	private boolean isLintIgnoreComment(final Path markerLocation, final int priorLine, final IASTComment com) {
		final IASTFileLocation location = com.getFileLocation();
		final Path commentLocation = new Path(location.getFileName());
		final boolean offsetFits = location.getStartingLineNumber() <= priorLine
				&& location.getEndingLineNumber() >= priorLine && markerLocation.equals(commentLocation);
		final String comment = new String(com.getComment());

		return offsetFits && comment.contains("lint"); //$NON-NLS-1$
	}

	private IDocument getDocument(final IFile sourceFile) {
		final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorPart part = null;
		try {
			part = IDE.openEditor(page, sourceFile, true);
		} catch (final PartInitException e) {
			return null;
		}
		if (!(part instanceof ITextEditor))
			return null;
		final ITextEditor editor = (ITextEditor) part;
		final IDocument doc = editor.getDocumentProvider().getDocument(new FileEditorInput(sourceFile));
		return doc;
	}

	private String getComment(final IASTComment com, final IMarker marker) throws CoreException {
		String comment;
		final String rawSignature = com.getRawSignature();
		comment = rawSignature.substring(0, rawSignature.length() - 2) + ADDITIONAL_IGNORE_COMMENT
				+ marker.getAttribute(IMarker.PROBLEM) + LINT_IGNORE_COMMENT_END;
		return comment;
	}
}