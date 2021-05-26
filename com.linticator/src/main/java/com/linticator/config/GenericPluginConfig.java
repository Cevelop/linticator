package com.linticator.config;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.cdt.core.cdtvariables.CdtVariableException;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.preference.IPreferenceStore;

import com.linticator.base.VariablesUtil;

public class GenericPluginConfig implements PluginConfig {

	public static final String CONFIG_DIR = ".lint";

	public static Set<String> getFileExtensions(final String formattedExtensions) {
		final Set<String> extensions = new LinkedHashSet<String>();
		final String[] array = formattedExtensions.split(" "); //$NON-NLS-1$
		Collections.addAll(extensions, array);
		extensions.remove(""); //$NON-NLS-1$
		return extensions;
	}

	protected final IPreferenceStore preferenceStore;

	public GenericPluginConfig(final IPreferenceStore preferenceStore) {
		this.preferenceStore = preferenceStore;
	}

	@Override
	public Set<String> getFileExtensions() {
		return getFileExtensions(getProperty(PreferenceConstants.FILE_EXTENSIONS, preferenceStore));
	}

	@Override
	public IPath getLintCompilerConfig() throws CdtVariableException {
		return getPropertyAsPath(PreferenceConstants.LINT_COMPILER_CONFIG);
	}

	@Override
	public IPath getLintCompilerConfigHeader() throws CdtVariableException {
		return getPropertyAsPath(PreferenceConstants.LINT_COMPILER_CONFIG_HEADER);
	}

	@Override
	public IPath getLintDocumentationFile() throws CdtVariableException {
		return getPropertyAsPath(PreferenceConstants.LINT_DOCUMENTATION);
	}

	@Override
	public IPath getLintExecutable() throws CdtVariableException {
		return getPropertyAsPath(PreferenceConstants.LINT_EXECUTABLE);
	}

	private static String getProperty(final String property, final IPreferenceStore preferenceStore) {
		final String defaultConfigId = preferenceStore.getString(PreferenceConstants.LINT_DEFAULT_CONFIG);
		return preferenceStore.getString(defaultConfigId + property);
	}

	protected Path getPropertyAsPath(final String property) throws CdtVariableException {
		return new Path(VariablesUtil.resolveWorkspaceVariables(getProperty(property, preferenceStore)));
	}

	@Override
	public IFolder getConfigurationDirectory(final IProject project) {
		return project.getFolder(CONFIG_DIR);
	}

	public static boolean isConfiguredForGcc(final IPreferenceStore preferenceStore) {
		return !getProperty(PreferenceConstants.LINT_MAKE_FILE, preferenceStore).equals("");
	}
}