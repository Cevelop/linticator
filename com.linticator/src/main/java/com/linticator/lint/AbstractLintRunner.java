package com.linticator.lint;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Semaphore;

import org.eclipse.cdt.core.cdtvariables.CdtVariableException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

import com.linticator.Linticator;
import com.linticator.config.LaunchConfig;
import com.linticator.config.ProjectConfig;
import com.linticator.functional.Function1;
import com.linticator.markers.Message;
import com.linticator.view.console.AbstractLintConsole;

public abstract class AbstractLintRunner {
	private static final String MODULE_LINE_PREFIX = "---"; //$NON-NLS-1$
	private static final String SPACE = "  "; //$NON-NLS-1$
	private static final String ESC_NEW_LINE = " \\\n"; //$NON-NLS-1$
	protected ProjectConfig projectConfig;
	protected LaunchConfig launchConfig;
	private final AbstractLintConsole console;
	private final Semaphore mutex = new Semaphore(1);

	public AbstractLintRunner(final ProjectConfig projectConfig, LaunchConfig launchConfig,
			final AbstractLintConsole console) {
		this.launchConfig = launchConfig;
		this.console = console;
		this.projectConfig = projectConfig;
	}

	public void run(final IProgressMonitor monitor) throws IOException {
		monitor.beginTask("Lint Analyzation", IProgressMonitor.UNKNOWN);
		synchronized (this) {
			monitor.subTask("Running Lint...");
			outputLintCommandLineString();
			prepareOutputFile();
			monitor.worked(1);
			runLint(monitor);
			if (hasNoSourceFiles()) {
				outputNoSourceFilesWarning();
			}
		}
		monitor.done();
	}

	private void outputNoSourceFilesWarning() {
		final String msg = "Warning: No lintable source files were found. (Hint: Check the \"Lintable file extensions\" setting of your Lint configuration.)";
		console.print(msg);
	}

	private boolean hasNoSourceFiles() {
		return projectConfig.getFiles(projectConfig.getProject()).isEmpty();
	}

	private void outputLintCommandLineString() {
		console.print("Running the following lint command:\n");
		console.print(SPACE + getLintExecutable() + ESC_NEW_LINE);

		for (final String option : getLintCommandOptions()) {
			console.print(SPACE);
			final IPath filePath = Path.fromOSString(option);

			try {
				final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(filePath);
				if (file != null) {
					console.printLink(option, filePath);
				} else {
					console.print(option);
				}
			} catch (final IllegalArgumentException e) {
				console.print(option);
			}

			console.print(ESC_NEW_LINE);
		}

		console.print("\n");
		console.revealConsole();
	}

	protected void runLint(final IProgressMonitor monitor) throws IOException {
		final Process process = Runtime.getRuntime().exec(getLintCommand());
		monitor.worked(3);
		readAndPrint(process.getInputStream(), process, monitor);
	}

	private void readAndPrint(final InputStream inputStream, final Process process, final IProgressMonitor monitor) throws IOException {
		final BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
		String tmp = in.readLine();
		StringBuilder sb = new StringBuilder(tmp != null ? tmp : ""); //$NON-NLS-1$
		while ((tmp = in.readLine()) != null) {
			console.print(tmp);
			console.print("\n"); //$NON-NLS-1$
			if (tmp.startsWith(MODULE_LINE_PREFIX)) {
				runParseJob(sb, new Function1<Message, Void>() {
					@Override
					public Void apply(Message t) {
						onMessageCreated(t);
						return null;
					}
				});
				if (monitor.isCanceled()) {
					process.destroy();
					console.print("\n Lint Canceled\n"); //$NON-NLS-1$
					return;
				}
				monitor.subTask(tmp.substring(4));
				sb = new StringBuilder();
				monitor.worked(1);
			}
			sb.append(System.getProperty("line.separator")); //$NON-NLS-1$
			sb.append(tmp);
		}
		console.print("\n"); //$NON-NLS-1$
		runParseJob(sb, new Function1<Message, Void>() {
			@Override
			public Void apply(Message t) {
				onMessageCreated(t);
				return null;
			}
		});
	}

	private void prepareOutputFile() {
		if (launchConfig.isCaptureOutput() && launchConfig.getCaptureFile() != null) {
			if (launchConfig.isAppend()) {
				return;
			}
			try {
				File file = new File(launchConfig.getCaptureFile());
				if(!file.exists()) {
					return;
				}
				FileWriter writer = new FileWriter(file);
				writer.close();
			} catch (IOException e) {
				Linticator.getDefault().handleError(AbstractLintRunner.class.getName(), e);
			}
		}
	}

	private void onMessageCreated(Message message) {
		if (launchConfig.isCaptureOutput() && launchConfig.getCaptureFile() != null) {
			try {
				mutex.acquire();
			} catch (InterruptedException e) {
				Linticator.getDefault().handleError(AbstractLintRunner.class.getName(), e);
			}

			FileWriter fileWriter = null;

			try {
				fileWriter = new FileWriter(launchConfig.getCaptureFile(), true);
				BufferedWriter writer = new BufferedWriter(fileWriter);
				String formattedMessage = message.getFile() + ":" + String.valueOf(message.getLine()) + " " + message.getMessageLevel() + " "
						+ String.valueOf(message.getMessageCode()) + " " + message.getDescription();
				writer.write(formattedMessage);
				writer.write(System.lineSeparator());
				writer.close();
			} catch (IOException e) {
				Linticator.getDefault().handleError(AbstractLintRunner.class.getName(), e);
			} finally {
				mutex.release();
				if (fileWriter != null) {
					try {
						fileWriter.close();
					} catch (IOException e) {
						// Give up
					}
				}
			}
		}
	}

	abstract protected void runParseJob(final StringBuilder sb, Function1<Message, Void> onMessageCreated);

	protected String[] getLintCommand() {
		final ArrayList<String> fullCommandLine = new ArrayList<String>();

		fullCommandLine.add(getLintExecutable());
		Collections.addAll(fullCommandLine, getLintCommandOptions());

		return fullCommandLine.toArray(new String[] {});
	}

	protected String getLintExecutable() {
		try {
			return projectConfig.getLintExecutable().makeAbsolute().toString();
		} catch (final CdtVariableException e) {
			Linticator.getDefault().handleError("AbstractLintRunner", e);
			return "<could not resolve variable>";
		}
	}

	abstract protected String[] getLintCommandOptions();
}
