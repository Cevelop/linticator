package com.linticator.config;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICIncludePathEntry;
import org.eclipse.cdt.core.settings.model.ICLanguageSetting;
import org.eclipse.cdt.core.settings.model.ICLanguageSettingEntry;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.core.settings.model.ICSettingEntry;
import org.eclipse.cdt.core.settings.model.util.CDataUtil;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import com.linticator.base.Constants;
import com.linticator.base.FileUtil;
import com.linticator.base.VariablesUtil;

class ProjectConfigurator extends ConfigFileConfigurator {
	private static final String QUOTE = "\"";

	public static void updateProjectConfigFile(final File projectConfigFile, final ProjectConfig project)
			throws IOException, CoreException {
		FileUtil.writeFile(projectConfigFile, getProjectConfigContent(project));
	}

	private static String getProjectConfigContent(final ProjectConfig projectConfig) throws CoreException {
		final StringBuilder projectConfigOutputString = new StringBuilder();
		final String newLine = System.getProperty(LINE_SEPARATOR);

		createIncludesPart(projectConfig.getProject(), projectConfigOutputString, newLine);
		if (projectConfig.usePredefinedCompilerSymbols()) {
			createDefinitionsPart(projectConfig.getProject(), projectConfigOutputString, newLine);
		}
		createLibraryMessagePart(projectConfigOutputString, newLine, projectConfig);
		createOwnLintFilePart(projectConfigOutputString, newLine, projectConfig);
		createCustomArgumentsPart(projectConfigOutputString, newLine, projectConfig);

		return projectConfigOutputString.toString();
	}

	private static void createOwnLintFilePart(final StringBuilder projectConfigOutputString, final String newLine,
			final ProjectConfig projectConfig) throws CoreException {
		projectConfigOutputString.append(newLine);
		projectConfigOutputString.append("// Own Lint file");
		final String customFile = projectConfig.getCustomLintFile();
		if (!customFile.equals("")) {
			projectConfigOutputString.append(newLine);
			projectConfigOutputString.append(QUOTE);
			projectConfigOutputString.append(VariablesUtil.resolveProjectVariables(projectConfig, customFile));
			projectConfigOutputString.append(QUOTE);
		}
	}

	private static void createCustomArgumentsPart(final StringBuilder projectConfigOutputString, final String newLine,
			final ProjectConfig projectConfig) throws CoreException {
		projectConfigOutputString.append(newLine);
		projectConfigOutputString.append("// Custom Lint arguments");
		final String customLintArguments = projectConfig.getCustomLintArgumentsPropertyValue();
		if (!customLintArguments.equals("")) {
			projectConfigOutputString.append(newLine);
			projectConfigOutputString.append(VariablesUtil.resolveProjectVariables(projectConfig, customLintArguments));
		}
		if (projectConfig.useCpp11()) {
			projectConfigOutputString.append(newLine);
			projectConfigOutputString.append("-A(C++2011)");
		}
		if (projectConfig.useCpp14()) {
			projectConfigOutputString.append(newLine);
			projectConfigOutputString.append("-A(C++2014)");
		}
		if (projectConfig.useC11()) {
			projectConfigOutputString.append(newLine);
			projectConfigOutputString.append("-A(C2011)");
		}
	}

	private static void createLibraryMessagePart(final StringBuilder projectConfigOutputString, final String newLine,
			final ProjectConfig projectConfig) throws CoreException {
		projectConfigOutputString.append(newLine);
		projectConfigOutputString.append("// Library message level"); //$NON-NLS-1$
		projectConfigOutputString.append(newLine);
		projectConfigOutputString.append(Constants.WLIB_START);
		projectConfigOutputString.append(projectConfig.getMessageLevelPropertyValue());
		projectConfigOutputString.append(Constants.WLIB_END);
		projectConfigOutputString.append(newLine);
	}

	private static void createIncludesPart(final IProject project, final StringBuilder projectConfigOutputString,
			final String newLine) {
		projectConfigOutputString.append(newLine);
		projectConfigOutputString.append("// Includes"); //$NON-NLS-1$
		projectConfigOutputString.append(newLine);
		for (final String include : getIncludes(project)) {
			projectConfigOutputString.append(Constants.INCLUDE_INSTRUCTION);
			projectConfigOutputString.append(QUOTE + include + QUOTE);
			projectConfigOutputString.append(newLine);
		}
	}

	private static void createDefinitionsPart(final IProject project, final StringBuilder projectConfigOutputString,
			final String newLine) {
		projectConfigOutputString.append(newLine);
		projectConfigOutputString.append("// Definitions (taken from the project's CDT settings)"); //$NON-NLS-1$
		projectConfigOutputString.append(newLine);
		for (final Entry<String, String> e : MacroFileConfigurator.getMacros(project).entrySet()) {

			if (hasQuotesInside(e.getValue())) {
				continue;
			}

			String value = e.getValue();
			String key = e.getKey();
			String definitionKey = "-d";

			if (value.startsWith(QUOTE) && value.endsWith(QUOTE)) {
				definitionKey += key;
				if (!value.isEmpty()) {
					definitionKey += "=" + value;
				}
			} else {
				definitionKey += QUOTE + key;
				if (!value.isEmpty()) {
					definitionKey += "=" + value;
				}
				definitionKey += QUOTE;
			}

			projectConfigOutputString.append(definitionKey);
			projectConfigOutputString.append(newLine);
		}
	}

	private static boolean hasQuotesInside(final String e) {
		return e.length() > 2 && e.substring(1, e.length() - 2).contains(QUOTE);
	}

	private static Set<String> getIncludes(final IProject project) {

		final HashSet<String> result = new LinkedHashSet<String>();

		final ICProjectDescription projectDescription = CoreModel.getDefault().getProjectDescription(project);
		final ICConfigurationDescription activeConfiguration = projectDescription.getActiveConfiguration();

		for (final ICLanguageSetting languageSetting : activeConfiguration.getRootFolderDescription().getLanguageSettings()) {
			ICLanguageSettingEntry[] settingEntries = languageSetting.getSettingEntries(ICSettingEntry.INCLUDE_PATH);
			ICLanguageSettingEntry[] resolvedEntries = CDataUtil.resolveEntries(settingEntries, activeConfiguration);
			for (final ICSettingEntry icSettingEntry : resolvedEntries) {
				if (icSettingEntry instanceof ICIncludePathEntry) {
					ICIncludePathEntry iIncludePathEntry = (ICIncludePathEntry) icSettingEntry;
					result.add(iIncludePathEntry.getLocation().toOSString());
				}
			}
		}

		return result;
	}
}
