package com.linticator.documentation;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class OpenDocumentationHandler implements IHandler {

	@Override
	public void addHandlerListener(final IHandlerListener handlerListener) {
	}

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {

		try {
			showDocumentationView();
		} catch (final PartInitException e) {
			return null;
		}

		final IViewReference[] allViews = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getViewReferences();

		for (final IViewReference view : allViews) {

			if (!isDocumentationView(view))
				continue;

			updateDocumentationView(view);
		}

		return null;
	}

	private void updateDocumentationView(final IViewReference view) {
		final IViewPart viewPart = view.getView(true);

		if (viewPart instanceof DocumentationView) {
			((DocumentationView) viewPart).showDocumentationForSelection(getCurrentSelection());
		}
	}

	private boolean isDocumentationView(final IViewReference view) {
		return view.getId().equals(DocumentationView.ID);
	}

	private ISelection getCurrentSelection() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getSelection();
	}

	private void showDocumentationView() throws PartInitException {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.showView(DocumentationView.ID, null, IWorkbenchPage.VIEW_VISIBLE);
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

	@Override
	public void dispose() {
	}
}
