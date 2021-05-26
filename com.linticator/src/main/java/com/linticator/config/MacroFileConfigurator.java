package com.linticator.config;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICLanguageSetting;
import org.eclipse.cdt.core.settings.model.ICLanguageSettingEntry;
import org.eclipse.cdt.core.settings.model.ICSettingEntry;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

import com.linticator.base.FileUtil;

class MacroFileConfigurator extends ConfigFileConfigurator {
	public static void updateMacroConfigFile(final File file, final IProject project) throws IOException {
		FileUtil.writeFile(file, getLintMacrosContent(project));
	}

	public static void emptyMacroConfigFile(final File file) throws IOException {
		FileUtil.writeFile(file, "");
	}

	private static String getLintMacrosContent(final IProject project) {
		final StringBuilder lintMacrosString = new StringBuilder();
		final String newLine = System.getProperty(LINE_SEPARATOR);

		// FIXME does that even make sense? Emanuel?
		for (final Map.Entry<String, String> entry : getMacros(project).entrySet()) {
			lintMacrosString.append("#define"); //$NON-NLS-1$
			lintMacrosString.append(" "); //$NON-NLS-1$
			lintMacrosString.append(entry.getKey());
			lintMacrosString.append(" "); //$NON-NLS-1$
			lintMacrosString.append(entry.getValue());
			lintMacrosString.append(newLine);
		}

		return lintMacrosString.toString();
	}

	static Map<String, String> getMacros(final IProject project) {

		final ICConfigurationDescription defaultSettingConfiguration = CoreModel.getDefault().getProjectDescription(project).getActiveConfiguration();

		final Map<String, String> collector = new LinkedHashMap<String, String>();

		for (String langId : getLanguages(project, defaultSettingConfiguration)) {
			List<ICLanguageSettingEntry> settingEntriesByKind = getSettingsEntries(project, defaultSettingConfiguration, langId);
			for (ICLanguageSettingEntry e : settingEntriesByKind) {
				collector.put(e.getName(), e.getValue());
			}
		}

		for (final ICLanguageSetting languageSetting : defaultSettingConfiguration.getRootFolderDescription().getLanguageSettings()) {
			for (final ICLanguageSettingEntry e : languageSetting.getSettingEntries(ICSettingEntry.MACRO)) {
				collector.put(e.getName(), e.getValue());
			}
		}

		return collector;
	}
	
	/*
	 * The next two methods use reflection so access some new Juno features we need to use. Pre-Juno, they won't work but this doesn't matter. 
	 * */

	@SuppressWarnings("unchecked")
	private static List<ICLanguageSettingEntry> getSettingsEntries(final IProject project, final ICConfigurationDescription defaultSettingConfiguration,
			String langId) {

		try {
			Class<?> languageSettingsProvidersSerializer = Class
					.forName("org.eclipse.cdt.internal.core.language.settings.providers.LanguageSettingsProvidersSerializer");

			Method getSettingEntriesByKind = languageSettingsProvidersSerializer.getMethod("getSettingEntriesByKind", ICConfigurationDescription.class,
					IResource.class, String.class, int.class);

			return (List<ICLanguageSettingEntry>) getSettingEntriesByKind.invoke(null, defaultSettingConfiguration, project, langId, ICSettingEntry.MACRO);

		} catch (ClassNotFoundException e) {
		} catch (NoSuchMethodException e) {
		} catch (SecurityException e) {
		} catch (IllegalAccessException e) {
		} catch (IllegalArgumentException e) {
		} catch (InvocationTargetException e) {
		}

		return new ArrayList<ICLanguageSettingEntry>();
	}

	@SuppressWarnings("unchecked")
	private static List<String> getLanguages(final IProject project, final ICConfigurationDescription defaultSettingConfiguration) {
		
		try {
			Class<?> languageSettingsManager = Class.forName("org.eclipse.cdt.core.language.settings.providers.LanguageSettingsManager");
			
			Method getLanguages = languageSettingsManager.getMethod("getLanguages", IResource.class, ICConfigurationDescription.class);
			
			return (List<String>) getLanguages.invoke(null, project, defaultSettingConfiguration);
			
		} catch (ClassNotFoundException e) {
		} catch (NoSuchMethodException e) {
		} catch (SecurityException e) {
		} catch (IllegalAccessException e) {
		} catch (IllegalArgumentException e) {
		} catch (InvocationTargetException e) {
		}
		
		return new ArrayList<String>();
	}
}
