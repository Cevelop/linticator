package com.linticator.quickfixes;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.ui.CDTSharedImages;
import org.eclipse.cdt.ui.text.ICCompletionProposal;
import org.eclipse.cdt.ui.text.IInvocationContext;
import org.eclipse.cdt.ui.text.IProblemLocation;
import org.eclipse.cdt.ui.text.IQuickFixProcessor;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.linticator.base.ILintMarker;
import com.linticator.markers.Message;
import com.linticator.quickfixes.inhibitmessages.InhibitMessagesWizard;

public class QuickFixProcessor implements IQuickFixProcessor {

	private final static class InhibitMessagesCompletionProposal implements ICCompletionProposal {
		private final Collection<IMarker> markersOnInvocationLine;

		public InhibitMessagesCompletionProposal(final Collection<IMarker> markersOnInvocationLine) {
			this.markersOnInvocationLine = markersOnInvocationLine;
		}

		@Override
		public void apply(final IDocument document) {
			InhibitMessagesWizard.openWizard(document, markersOnInvocationLine);
		}

		@Override
		public Point getSelection(final IDocument document) {
			return null; // no new selection
		}

		@Override
		public String getAdditionalProposalInfo() {
			return Messages.QuickFixProcessor_0;
		}

		@Override
		public String getDisplayString() {
			return Messages.QuickFixProcessor_1;
		}

		@Override
		public Image getImage() {
			return CDTSharedImages.getImage(CDTSharedImages.IMG_FILELIST_DEL);
		}

		@Override
		public IContextInformation getContextInformation() {
			return null;
		}

		@Override
		public int getRelevance() {
			return 0;
		}

		@Override
		public String getIdString() {
			return "getIdString"; //$NON-NLS-1$
		}
	}

	@Override
	public boolean hasCorrections(final ITranslationUnit unit, final int problemId) {
		// doesn't have any effect..
		return true;
	}

	@Override
	public ICCompletionProposal[] getCorrections(final IInvocationContext context, final IProblemLocation[] locations) throws CoreException {

		if (context instanceof IQuickAssistInvocationContext) {

			final IQuickAssistInvocationContext ctx = (IQuickAssistInvocationContext) context;

			final ArrayList<IMarker> markersOnInvocationLine = new ArrayList<IMarker>();

			final IResource resource = context.getTranslationUnit().getResource();
			try {
				final int invocationLine = ctx.getSourceViewer().getDocument().getLineOfOffset(ctx.getOffset());

				for (final IMarker m : resource.findMarkers(Message.LINT_MARKER_ID, true, IResource.DEPTH_INFINITE)) {
					final Integer actualMarkerLineNumber = Message.getActualMarkerLineNumber(m);
					if (actualMarkerLineNumber == null || actualMarkerLineNumber != invocationLine)
						continue;
					if (!markerAlreadyInList(m, markersOnInvocationLine))
						markersOnInvocationLine.add(m);
				}

				if (markersOnInvocationLine.isEmpty()) {
					return null;
				} else {
					return new ICCompletionProposal[] { new InhibitMessagesCompletionProposal(markersOnInvocationLine) };
				}
			} catch (final BadLocationException e) {
			}
		}

		return null;
	}

	private boolean markerAlreadyInList(final IMarker toInsert, final ArrayList<IMarker> markersOnInvocationLine) {
		final String compareWith = ILintMarker.PROBLEM_DESCRIPTION;
		for (final IMarker m : markersOnInvocationLine) {
			if (m.getAttribute(compareWith, "").equals(toInsert.getAttribute(compareWith, ""))) { //$NON-NLS-1$ //$NON-NLS-2$
				return true;
			}
		}
		return false;
	}
}
