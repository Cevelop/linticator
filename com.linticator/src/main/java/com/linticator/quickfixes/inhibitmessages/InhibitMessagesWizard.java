package com.linticator.quickfixes.inhibitmessages;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.linticator.Linticator;
import com.linticator.config.ProjectConfig;
import com.linticator.documentation.IDocumentation;
import com.linticator.functional.Function1;
import com.linticator.lint.LintProjectJob;

public class InhibitMessagesWizard extends org.eclipse.jface.wizard.Wizard {

	public static void openWizard(final IDocument document, final Collection<IMarker> markers) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				final InhibitMessagesWizard wizard = new InhibitMessagesWizard(document, markers);
				final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				final WizardDialog dialog = new WizardDialog(shell, wizard);
				dialog.create();
				dialog.open();
			}
		});
	}

	private final Collection<MessageInhibitionConfigurationEntry> configuration;
	private final IDocument document;

	private InhibitionOptionsTablePage page;

	public InhibitMessagesWizard(final IDocument document, final Collection<IMarker> markers) {
		this.document = document;
		this.configuration = new ArrayList<MessageInhibitionConfigurationEntry>();
		for (final IMarker m : sortByAscendingMessageId(markers)) {
			configuration.add(new MessageInhibitionConfigurationEntry(document, m, getDocumentation()));
		}

		setWindowTitle("Inhibit Messages");
	}

	private Collection<IMarker> sortByAscendingMessageId(final Collection<IMarker> markersOnInvocationLine) {
		final ArrayList<IMarker> result = new ArrayList<IMarker>(markersOnInvocationLine);
		Collections.sort(result, new Comparator<IMarker>() {
			@Override
			public int compare(final IMarker o1, final IMarker o2) {
				return o1.getAttribute(IMarker.PROBLEM, 0) - o2.getAttribute(IMarker.PROBLEM, 0);
			}
		});
		return result;
	}

	private IDocumentation getDocumentation() {
		return Linticator.getBeans().getDocumentation();
	}

	@Override
	public void addPages() {
		page = new InhibitionOptionsTablePage(configuration);
		addPage(page);

		if (!hasCustomLintFileConfigured()) {
			addPage(new CustomLintFileWizardPage(new ProjectConfig(getProject())));
		}
	}

	private String getCustomLintFile() {
		try {
			return new ProjectConfig(getProject()).getCustomLintFile();
		} catch (final CoreException e) {
			// Project exists and is open
			return null;
		}
	}

	private IProject getProject() {
		return configuration.iterator().next().getProject();
	}

	private boolean hasCustomLintFileConfigured() {
		return !(getCustomLintFile().equals(""));
	}

	@Override
	public boolean performFinish() {
		writeInhibitionOptions();
		runLintIfNecessary();
		return true;
	}

	private void runLintIfNecessary() {
		if (page.getRunLinticatorAfterwards()) {
			new LintProjectJob(getProject()).schedule();
		}
	}

	private void writeInhibitionOptions() {

		final IProject project = getProject();

		final Job job = new Job("Configuring Lint Inhibition Options") {

			@Override
			protected IStatus run(final IProgressMonitor monitor) {

				InhibitMessages.withCustomLintFile(project, new Function1<PrintWriter, Void>() {
					@Override
					public Void apply(final PrintWriter writer) {
						InhibitMessages.writeMessageInhibitionOptions(writer, document, configuration,
								getDocumentation());
						return null;
					}
				});

				return Status.OK_STATUS;
			}
		};

		job.setRule(project);

		job.schedule();
	}
}
