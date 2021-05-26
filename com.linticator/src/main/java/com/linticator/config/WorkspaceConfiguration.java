package com.linticator.config;

import java.util.UUID;

import org.eclipse.jface.preference.IPreferenceStore;

import com.linticator.Linticator;

public class WorkspaceConfiguration {
	public static String createNewConfigurationId() {
		return UUID.randomUUID().toString();
	}

	public static boolean hasDefaultConfiguration() {
		return !store().getString(PreferenceConstants.LINT_DEFAULT_CONFIG).equals("");
	}

	public static boolean askForConfiguration() {
		final boolean showScreen = System.getProperty("HideLintWelcomeScreen") == null;
		return showScreen && !hasDefaultConfiguration()
				&& !store().getBoolean(PreferenceConstants.LINT_DONT_ASK_FOR_CONFIGURATION);
	}

	public static void disableAskingForConfiguration() {
		store().setValue(PreferenceConstants.LINT_DONT_ASK_FOR_CONFIGURATION, true);
	}

	public static void setRunLintAfterBuild(boolean value) {
		store().setValue(PreferenceConstants.LINT_AUTOMATICALLY_AFTER_BUILD, value);
	}

	public static boolean getRunLintAfterBuild() {
		if (!store().contains(PreferenceConstants.LINT_AUTOMATICALLY_AFTER_BUILD)) {
			store().setDefault(PreferenceConstants.LINT_AUTOMATICALLY_AFTER_BUILD, true);
			setRunLintAfterBuild(true);
		}
		return store().getBoolean(PreferenceConstants.LINT_AUTOMATICALLY_AFTER_BUILD);
	}

	private static IPreferenceStore store() {
		return Linticator.getDefault().getPreferenceStore();
	}
}
