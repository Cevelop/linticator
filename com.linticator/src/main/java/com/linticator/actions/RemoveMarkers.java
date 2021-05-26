package com.linticator.actions;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

import com.linticator.functional.Function1;
import com.linticator.lint.LintBuilder;

public class RemoveMarkers extends WithSelectedProjectAction implements IHandler {

	@Override
	public void run(final IAction action) {
		withProject(new Function1<IProject, Void>() {

			@Override
			public Void apply(final IProject p) {
				LintBuilder.removeAllLintMarkers(p);
				return null;
			}
		});
	}

	@Override
	public void addHandlerListener(final IHandlerListener handlerListener) {
	}

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {

		final IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getActiveEditor();

		if (editor instanceof ITextEditor) {
			final IEditorInput input = ((ITextEditor) editor).getEditorInput();
			if (input instanceof IFileEditorInput) {
				final IProject project = ((IFileEditorInput) input).getFile().getProject();
				LintBuilder.removeAllLintMarkers(project);
			}
		}

		return null;
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
