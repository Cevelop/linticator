package com.linticator.markers;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.AbstractMarkerAnnotationModel;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import com.linticator.base.ILintMarker;
import com.linticator.base.StringUtil;
import com.linticator.config.GenericPluginConfig;

public abstract class Message {
	public static final String EXTERNAL_FILE_LINE = "externalFileLine";
	public static final String EXTERNAL_FILE_LOCATION = "externalFileLocation";
	private static final String COMMA_SPACE = ", "; //$NON-NLS-1$
	public static final String LINT_MARKER_ID = "com.linticator.lintMarker"; //$NON-NLS-1$
	public static final String LINT_LIBRARY_MARKER_ID = "com.linticator.lintLibraryMarker"; //$NON-NLS-1$
	private final IPath file;
	private final int line;
	private final int column;
	private final int messageCode;
	private final String description;
	public String getDescription() {
		return description;
	}

	private final String rawFileName;
	private final int quickfixLine;

	Message(final MessageParameters params) {
		rawFileName = params.fileName == null ? "" : params.fileName;
		file = new Path(rawFileName);
		line = params.lineNumber;
		column = params.columnNumber;
		messageCode = params.messageCode;
		description = params.description;
		quickfixLine = params.quickfixLineNumber;
	}

	public abstract String getMessageLevel();

	public boolean createMarker(final IProject project) throws CoreException {

		final IPath directory = file.removeLastSegments(1);
		if(directory != null && directory.lastSegment() != null && directory.lastSegment().equals(GenericPluginConfig.CONFIG_DIR)) {
			// don't create markers for files in the .lint directory
			return false;
		}
		
		final URI uri = URIUtil.toURI(file);
		List<IFile> filesInWorkspace = Collections.emptyList();
		if (uri.isAbsolute()) {
			filesInWorkspace = Arrays.asList(project.getWorkspace().getRoot().findFilesForLocationURI(uri));
		}

		if (filesInWorkspace.isEmpty()) {
			if (line == 0 && file.toString().length() == 0) {
				IMarker marker = createMarker(project, LINT_MARKER_ID);
				return marker != null && marker.exists();
			} else {
				try {
					final IMarker marker = createMarker(project, LINT_LIBRARY_MARKER_ID);
					marker.setAttribute(IMarker.LOCATION, String.format("line %d, %s", line, file.toString())); //$NON-NLS-1$
					marker.setAttribute(EXTERNAL_FILE_LOCATION, file.toOSString());
					marker.setAttribute(EXTERNAL_FILE_LINE, line);
				} catch (final CoreException e) {
					// marker creation failed; this can happen when the marker has already
					// been deleted, e.g. because concurrent Lint jobs are running.
					return false;
				}
				return true;
			}
		} else {
			for (final IFile f : filesInWorkspace) {
				// we also get files from other projects
				if (f.getProject().equals(project) && f.exists()) {
					try {
						final IMarker marker = createMarker(f, LINT_MARKER_ID);
						if (marker.exists()) {
							marker.setAttribute(IMarker.LOCATION, "line " + line); //$NON-NLS-1$	
							return true;
						}
					} catch (final CoreException e) {
						// marker creation failed; this can happen when the marker has already
						// been deleted, e.g. because concurrent Lint jobs are running.
					}
				}
			}
			return false;
		}
	}

	private IMarker createMarker(final IResource resource, final String markerType) throws CoreException {
		final IMarker marker = resource.createMarker(markerType);

		try {
			marker.setAttribute(IMarker.LINE_NUMBER, line);
			marker.setAttribute(IMarker.MESSAGE, messageCode + ": " + description); //$NON-NLS-1$
			marker.setAttribute(ILintMarker.PROBLEM_DESCRIPTION, description);
			marker.setAttribute(IMarker.PROBLEM, messageCode);
			marker.setAttribute(IMarker.SEVERITY, getSeverity());
			marker.setAttribute(ILintMarker.QUICKFIX_LOCATION, quickfixLine);
		} catch (final CoreException e) {
		}
		return marker;
	}

