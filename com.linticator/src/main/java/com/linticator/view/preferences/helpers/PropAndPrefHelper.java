package com.linticator.view.preferences.helpers;

import org.eclipse.jface.preference.IPreferenceStore;

import com.linticator.Linticator;
import com.linticator.view.preferences.infrastructure.PropertyAndPreferenceHelper;

public class PropAndPrefHelper extends PropertyAndPreferenceHelper {

	private static final IPreferenceStore workspacePreferences = Linticator.getDefault().getPreferenceStore();

	@Override
	public IPreferenceStore getWorkspacePreferences() {
		return workspacePreferences;
	}

	@Override
	public String getPreferenceIdQualifier() {
		return "com.linticator";
	}
}
