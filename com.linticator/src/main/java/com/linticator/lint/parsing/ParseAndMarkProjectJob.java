package com.linticator.lint.parsing;

import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;

import com.linticator.Linticator;
import com.linticator.functional.Function1;
import com.linticator.markers.Message;

@SuppressWarnings("deprecation")
public class ParseAndMarkProjectJob extends ParseAndMarkJob {

	private Function1<Message, Void> onMessageCreated;

	public ParseAndMarkProjectJob(final IProject project, Function1<Message, Void> onMessageCreated, final String input) {
		super(project, input);
		this.onMessageCreated = onMessageCreated;
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		monitor.beginTask("Parse input: " + module, 4);
		final Parser parser = new Parser(new SubProgressMonitor(monitor, 1));
		try {
			final Set<Message> messages = parser.parse(input);
			createMarkers(messages, onMessageCreated, new SubProgressMonitor(monitor, 3));
		} catch (final CoreException e) {
			return new Status(IStatus.ERROR, Linticator.PLUGIN_ID, e.getLocalizedMessage(), e);
		}
		monitor.done();
		return new Status(IStatus.OK, Linticator.PLUGIN_ID, "ok");
	}
}
