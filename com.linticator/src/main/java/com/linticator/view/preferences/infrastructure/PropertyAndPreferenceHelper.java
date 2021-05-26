package com.linticator.view.preferences.infrastructure;

import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;


public abstract class PropertyAndPreferenceHelper implements IPropertyAndPreferenceHelper {
	private IPersistentPreferenceStore projectPreferences;
	private IProject currentProject;

	@Override
	public IPreferenceStore getProjectPreferences(final IProject project) {
		if (!project.equals(currentProject)) {
			if (projectPreferences != null) {
				try {
					projectPreferences.save();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			projectPreferences = new ScopedPreferenceStore(new ProjectScope(project), getPreferenceIdQualifier());
		}
		return projectPreferences;
	}

	@Override
	public String getDefaultPreferenceIdQualifier() {
		return getPreferenceIdQualifier();
	}

	@Override
	public boolean getBoolean(final String name, final IProject project) {
		if (projectSpecificPreferencesEnabled(project)) {
			return getProjectPreferences(project).getBoolean(name);
		} else {
			return getWorkspacePreferences().getBoolean(name);
		}
	}

	@Override
	public boolean getProjectBoolean(String name, IProject project) {
		return getProjectPreferences(project).getBoolean(name);
	}

	@Override
	public boolean getDefaultBoolean(final String name, final IProject project) {
		if (projectSpecificPreferencesEnabled(project)) {
			return getProjectPreferences(project).getDefaultBoolean(name);
		} else {
			return getWorkspacePreferences().getDefaultBoolean(name);
		}
	}

	@Override
	public boolean getProjectDefaultBoolean(String name, IProject project) {
		return getProjectPreferences(project).getDefaultBoolean(name);
	}

	@Override
	public double getDouble(final String name, final IProject project) {
		if (projectSpecificPreferencesEnabled(project)) {
			return getProjectPreferences(project).getDouble(name);
		} else {
			return getWorkspacePreferences().getDouble(name);
		}
	}

	@Override
	public double getProjectDouble(String name, IProject project) {
		return getProjectPreferences(project).getDouble(name);
	}

	@Override
	public double getDefaultDouble(final String name, final IProject project) {
		if (projectSpecificPreferencesEnabled(project)) {
			return getProjectPreferences(project).getDefaultDouble(name);
		} else {
			return getWorkspacePreferences().getDefaultDouble(name);
		}
	}

	@Override
	public double getProjectDefaultDouble(String name, IProject project) {
		return getProjectPreferences(project).getDefaultDouble(name);
	}

	@Override
	public float getFloat(final String name, final IProject project) {
		if (projectSpecificPreferencesEnabled(project)) {
			return getProjectPreferences(project).getFloat(name);
		} else {
			return getWorkspacePreferences().getFloat(name);
		}
	}

	@Override
	public float getProjectFloat(String name, IProject project) {
		return getProjectPreferences(project).getFloat(name);
	}

	@Override
	public float getDefaultFloat(final String name, final IProject project) {
		if (projectSpecificPreferencesEnabled(project)) {
			return getProjectPreferences(project).getDefaultFloat(name);
		} else {
			return getWorkspacePreferences().getDefaultFloat(name);
		}
	}

	@Override
	public float getProjectDefaultFloat(String name, IProject project) {
		return getProjectPreferences(project).getDefaultFloat(name);
	}

	@Override
	public int getInt(final String name, final IProject project) {
		if (projectSpecificPreferencesEnabled(project)) {
			return getProjectPreferences(project).getInt(name);
		} else {
			return getWorkspacePreferences().getInt(name);
		}
	}

	@Override
	public int getProjectInt(String name, IProject project) {
		return getProjectPreferences(project).getInt(name);
	}

	@Override
	public int getDefaultInt(final String name, final IProject project) {
		if (projectSpecificPreferencesEnabled(project)) {
			return getProjectPreferences(project).getDefaultInt(name);
		} else {
			return getWorkspacePreferences().getDefaultInt(name);
		}
	}

	@Override
	public int getProjectDefaultInt(String name, IProject project) {
		return getProjectPreferences(project).getDefaultInt(name);
	}

	@Override
	public long getLong(final String name, final IProject project) {
		if (projectSpecificPreferencesEnabled(project)) {
			return getProjectPreferences(project).getLong(name);
		} else {
			return getWorkspacePreferences().getLong(name);
		}
	}

	@Override
	public long getProjectLong(String name, IProject project) {
		return getProjectPreferences(project).getLong(name);
	}

	@Override
	public long getDefaultLong(final String name, final IProject project) {
		if (projectSpecificPreferencesEnabled(project)) {
			return getProjectPreferences(project).getDefaultLong(name);
		} else {
			return getWorkspacePreferences().getDefaultLong(name);
		}
	}

	@Override
	public long getProjectDefaultLong(String name, IProject project) {
		return getProjectPreferences(project).getDefaultLong(name);
	}

	@Override
	public String getString(final String name, final IProject project) {
		if (projectSpecificPreferencesEnabled(project)) {
			return getProjectPreferences(project).getString(name);
		} else {
			return getWorkspacePreferences().getString(name);
		}
	}

	@Override
	public String getProjectString(final String name, final IProject project) {
		return getProjectPreferences(project).getString(name);
	}

	@Override
	public String getDefaultString(final String name, final IProject project) {
		if (projectSpecificPreferencesEnabled(project)) {
			return getProjectPreferences(project).getDefaultString(name);
		} else {
			return getWorkspacePreferences().getDefaultString(name);
		}
	}

	@Override
	public String getProjectDefaultString(String name, IProject project) {
		return getProjectPreferences(project).getDefaultString(name);
	}

	/* Setters */

	@Override
	public void setValue(String name, boolean value, IProject project) {
		if (projectSpecificPreferencesEnabled(project)) {
			getProjectPreferences(project).setValue(name, value);
		} else {
			getWorkspacePreferences().setValue(name, value);
		}
	}

	@Override
	public void setProjectValue(String name, boolean value, IProject project) {
		getProjectPreferences(project).setValue(name, value);
	}

	@Override
	public void setDefaultValue(String name, boolean value, IProject project) {
		if (projectSpecificPreferencesEnabled(project)) {
			getProjectPreferences(project).setDefault(name, value);
		} else {
			getWorkspacePreferences().setDefault(name, value);
		}
	}

	@Override
	public void setProjectDefaultValue(String name, boolean value, IProject project) {
		getProjectPreferences(project).setDefault(name, value);
	}

	@Override
	public void setValue(String name, double value, IProject project) {
		if (projectSpecificPreferencesEnabled(project)) {
			getProjectPreferences(project).setValue(name, value);
		} else {
			getWorkspacePreferences().setValue(name, value);
		}
	}

	@Override
	public void setProjectValue(String name, double value, IProject project) {
		getProjectPreferences(project).setValue(name, value);
	}

	@Override
	public void setDefaultValue(String name, double value, IProject project) {
		if (projectSpecificPreferencesEnabled(project)) {
			getProjectPreferences(project).setDefault(name, value);
		} else {
			getWorkspacePreferences().setDefault(name, value);
		}
	}

	@Override
	public void setProjectDefaultValue(String name, double value, IProject project) {
		getProjectPreferences(project).setDefault(name, value);
	}

	@Override
	public void setValue(String name, float value, IProject project) {
		if (projectSpecificPreferencesEnabled(project)) {
			getProjectPreferences(project).setValue(name, value);
		} else {
			getWorkspacePreferences().setValue(name, value);
		}
	}

	@Override
	public void setProjectValue(String name, float value, IProject project) {
		getProjectPreferences(project).setValue(name, value);
	}

	@Override
	public void setDefaultValue(String name, float value, IProject project) {
		if (projectSpecificPreferencesEnabled(project)) {
			getProjectPreferences(project).setDefault(name, value);
		} else {
			getWorkspacePreferences().setDefault(name, value);
		}
	}

	@Override
	public void setProjectDefaultValue(String name, float value, IProject project) {
		getProjectPreferences(project).setDefault(name, value);
	}

	@Override
	public void setValue(String name, int value, IProject project) {
		if (projectSpecificPreferencesEnabled(project)) {
			getProjectPreferences(project).setValue(name, value);
		} else {
			getWorkspacePreferences().setValue(name, value);
		}
	}

	@Override
	public void setProjectValue(String name, int value, IProject project) {
		getProjectPreferences(project).setValue(name, value);
	}

	@Override
	public void setDefaultValue(String name, int value, IProject project) {
		if (projectSpecificPreferencesEnabled(project)) {
			getProjectPreferences(project).setDefault(name, value);
		} else {
			getWorkspacePreferences().setDefault(name, value);
		}
	}

	@Override
	public void setProjectDefaultValue(String name, int value, IProject project) {
		getProjectPreferences(project).setDefault(name, value);
	}

	@Override
	public void setValue(String name, long value, IProject project) {
		if (projectSpecificPreferencesEnabled(project)) {
			getProjectPreferences(project).setValue(name, value);
		} else {
			getWorkspacePreferences().setValue(name, value);
		}
	}

	@Override
	public void setProjectValue(String name, long value, IProject project) {
		getProjectPreferences(project).setValue(name, value);
	}

	@Override
	public void setDefaultValue(String name, long value, IProject project) {
		if (projectSpecificPreferencesEnabled(project)) {
			getProjectPreferences(project).setDefault(name, value);
		} else {
			getWorkspacePreferences().setDefault(name, value);
		}
	}

	@Override
	public void setProjectDefaultValue(String name, long value, IProject project) {
		getProjectPreferences(project).setDefault(name, value);
	}

	@Override
	public void setValue(String name, String value, IProject project) {
		if (projectSpecificPreferencesEnabled(project)) {
			getProjectPreferences(project).setValue(name, value);
		} else {
			getWorkspacePreferences().setValue(name, value);
		}
	}

	@Override
	public void setProjectValue(String name, String value, IProject project) {
		getProjectPreferences(project).setValue(name, value);
	}

	@Override
	public void setDefaultValue(String name, String value, IProject project) {
		if (projectSpecificPreferencesEnabled(project)) {
			getProjectPreferences(project).setDefault(name, value);
		} else {
			getWorkspacePreferences().setDefault(name, value);
		}
	}

	@Override
	public void setProjectDefaultValue(String name, String value, IProject project) {
		getProjectPreferences(project).setDefault(name, value);
	}

	/* Others */

	@Override
	public void setProjectSpecificPreferences(final IProject project, final boolean enabled) {
		getProjectPreferences(project).setValue(P_USE_PROJECT_PREFERENCES, enabled);
	}

	@Override
	public boolean projectSpecificPreferencesEnabled(final IProject project) {
		if (project == null) {
			return false;
		}
		final IPreferenceStore projectPreferences = getProjectPreferences(project);
		return projectPreferences.contains(P_USE_PROJECT_PREFERENCES) && projectPreferences.getBoolean(P_USE_PROJECT_PREFERENCES);
	}

	@Override
	public boolean contains(final String name, final IProject project) {
		return projectSpecificPreferencesEnabled(project) ? getProjectPreferences(project).contains(name) : getWorkspacePreferences().contains(name);
	}

}
