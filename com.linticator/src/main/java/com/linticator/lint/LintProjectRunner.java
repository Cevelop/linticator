package com.linticator.lint;

import com.linticator.Linticator;
import com.linticator.config.LaunchConfig;
import com.linticator.config.ProjectConfig;
import com.linticator.functional.Function1;
import com.linticator.lint.parsing.ParseAndMarkJob;
import com.linticator.lint.parsing.ParseAndMarkProjectJob;
import com.linticator.markers.Message;
import com.linticator.view.console.AbstractLintConsole;

public class LintProjectRunner extends AbstractLintRunner {

	public LintProjectRunner(final ProjectConfig projectConfig,
			LaunchConfig launchConfig, final AbstractLintConsole console) {
		super(projectConfig, launchConfig, console);
	}

	@Override
	protected void runParseJob(final StringBuilder sb, Function1<Message, Void> onMessageCreated) {
		final ParseAndMarkJob job = new ParseAndMarkProjectJob(projectConfig.getProject(), onMessageCreated, sb.toString());
		job.schedule();
	}

	@Override
	protected String[] getLintCommandOptions() {
		final String[] command = new String[] { projectConfig.getCompilerConfigFile().makeAbsolute().toString(),
				projectConfig.getProjectConfigFile().makeAbsolute().toString(),
				Linticator.getFormatFile().makeAbsolute().toString(),
				projectConfig.getFileListFile().makeAbsolute().toString() };
		return command;
	}

}
