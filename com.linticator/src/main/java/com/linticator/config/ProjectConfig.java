package com.linticator.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import org.eclipse.cdt.core.cdtvariables.CdtVariableException;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.core.settings.model.ICSourceEntry;
import org.eclipse.cdt.core.settings.model.util.CDataUtil;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.preference.IPreferenceStore;

import com.linticator.base.VariablesUtil;
import com.linticator.view.preferences.helpers.PropAndPrefHelper;
import com.linticator.view.preferences.infrastructure.PropertyAndPreferenceHelper;

public class ProjectConfig implements PluginConfig {
	private static final String GCC_MACROS = "macros.h"; //$NON-NLS-1$
	private static final String COMPILER_CONFIG_HEADER_H = "compiler-config-header.h"; //$NON-NLS-1$
	private static final String COMPILER_CONFIG_LNT = "compiler-config.lnt"; //$NON-NLS-1$
	private static final String PROJECT_CONFIG_LNT = "project-config.lnt"; //$NON-NLS-1$
	private static final String FILES_LNT = "files.lnt"; //$NON-NLS-1$
	private static final String PROJECT_CONFIG_DIR = ".lint/"; //$NON-NLS-1$
	private static final String SIZE_OPTIONS_FILE = "size-options.lnt"; //$NON-NLS-1$

	private final IProject project;
	private final PluginConfig underlyingPluginConfig;
	private final PropertyAndPreferenceHelper helper = new PropAndPrefHelper();

	public ProjectConfig(final IProject project) {
		this.project = project;
		final IPreferenceStore preferenceStore = getPreferenceStore();
		if (GenericPluginConfig.isConfiguredForGcc(preferenceStore)) {
			underlyingPluginConfig = new GccPluginConfig(preferenceStore);
		} else {
			underlyingPluginConfig = new GenericPluginConfig(preferenceStore);
		}
	}

	public IPreferenceStore getPreferenceStore() {
		if (project != null && helper.projectSpecificPreferencesEnabled(project)) {
			return helper.getProjectPreferences(project);
		} else {
			return helper.getWorkspacePreferences();
		}
	}

	public String getCustomLintFile() throws CoreException {
		return VariablesUtil.resolveProjectVariables(this, getCustomLintFilePropertyValue());
	}

	public String getCustomLintFilePropertyValue() throws CoreException {
		final String value = helper.getProjectString(PreferenceConstants.CUSTOM_LINT_FILE_PROPKEY, project);
		return (value == null) ? "" : value; //$NON-NLS-1$
	}

	public void setCustomLintFilePropertyValue(final String customLintFile) throws CoreException {
		helper.setProjectValue(PreferenceConstants.CUSTOM_LINT_FILE_PROPKEY, customLintFile, project);
	}

	public String getCustomLintArgumentsPropertyValue() throws CoreException {
		final String value = helper.getProjectString(PreferenceConstants.CUSTOM_LINT_ARGUMENTS_PROPKEY, project);
		return (value == null) ? "" : value; //$NON-NLS-1$
	}

	public void setCustomLintArgumentsPropertyValue(final String customLintArgs) throws CoreException {
		helper.setProjectValue(PreferenceConstants.CUSTOM_LINT_ARGUMENTS_PROPKEY, customLintArgs, project);
	}

	public boolean useCpp11() throws CoreException {
		final String value = helper.getProjectString(PreferenceConstants.USE_CPP11, project);
		return value != null && value.equals("true");
	}

	public void setUseCpp11(final boolean value) throws CoreException {
		helper.setProjectValue(PreferenceConstants.USE_CPP11, String.valueOf(value), project);
	}

	public boolean useCpp14() throws CoreException {
		final String value = helper.getProjectString(PreferenceConstants.USE_CPP14, project);
		return value != null && value.equals("true");
	}

	public void setUseCpp14(final boolean value) throws CoreException {
		helper.setProjectValue(PreferenceConstants.USE_CPP14, String.valueOf(value), project);
	}

	public void sethasCustomLintConfig(final boolean value) {
		helper.setProjectSpecificPreferences(project, value);
	}

	public boolean useC11() throws CoreException {
		final String value = helper.getProjectString(PreferenceConstants.USE_C11, project);
		return value != null && value.equals("true");
	}

	public void setUseC11(final boolean value) throws CoreException {
		helper.setProjectValue(PreferenceConstants.USE_C11, String.valueOf(value), project);
	}

	public boolean usePredefinedCompilerSymbols() throws CoreException {
		final String value = helper.getProjectString(PreferenceConstants.USE_PREDEFINED_COMPILER_SYMBOLS, project);
		return value == null || value.equals("true");
	}

	public void setUsePredefinedCompilerSymbols(final boolean value) throws CoreException {
		helper.setProjectValue(PreferenceConstants.USE_PREDEFINED_COMPILER_SYMBOLS, String.valueOf(value), project);
	}

	IPath getSizeOptionsFile() {
		return project.getLocation().append(PROJECT_CONFIG_DIR + SIZE_OPTIONS_FILE);
	}

