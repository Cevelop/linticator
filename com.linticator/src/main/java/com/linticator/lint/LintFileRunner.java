package com.linticator.lint;

import org.eclipse.core.resources.IFile;

import com.linticator.Linticator;
import com.linticator.config.LaunchConfig;
import com.linticator.config.ProjectConfig;
import com.linticator.functional.Function1;
import com.linticator.lint.parsing.ParseAndMarkFileJob;
import com.linticator.markers.Message;
import com.linticator.view.console.AbstractLintConsole;


public class LintFileRunner extends AbstractLintRunner {

	private final IFile file;

	public LintFileRunner(final ProjectConfig projectConfig, final AbstractLintConsole console, final IFile file) {
		super(projectConfig, new LaunchConfig(), console);
		this.file = file;
	}

	@Override
	protected void runParseJob(final StringBuilder sb, Function1<Message, Void> onMessageCreated) {
		final ParseAndMarkFileJob job = new ParseAndMarkFileJob(projectConfig.getProject(), onMessageCreated, file, sb.toString());
		job.schedule();
	}

	@Override
	protected String[] getLintCommandOptions() {
		final String[] command = new String[] {
				"-u", //$NON-NLS-1$
				projectConfig.getCompilerConfigFile().makeAbsolute().toString(),
				projectConfig.getProjectConfigFile().makeAbsolute().toString(),
				Linticator.getFormatFile().makeAbsolute().toString(),
				file.getLocation().makeAbsolute().toString()};
		return command;
	}
}
