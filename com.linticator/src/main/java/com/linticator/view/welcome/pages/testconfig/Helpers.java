package com.linticator.view.welcome.pages.testconfig;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.cdt.core.CProjectNature;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescriptionManager;
import org.eclipse.cdt.managedbuilder.core.IBuilder;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IProjectType;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.internal.core.Configuration;
import org.eclipse.cdt.managedbuilder.internal.core.ManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.internal.core.ManagedProject;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.linticator.LintNature;

@SuppressWarnings("restriction")
class Helpers {

	static void createMainFile(final IProject project) throws CoreException {

		final IFile mainFile = getTestSourceFile(project);
		final String src = "int main(int argc, char** argv) {\n" + "  int unused;\n" + "  return 0;\n" + "}\n";
		try {
			mainFile.create(new ByteArrayInputStream(src.getBytes("UTF-8")), true, new NullProgressMonitor());
		} catch (final UnsupportedEncodingException e) {
			// ignore
		}
	}

	static IFile getTestSourceFile(final IProject project) {
		return project.getFile("main.cpp");
	}

	static IProject createEmptyProject(final String name) throws CoreException {

		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		final IProject project = root.getProject(name);
		project.create(null);
		project.open(null);

		return project;
	}

	static void addLinticatorNature(final IProject project) throws CoreException {
		CProjectNature.addNature(project, LintNature.NATURE_ID, new NullProgressMonitor());
	}

	static void makeManagedCdtProject(final IProject project) throws CoreException {
		CProjectNature.addCNature(project, null);
		final ICProjectDescriptionManager mgr = CoreModel.getDefault().getProjectDescriptionManager();
		ICProjectDescription des = mgr.getProjectDescription(project, true);

		if (des != null)
			return; // C project description already exists

		des = mgr.createProjectDescription(project, true);

		final ManagedBuildInfo info = ManagedBuildManager.createBuildInfo(project);
		final IProjectType projType = ManagedBuildManager.getExtensionProjectType("cdt.managedbuild.target.gnu.exe");
		
		if(projType == null) {
			throw new UnsupportedOperationException();
		}
		
		final IToolChain toolChain = ManagedBuildManager
				.getExtensionToolChain("cdt.managedbuild.toolchain.gnu.exe.release");

		final ManagedProject mProj = new ManagedProject(project, projType);
		info.setManagedProject(mProj);

		final IConfiguration[] configs = ManagedBuildManager.getExtensionConfigurations(toolChain, projType);

		for (final IConfiguration icf : configs) {
			if (!(icf instanceof Configuration)) {
				continue;
			}
			final Configuration cf = (Configuration) icf;

			final String id = ManagedBuildManager.calculateChildId(cf.getId(), null);
			final Configuration config = new Configuration(mProj, cf, id, false, true);

			final ICConfigurationDescription cfgDes = des.createConfiguration(ManagedBuildManager.CFG_DATA_PROVIDER_ID,
					config.getConfigurationData());
			config.setConfigurationDescription(cfgDes);
			config.exportArtifactInfo();

			final IBuilder bld = config.getEditableBuilder();
			if (bld != null) {
				bld.setManagedBuildOn(true);
			}

			config.setName(toolChain.getName());
			config.setArtifactName(project.getName());

		}

		mgr.setProjectDescription(project, des);
	}

	public static boolean existsProject(final String projectName) throws CoreException {
		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		return root.getProject(projectName).exists();
	}

	public static void deleteProject(final String projectName) throws CoreException {
		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		final IProject project = root.getProject(projectName);
		if (project.exists())
			project.delete(true, new NullProgressMonitor());
	}
}