	public int getMessageLevelPropertyValue() throws CoreException {
		final String value = helper.getProjectString(PreferenceConstants.MESSAGE_LEVEL_PROPKEY, project);
		try {
			return Integer.parseInt(value);
		} catch (final NumberFormatException e) {
			return 1 /* the lint default is 3, but this is too verbose for our taste */;
		}
	}

	public void setMessageLevelPropertyValue(final int messageLevel) throws CoreException {
		helper.setProjectValue(PreferenceConstants.MESSAGE_LEVEL_PROPKEY, String.valueOf(messageLevel), project);
	}

	public void updateConfig(final IResource[] startingPoints) throws IOException, CoreException {
		if (getProject() != null && getProject().exists()) {
			FileListConfigurator.updateFileListConfigFile(this, getFileListFile().toFile(), startingPoints);
			ProjectConfigurator.updateProjectConfigFile(getProjectConfigFile().toFile(), this);

			final IPath configHeaderFile = project.getLocation().append(PROJECT_CONFIG_DIR + COMPILER_CONFIG_HEADER_H);
			CompilerConfigConfigurator.updateCompilerConfigFile(getCompilerConfigFile().toFile(), this, configHeaderFile);

			final IPath macroFile = project.getLocation().append(PROJECT_CONFIG_DIR + GCC_MACROS);

			if (usePredefinedCompilerSymbols()) {
				MacroFileConfigurator.updateMacroConfigFile(macroFile.toFile(), project);
			} else {
				MacroFileConfigurator.emptyMacroConfigFile(macroFile.toFile());
			}

			final IPath lintCompilerConfigHeader = getLintCompilerConfigHeader();
			CompilerConfigHeaderConfigurator.updateCompilerConfigHeader(configHeaderFile.toFile(), this, macroFile, lintCompilerConfigHeader);
		}
	}

	public IPath getFileListFile() {
		return project.getLocation().append(PROJECT_CONFIG_DIR + FILES_LNT);
	}

	public IPath getProjectConfigFile() {
		return project.getLocation().append(PROJECT_CONFIG_DIR + PROJECT_CONFIG_LNT);
	}

	public IPath getCompilerConfigFile() {
		return project.getLocation().append(PROJECT_CONFIG_DIR + COMPILER_CONFIG_LNT);
	}

	public IProject getProject() {
		return project;
	}

	public ArrayList<IResource> getFiles(final IResource startingPoint) {

		class FindAllRelevantResources implements IResourceVisitor {

			final ArrayList<IResource> foundFiles = new ArrayList<IResource>();

			private final ICSourceEntry[] sourceEntries;
			private final Set<String> fileExtensions = getFileExtensions();

			public FindAllRelevantResources(final IProject p) {
				final ICProjectDescription projectDescription = CoreModel.getDefault().getProjectDescription(p);
				final ICConfigurationDescription activeConfiguration = projectDescription.getActiveConfiguration();
				final IConfiguration configuration = ManagedBuildManager.getConfigurationForDescription(activeConfiguration);

				if (configuration != null) {
					sourceEntries = configuration.getSourceEntries();
				} else {
					sourceEntries = new ICSourceEntry[0];
				}
			}

			@Override
			public boolean visit(final IResource r) throws CoreException {
				if (fileExtensions.contains(r.getFileExtension()) && !isExcluded(r)) {
					foundFiles.add(r);
				}
				return true;
			}

			private boolean isExcluded(final IResource r) {
				if (sourceEntries.length > 0) {
					return CDataUtil.isExcluded(r.getProjectRelativePath(), sourceEntries);
				} else {
					// In some circumstances (see #63) the configuration cannot be obtained be null,
					// so we cannot detected excluded resources.
					return false;
				}
			}
		}

		final FindAllRelevantResources visitor = new FindAllRelevantResources(project);
		try {
			startingPoint.accept(visitor);
		} catch (final CoreException ignored) {
		}
		return visitor.foundFiles;
	}

	@Override
	public Set<String> getFileExtensions() {
		return underlyingPluginConfig.getFileExtensions();
	}

	@Override
	public IPath getLintCompilerConfig() throws CdtVariableException {
		return underlyingPluginConfig.getLintCompilerConfig();
	}

	@Override
	public IPath getLintCompilerConfigHeader() throws CdtVariableException {
		return underlyingPluginConfig.getLintCompilerConfigHeader();
	}

	@Override
	public IPath getLintDocumentationFile() throws CdtVariableException {
		return underlyingPluginConfig.getLintDocumentationFile();
	}

	@Override
	public IPath getLintExecutable() throws CdtVariableException {
		return underlyingPluginConfig.getLintExecutable();
	}

	@Override
	public IFolder getConfigurationDirectory(final IProject project) {
		return underlyingPluginConfig.getConfigurationDirectory(project);
	}

	public PluginConfig getUnderlyingConfig() throws CdtVariableException {
		return underlyingPluginConfig;
	}

}
