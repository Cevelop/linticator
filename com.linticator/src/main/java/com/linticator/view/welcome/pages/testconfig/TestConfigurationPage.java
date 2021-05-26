package com.linticator.view.welcome.pages.testconfig;

import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;

public class TestConfigurationPage extends WizardPage {

	TestConfigurationComposite childComposite;

	final class TestConfigChild extends TestConfigurationComposite {

		private final class TestJob extends Job {
			private TestJob(final String name) {
				super(name);
			}

			@Override
			protected IStatus run(final IProgressMonitor monitor) {

				removeOldTestProjects();

				if (monitor.isCanceled())
					return Status.OK_STATUS;

				out.emptyOutputArea();
				pageComplete(false);

				if (monitor.isCanceled())
					return Status.OK_STATUS;

				configurationTester.checkConfiguration(monitor);

				pageComplete(true);

				setRunButtonText("Run Test");

				return Status.OK_STATUS;
			}
		}

		final StyledTextPrinter out = new StyledTextPrinter(getOutputArea());

		final ConfigurationTester configurationTester = new ConfigurationTester(out, PROJECT_NAME, getDisplay());

		Job currentlyRunningJob = null;

		TestConfigChild(final Composite parent, final int style) {
			super(parent, style);
		}

		private void pageComplete(final boolean b) {
			getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					setPageComplete(b);
				}
			});
		}

		@Override
		protected void runTest() {
			if ((noJobRunning()))
				runJob();
			else
				cancelJob();
		}

		private void runJob() {
			currentlyRunningJob = new TestJob("Lint Configuration Tester");

			setRunButtonText("Cancel");

			currentlyRunningJob.schedule();
		}

		private void cancelJob() {
			currentlyRunningJob.cancel();

			try {
				currentlyRunningJob.join();
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}

			setRunButtonText("Run Test");
			currentlyRunningJob = null;
		}

		private boolean noJobRunning() {
			return currentlyRunningJob == null || currentlyRunningJob.getResult() != null;
		}

		private void setRunButtonText(final String txt) {
			getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					getRunButton().setText(txt);
				}
			});
		}

		void removeOldTestProjects() {
			try {
				if (Helpers.existsProject(PROJECT_NAME)) {
					out.println("Removing already existing test project.");
					Helpers.deleteProject(PROJECT_NAME);
				}
			} catch (final CoreException e) {
				e.printStackTrace();
			}
		}

		@Override
		protected void deleteTestProject(final boolean selected) {
			deleteProjectOnFinish = selected;
		}

		@Override
		protected void saveLog() {
			final FileDialog dialog = new FileDialog(getShell(), SWT.SAVE);
			dialog.setFileName("linticator.log");
			final String filename = dialog.open();

			if (filename == null)
				return;

			FileWriter writer = null;
			try {
				writer = new FileWriter(filename);
				writer.write(childComposite.getOutputArea().getText());
			} catch (final IOException e) {
				System.err.println("Exception occured: File not saved!");
			} finally {
				try {
					writer.close();
				} catch (final Exception e) {
				}
			}
		}
	}

	static final String PROJECT_NAME = "LinticatorConfigurationTest";

	public boolean deleteProjectOnFinish = true;

	private final String pageName;

	public TestConfigurationPage(final String title) {
		super(title);
		pageName = title;
	}

	@Override
	public void createControl(final Composite parent) {

		setTitle(pageName);
		setMessage("Configuration Test");

		childComposite = new TestConfigChild(parent, SWT.NONE);
		setControl(childComposite);
	}

	public void deleteCreatedProjectIfEnabled() {
		if (deleteProjectOnFinish) {
			try {
				Helpers.deleteProject(TestConfigurationPage.PROJECT_NAME);
			} catch (final CoreException e) {
				e.printStackTrace();
			}
		}
	}
}