	protected abstract int getSeverity();

	public static void eraseLintMarkers(final IResource r) throws CoreException {
		if (r.exists()) {
			r.deleteMarkers(LINT_MARKER_ID, true, IResource.DEPTH_ONE);
		}
	}

	public static void eraseLintLibraryMarkers(final IResource r) throws CoreException {
		if (r.exists()) {
			r.deleteMarkers(LINT_LIBRARY_MARKER_ID, true, IResource.DEPTH_ONE);
		}
	}

	/**
	 * @return Returns the Lint line number of the marker (lint starts counting lines by 1)
	 */
	public static int getLintLineNumberFromMarker(final IMarker marker) {
		try {
			final Integer line = getActualMarkerLineNumber(marker);
			if (line != null) {
				return line + 1; // Lint-Message lines start counting from 1
			}
		} catch (final PartInitException e) {
		}
		return marker.getAttribute(IMarker.LINE_NUMBER, 0);
	}

	public static Integer getActualMarkerLineNumber(final IMarker marker) throws PartInitException {
		final ITextEditor editor = getTextEditor(marker);
		if (editor != null) {
			final IDocumentProvider documentProvider = editor.getDocumentProvider();
			try {
				final IDocument document = documentProvider.getDocument(editor.getEditorInput());
				final Integer actualMarkerOffset = getActualMarkerOffset(marker);
				if(actualMarkerOffset != null) {
					return document.getLineOfOffset(actualMarkerOffset);
				}
			} catch (final BadLocationException e) {
			}
		}
		return null;
	}

	private static Integer getActualMarkerOffset(final IMarker marker) throws PartInitException {
		final ITextEditor editor = getTextEditor(marker);
		if (editor != null) {
			final IDocumentProvider documentProvider = editor.getDocumentProvider();
			final IAnnotationModel model = documentProvider.getAnnotationModel(editor.getEditorInput());
			if (model instanceof AbstractMarkerAnnotationModel) {
				final AbstractMarkerAnnotationModel markerModel = (AbstractMarkerAnnotationModel) model;
				final Position position = markerModel.getMarkerPosition(marker);
				if (position != null && !position.isDeleted()) {
					return position.getOffset();
				}
			}
		}
		return null;
	}

	public static ITextEditor getTextEditor(final IMarker marker) throws PartInitException {
		final IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

		if (activeWorkbenchWindow == null)
			return null;

		return getEditor(marker.getResource().getLocationURI(), activeWorkbenchWindow);
	}

	private static ITextEditor getEditor(final URI fileUri, final IWorkbenchWindow window) throws PartInitException {
		final IWorkbenchPage activePage = window.getActivePage();

		if (activePage == null)
			return null;

		final IEditorReference[] editors = activePage.getEditorReferences();
		for (final IEditorReference reference : editors) {
			final IEditorInput editorInput = reference.getEditorInput();
			if (editorInput instanceof IFileEditorInput) {
				if (fileUri.equals(((IFileEditorInput) editorInput).getFile().getLocationURI())) {
					final IEditorPart editor = reference.getEditor(false);
					if ((editor != null) && (editor instanceof ITextEditor)) {
						return (ITextEditor) editor;
					}
				}
			}
		}
		return null;
	}

	public int getMessageCode() {
		return messageCode;
	}

	public IPath getFile() {
		return file;
	}

	public int getLine() {
		return line;
	}

	public String getRawFileName() {
		return rawFileName;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof Message) {
			final Message message = (Message) obj;
			return file.equals(message.file) && line == message.line && column == message.column
					&& messageCode == message.messageCode && description.equals(message.description);
		}
		return false;
	}

	@Override
	public String toString() {
		return StringUtil.join(COMMA_SPACE, file.toString(), String.valueOf(line), String.valueOf(column),
				String.valueOf(messageCode), description);
	}

	@Override
	public int hashCode() {
		return String.format("%s%d%d%d%s", file.toString(), line, column, //$NON-NLS-1$
				messageCode, description).hashCode();
	}
}
