package com.linticator.lint.parsing;

import java.util.Arrays;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;

import com.linticator.Linticator;
import com.linticator.functional.Function1;
import com.linticator.markers.Message;

@SuppressWarnings("deprecation")
public class ParseAndMarkFileJob extends ParseAndMarkJob {

	private final IResource file;
	private Function1<Message, Void> onMessageCreated;

	public ParseAndMarkFileJob(final IProject project, Function1<Message, Void> onMessageCreated, final IResource file, final String input) {
		super(project, input);
		this.onMessageCreated = onMessageCreated;
		this.file = file;
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		final Parser parser = new Parser(monitor);
		try {
			monitor.beginTask("Parse input", 5);
			final Set<Message> messages = parser.parse(input);
			monitor.worked(1);
			removeMessagesInIncludedFiles(messages);
			monitor.worked(1);
			createMarkers(messages, onMessageCreated, new SubProgressMonitor(monitor, 3));
		} catch (final CoreException e) {
			return new Status(IStatus.ERROR, Linticator.PLUGIN_ID, e.getLocalizedMessage(), e);
		}
		return new Status(IStatus.OK, Linticator.PLUGIN_ID, "ok");
	}

	/**
	 * Copied from Path because it exists only since 3.5
	 */
	private IPath makeRelativeTo(IPath self, IPath base) {
		// can't make relative if devices are not equal
		if (self.getDevice() != base.getDevice() && (self.getDevice() == null || !self.getDevice().equalsIgnoreCase(base.getDevice())))
			return self;
		int commonLength = self.matchingFirstSegments(base);
		final int differenceLength = base.segmentCount() - commonLength;
		final int newSegmentLength = differenceLength + self.segmentCount() - commonLength;
		if (newSegmentLength == 0)
			return Path.EMPTY;
		String[] newSegments = new String[newSegmentLength];
		// add parent references for each segment different from the base
		Arrays.fill(newSegments, 0, differenceLength, ".."); //$NON-NLS-1$
		// append the segments of this path not in common with the base
		System.arraycopy(self.segments(), commonLength, newSegments, differenceLength, newSegmentLength - differenceLength);

		String newPath = "";
		for (String s : newSegments)
			newPath += s + IPath.SEPARATOR;

		return new Path(newPath);
	}

	/**
	 * When re-linting a single file, we need to remove the already existing markers from the included files (i.e. those
	 * files lint also reports messages for).
	 * 
	 * We extract the files from the new messages, get the corresponding resource member and remove all markers from it.
	 */
	private void removeMessagesInIncludedFiles(final Set<Message> messages) throws CoreException {
		for (final Message message : messages) {
			// skip global messages not bound to a file
			if (message.getFile().isEmpty())
				continue;

			final IPath relFilePath = makeRelativeTo(message.getFile(), getProject().getLocation());
			if (!file.getProjectRelativePath().equals(relFilePath)) {
				final IResource member = getProject().findMember(relFilePath);
				if (member != null) {
					Message.eraseLintMarkers(member);
				}
			}
		}
	}
}
