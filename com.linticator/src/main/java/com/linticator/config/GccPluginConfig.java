package com.linticator.config;

import org.eclipse.cdt.core.cdtvariables.CdtVariableException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.preference.IPreferenceStore;

public class GccPluginConfig extends GenericPluginConfig {

	public GccPluginConfig(final IPreferenceStore preferenceStore) {
		super(preferenceStore);
	}

	public IPath getMakeFile() throws CdtVariableException {
		return getPropertyAsPath(PreferenceConstants.LINT_MAKE_FILE);
	}
}
