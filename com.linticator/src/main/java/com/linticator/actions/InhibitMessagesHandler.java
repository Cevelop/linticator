package com.linticator.actions;

import java.util.Arrays;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

import com.linticator.markers.Message;
import com.linticator.quickfixes.inhibitmessages.InhibitMessagesWizard;

public class InhibitMessagesHandler implements IHandler {

	@Override
	public void addHandlerListener(final IHandlerListener handlerListener) {
	}

	@Override
	public void dispose() {
	}

	private IFile getCurrentFile() {

		final IEditorPart editor = getActiveEditor();

		if (editor instanceof ITextEditor) {
			final IEditorInput input = ((ITextEditor) editor).getEditorInput();
			if (input instanceof IFileEditorInput) {
				return ((IFileEditorInput) input).getFile();
			}
		}

		return null;
	}

	private IEditorPart getActiveEditor() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
	}

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {

		final IEditorPart editor = getActiveEditor();

		if (editor instanceof ITextEditor) {
			try {
				final IMarker[] markers = getAllLintMarkers();

				if (markers.length > 0) {
					final IDocument document = ((ITextEditor) editor).getDocumentProvider().getDocument(
							editor.getEditorInput());
					InhibitMessagesWizard.openWizard(document, Arrays.asList(markers));
				} else {
					MessageDialog.openInformation(Display.getCurrent().getActiveShell(),
							"Linticator: Inhibit Messages", "There are no Lint messages in the current file.");
				}
			} catch (final CoreException e) {
			}
		}

		return null;
	}

	private IMarker[] getAllLintMarkers() throws CoreException {
		return getCurrentFile().findMarkers(Message.LINT_MARKER_ID, true, IResource.DEPTH_INFINITE);
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean isHandled() {
		return true;
	}

	@Override
	public void removeHandlerListener(final IHandlerListener handlerListener) {
	}
}
