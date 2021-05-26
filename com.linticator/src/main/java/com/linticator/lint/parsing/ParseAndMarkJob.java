package com.linticator.lint.parsing;

import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;

import com.linticator.functional.Function1;
import com.linticator.markers.Message;

@SuppressWarnings("deprecation")
public abstract class ParseAndMarkJob extends Job {

	protected String input;
	protected String module;
	private final IProject project;

	public ParseAndMarkJob(final IProject project, final String input) {
		super("Parse and Mark");
		this.project = project;
		setRule(null);
		final int start = input.indexOf('M');
		final int end = input.indexOf('\n', start);
		if (start >= 0 && end > 0) {
			module = input.substring(start, end);
		} else {
			module = "";
		}
		this.input = input;
	}

	protected void createMarkers(final Set<Message> messages, Function1<Message, Void> onMessageCreated, final SubProgressMonitor monitor) throws CoreException {
		monitor.beginTask("Create Markers: ", messages.size());
		for (final Message message : messages) {
			monitor.subTask(message.toString());
			boolean created = message.createMarker(getProject());
			if (created) {
				onMessageCreated.apply(message);
			}
			monitor.worked(1);
		}
		monitor.done();
	}

	public IProject getProject() {
		return project;
	}
}
