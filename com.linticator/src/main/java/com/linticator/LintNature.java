package com.linticator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

import com.linticator.lint.LintBuilder;
import com.linticator.lint.configurator.LintConfigBuilder;
import com.linticator.markers.Message;

public class LintNature implements IProjectNature {

	/**
	 * ID of this project nature
	 */
	public static final String NATURE_ID = "com.linticator.Nature"; //$NON-NLS-1$

	private IProject project;

	public static void addLintNature(final IProject project, final IProgressMonitor mon) throws CoreException {
		addNature(project, NATURE_ID, mon, true);
	}

	public static void addLintNatureWithoutBuilding(final IProject project, final IProgressMonitor mon) throws CoreException {
		addNature(project, NATURE_ID, mon, false);
	}

	public static void removeLintNature(final IProject project, final IProgressMonitor mon) throws CoreException {
		removeNature(project, NATURE_ID, mon);
	}

	private static void addNature(final IProject project, final String natureId, final IProgressMonitor monitor, boolean build) throws CoreException {
		final IProjectDescription description = project.getDescription();
		final String[] prevNatures = description.getNatureIds();
		for (int i = 0; i < prevNatures.length; i++)
			if (natureId.equals(prevNatures[i]))
				return;
		final String[] newNatures = new String[prevNatures.length + 1];
		System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);
		newNatures[prevNatures.length] = natureId;
		description.setNatureIds(newNatures);
		project.setDescription(description, monitor);
		if (build) {
			project.build(IncrementalProjectBuilder.FULL_BUILD, SubMonitor.convert(monitor, 1));
		}
	}

	private static void removeNature(final IProject project, final String natureId, final IProgressMonitor monitor) throws CoreException {
		final IProjectDescription description = project.getDescription();
		final String[] prevNatures = description.getNatureIds();
		final List<String> newNatures = new ArrayList<String>(Arrays.asList(prevNatures));
		newNatures.remove(natureId);
		description.setNatureIds(newNatures.toArray(new String[newNatures.size()]));
		project.setDescription(description, monitor);
		project.accept(new IResourceVisitor() {

			@Override
			public boolean visit(final IResource resource) throws CoreException {
				Message.eraseLintMarkers(resource);
				return true;
			}
		});
	}

	@Override
	public void configure() throws CoreException {
		configureBuilder(LintConfigBuilder.ID);
		configureBuilder(LintBuilder.ID);
	}

	private void configureBuilder(final String builderID) throws CoreException {
		final IProjectDescription desc = project.getDescription();
		final ICommand[] commands = desc.getBuildSpec();

		for (int i = 0; i < commands.length; ++i)
			if (commands[i].getBuilderName().equals(builderID))
				return;

		final ICommand[] newCommands = new ICommand[commands.length + 1];
		System.arraycopy(commands, 0, newCommands, 0, commands.length);
		final ICommand command = desc.newCommand();
		command.setBuilderName(builderID);
		newCommands[newCommands.length - 1] = command;
		desc.setBuildSpec(newCommands);
		project.setDescription(desc, null);
	}

	@Override
	public void deconfigure() throws CoreException {
		final IProjectDescription description = getProject().getDescription();
		final ICommand[] commands = description.getBuildSpec();
		deconfigureBuilder(commands, description, LintConfigBuilder.ID);
	}

	private void deconfigureBuilder(final ICommand[] commands, final IProjectDescription description, final String builderId) {
		for (int i = 0; i < commands.length; ++i)
			if (commands[i].getBuilderName().equals(builderId)) {
				final ICommand[] newCommands = new ICommand[commands.length - 1];
				System.arraycopy(commands, 0, newCommands, 0, i);
				System.arraycopy(commands, i + 1, newCommands, i, commands.length - i - 1);
				description.setBuildSpec(newCommands);
				return;
			}
	}

	@Override
	public IProject getProject() {
		return project;
	}

	@Override
	public void setProject(final IProject project) {
		this.project = project;
	}

}
