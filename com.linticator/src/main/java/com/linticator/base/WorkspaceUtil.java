package com.linticator.base;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;

import com.linticator.Linticator;

public final class WorkspaceUtil {

	/**
	 * @param line
	 *            The line to open, counting starts from 1.
	 */
	public static void openFileAtLine(final IPath path, final int line) {
		final IFileStore fileStore = EFS.getLocalFileSystem().getStore(path);
		try {
			final IEditorPart editor = IDE.openEditorOnFileStore(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage(), fileStore);

			if (editor != null && editor instanceof ITextEditor) {
				selectLineInEditor(line, (ITextEditor) editor);
			}
		} catch (final PartInitException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param line
	 *            The line to open, counting starts from 1.
	 */
	public static void selectLineInEditor(final int line, final ITextEditor editor) {

		if (editor.getDocumentProvider() == null || editor.getEditorInput() == null)
			return;

		final IDocument document = editor.getDocumentProvider().getDocument(editor.getEditorInput());

		if (document == null)
			return;

		int offsetBeginOfLine = -1;
		int offsetEndOfLine = -1;

		try {
			offsetBeginOfLine = document.getLineOffset(line > 0 ? line - 1 : 0);
			offsetEndOfLine = document.getLineOffset(line);
		} catch (final BadLocationException e) {
		}

		if (offsetBeginOfLine < 0 || offsetEndOfLine < 0)
			return;

		editor.selectAndReveal(offsetBeginOfLine, offsetEndOfLine - offsetBeginOfLine - 1);
	}

	public static void openLink(final String href, final String scopeForErrorMessage) {
		final IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench().getBrowserSupport();
		try {
			browserSupport.getExternalBrowser().openURL(new URL(href));
		} catch (final PartInitException e) {
			Linticator.getDefault().handleError(scopeForErrorMessage, e);
		} catch (final MalformedURLException e) {
		}
	}
}
