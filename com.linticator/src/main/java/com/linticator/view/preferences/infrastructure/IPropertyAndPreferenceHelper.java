package com.linticator.view.preferences.infrastructure;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;


//TODO move to Infrastructure-layer once created
/**
 * @author tstauber
 */
public interface IPropertyAndPreferenceHelper {

	/**
	 * Preference that will be set if the selected project should use project specific preferences
	 */
	public static final String P_USE_PROJECT_PREFERENCES = "USE_PROJECT_PREFERENCES";

	/**
	 * Returns the workspacePreferences for this plugin
	 */
	public IPreferenceStore getWorkspacePreferences();

	/**
	 * Returns the qualifier (prefix) to all the preferences specified in this page. If this identifier is the same for
	 * two unrelated {@link IPropertyAndPreferenceHelper}, the {@link PreferencePage}s will suffer from collisions.
	 * 
	 * I.e. If there are three individual {@link FieldEditorPropertyAndPreferencePage}s and either none or all of them
	 * should have project specific preferences enabled, these three pages should share the same preferenceIdQualifier
	 * 
	 * Subclasses must implement.
	 */
	public String getPreferenceIdQualifier();

	/**
	 * If overridden this should return the default preference id qualifier. This qualifier is used to search for
	 * default values. If this method is not overridden then the default preference id qualifier is assumed to be the
	 * same as the preference id qualifier.
	 */
	public String getDefaultPreferenceIdQualifier();

	/**
	 * Gets the {@code IPreferenceStore} for the passed {@code IProject}
	 * 
	 * @param project
	 *            The project for which to get the preference store, not null
	 * @param preferenceIdQualifier
	 *            The qualifier to the preference node, not null
	 * @return The {@code IPreferenceStore} for this project and this qualifier.
	 */
	public IPreferenceStore getProjectPreferences(final IProject project);

	/**
	 * Returns the value stored for this name in this project if project is not null and project specific preferences
	 * are enabled for this project, else the value stored for this name in the workspace preferences
	 * 
	 * @see IPreferenceStore#getBoolean(String)
	 * 
	 * @param name
	 *            The preference name
	 * @param project
	 *            The project, null returns workspace preference
	 */
	public boolean getBoolean(final String name, final IProject project);

	/**
	 * Returns the value stored for this name in this project
	 * 
	 * @see IPreferenceStore#getBoolean(String)
	 * 
	 * @param name
	 *            The preference name
	 * @param project
	 *            The project, null returns workspace preference
	 */
	public boolean getProjectBoolean(final String name, final IProject project);

	/**
	 * Returns the default value for this name in this project if project is not null and project specific preferences
	 * are enabled for this project, else the default value for this name in the workspace preferences
	 * 
	 * @see IPreferenceStore#getDefaultBoolean(String)
	 * 
	 * @param name
	 *            The preference name
	 * @param project
	 *            The project, null returns workspace preference
	 */
	public boolean getDefaultBoolean(final String name, final IProject project);

	/**
	 * Returns the default value stored for this name in this project's preferences
	 * 
	 * @see IPreferenceStore#getDefaultBoolean(String)
	 * 
	 * @param name
	 *            The preference name
	 * @param project
	 *            The project, null returns workspace preference
	 */
	public boolean getProjectDefaultBoolean(String name, IProject project);

	/**
	 * Returns the value stored for this name in this project if project is not null and project specific preferences
	 * are enabled for this project, else the value stored for this name in the workspace preferences
	 * 
	 * @see IPreferenceStore#getDouble(String)
	 * 
	 * @param name
	 *            The preference name
	 * @param project
	 *            The project, null returns workspace preference
	 */
	public double getDouble(final String name, final IProject project);

	/**
	 * Returns the value stored for this name in this project's preferences
	 * 
	 * @see IPreferenceStore#getDouble(String)
	 * 
	 * @param name
	 *            The preference name
	 * @param project
	 *            The project, null returns workspace preference
	 */
	public double getProjectDouble(final String name, final IProject project);

	/**
	 * Returns the default value for this name in this project if project is not null and project specific preferences
	 * are enabled for this project, else the default value for this name in the workspace preferences
	 * 
	 * @see IPreferenceStore#getDefaultDouble(String)
	 * 
	 * @param name
	 *            The preference name
	 * @param project
	 *            The project, null returns workspace preference
	 */
	public double getDefaultDouble(final String name, final IProject project);

	/**
	 * Returns the default value stored for this name in this project's preferences
	 * 
	 * @see IPreferenceStore#getDefaultDouble(String)
	 * 
	 * @param name
	 *            The preference name
	 * @param project
	 *            The project, null returns workspace preference
	 */
	public double getProjectDefaultDouble(final String name, final IProject project);

	/**
	 * Returns the value stored for this name in this project if project is not null and project specific preferences
	 * are enabled for this project, else the value stored for this name in the workspace preferences
	 * 
	 * @see IPreferenceStore#getFloat(String)
	 * 
	 * @param name
	 *            The preference name
	 * @param project
	 *            The project, null returns workspace preference
	 */
	public float getFloat(final String name, final IProject project);

	/**
	 * Returns the value stored for this name in this project's preferences
	 * 
	 * @see IPreferenceStore#getFloat(String)
	 * 
	 * @param name
	 *            The preference name
	 * @param project
	 *            The project, null returns workspace preference
	 */
	public float getProjectFloat(final String name, final IProject project);

	/**
	 * Returns the default value for this name in this project if project is not null and project specific preferences
	 * are enabled for this project, else the default value for this name in the workspace preferences
	 * 
	 * @see IPreferenceStore#getDefaultFloat(String)
	 * 
	 * @param name
	 *            The preference name
	 * @param project
	 *            The project, null returns workspace preference
	 */
	public float getDefaultFloat(final String name, final IProject project);

	/**
	 * Returns the default value stored for this name in this project's preferences
	 * 
	 * @see IPreferenceStore#getDefaultFloat(String)
	 * 
	 * @param name
	 *            The preference name
	 * @param project
	 *            The project, null returns workspace preference
	 */
	public float getProjectDefaultFloat(final String name, final IProject project);

	/**
	 * Returns the value stored for this name in this project if project is not null and project specific preferences
	 * are enabled for this project, else the value stored for this name in the workspace preferences
	 * 
	 * @see IPreferenceStore#getInt(String)
	 * 
	 * @param name
	 *            The preference name
	 * @param project
	 *            The project, null returns workspace preference
	 */
	public int getInt(final String name, final IProject project);

	/**
	 * Returns the value stored for this name in this project's preferences
	 * 
	 * @see IPreferenceStore#getInt(String)
	 * 
	 * @param name
	 *            The preference name
	 * @param project
	 *            The project, null returns workspace preference
	 */
	public int getProjectInt(final String name, final IProject project);

	/**
	 * Returns the default value for this name in this project if project is not null and project specific preferences
	 * are enabled for this project, else the default value for this name in the workspace preferences
	 * 
	 * @see IPreferenceStore#getDefaultInt(String)
	 * 
	 * @param name
	 *            The preference name
	 * @param project
	 *            The project, null returns workspace preference
	 */
	public int getDefaultInt(final String name, final IProject project);

	/**
	 * Returns the default value stored for this name in this project's preferences
	 * 
	 * @see IPreferenceStore#getDefaultInt(String)
	 * 
	 * @param name
	 *            The preference name
	 * @param project
	 *            The project, null returns workspace preference
	 */
	public int getProjectDefaultInt(final String name, final IProject project);

	/**
	 * Returns the value stored for this name in this project if project is not null and project specific preferences
	 * are enabled for this project, else the value stored for this name in the workspace preferences
	 * 
	 * @see IPreferenceStore#getLong(String)
	 * 
	 * @param name
	 *            The preference name
	 * @param project
	 *            The project, null returns workspace preference
	 */
	public long getLong(final String name, final IProject project);

	/**
	 * Returns the value stored for this name in this project's preferences
	 * 
	 * @see IPreferenceStore#getLong(String)
	 * 
	 * @param name
	 *            The preference name
	 * @param project
	 *            The project, null returns workspace preference
	 */
	public long getProjectLong(final String name, final IProject project);

	/**
	 * Returns the default value for this name in this project if project is not null and project specific preferences
	 * are enabled for this project, else the default value for this name in the workspace preferences
	 * 
	 * @see IPreferenceStore#getDefaultLong(String)
	 * 
	 * @param name
	 *            The preference name
	 * @param project
	 *            The project, null returns workspace preference
	 */
	public long getDefaultLong(final String name, final IProject project);

	/**
	 * Returns the default value stored for this name in this project's preferences
	 * 
	 * @see IPreferenceStore#getDefaultLong(String)
	 * 
	 * @param name
	 *            The preference name
	 * @param project
	 *            The project, null returns workspace preference
	 */
	public long getProjectDefaultLong(final String name, final IProject project);

	/**
	 * Returns the value stored for this name in this project if project is not null and project specific preferences
	 * are enabled for this project, else the value stored for this name in the workspace preferences
	 * 
	 * @see IPreferenceStore#getString(String)
	 * 
	 * @param name
	 *            The preference name
	 * @param project
	 *            The project, null returns workspace preference
	 */
	public String getString(final String name, final IProject project);

	/**
	 * Returns the value stored for this name in this project's preferences
	 * 
	 * @see IPreferenceStore#getString(String)
	 * 
	 * @param name
	 *            The preference name
	 * @param project
	 *            The project, null returns workspace preference
	 */
	public String getProjectString(final String name, final IProject project);

	/**
	 * Returns the default value for this name in this project if project is not null and project specific preferences
	 * are enabled for this project, else the default value for this name in the workspace preferences
	 * 
	 * @see IPreferenceStore#getDefaultString(String)
	 * 
	 * @param name
	 *            The preference name
	 * @param project
	 *            The project, null returns workspace preference
	 */
	public String getDefaultString(final String name, final IProject project);

	/**
	 * Returns the default value stored for this name in this project's preferences
	 * 
	 * @see IPreferenceStore#getDefaultString(String)
	 * 
	 * @param name
	 *            The preference name
	 * @param project
	 *            The project, null returns workspace preference
	 */
	public String getProjectDefaultString(final String name, final IProject project);

	/* Setter */

	/**
	 * Sets the value for this name in this project if project is not null and project specific preferences are enabled
	 * for this project, else the value will be stored in the workspace preferences
	 * 
	 * @see IPreferenceStore#setValue(String, boolean)
	 * 
	 * @param name
	 *            The preference name
	 * @param value
	 *            The value to be stored
	 * @param project
	 *            The project, null sets workspace preference
	 */
	public void setValue(final String name, final boolean value, final IProject project);

	/**
	 * Sets the value for this name in this project's preferences
	 * 
	 * @see IPreferenceStore#setValue(String, boolean)
	 * 
	 * @param name
	 *            The preference name
	 * @param value
	 *            The value to be stored
	 * @param project
	 *            The project
	 */
	public void setProjectValue(final String name, final boolean value, final IProject project);

	/**
	 * Sets the default value for this name in this project if project is not null and project specific preferences are
	 * enabled for this project, else the default value will be stored in the workspace preferences
	 * 
	 * @see IPreferenceStore#setDefault(String, boolean)
	 * 
	 * @param name
	 *            The preference name
	 * @param value
	 *            The default value to be stored
	 * @param project
	 *            The project, null sets workspace preference
	 */
	public void setDefaultValue(final String name, final boolean value, final IProject project);

	/**
	 * Sets the default value for this name in this project's preferences
	 * 
	 * @see IPreferenceStore#setDefault(String, boolean)
	 * 
	 * @param name
	 *            The preference name
	 * @param value
	 *            The default value to be stored
	 * @param project
	 *            The project
	 */
	public void setProjectDefaultValue(final String name, final boolean value, final IProject project);

	/**
	 * Sets the value for this name in this project if project is not null and project specific preferences are enabled
	 * for this project, else the value will be stored in the workspace preferences
	 * 
	 * @see IPreferenceStore#setValue(String, double)
	 * 
	 * @param name
	 *            The preference name
	 * @param value
	 *            The value to be stored
	 * @param project
	 *            The project, null sets workspace preference
	 */
	public void setValue(final String name, final double value, final IProject project);

	/**
	 * Sets the value for this name in this project's preferences
	 * 
	 * @see IPreferenceStore#setValue(String, double)
	 * 
	 * @param name
	 *            The preference name
	 * @param value
	 *            The value to be stored
	 * @param project
	 *            The project
	 */
	public void setProjectValue(final String name, final double value, final IProject project);

	/**
	 * Sets the default value for this name in this project if project is not null and project specific preferences are
	 * enabled for this project, else the default value will be stored in the workspace preferences
	 * 
	 * @see IPreferenceStore#setDefault(String, double)
	 * 
	 * @param name
	 *            The preference name
	 * @param value
	 *            The default value to be stored
	 * @param project
	 *            The project, null sets workspace preference
	 */
	public void setDefaultValue(final String name, final double value, final IProject project);

	/**
	 * Sets the default value for this name in this project's preferences
	 * 
	 * @see IPreferenceStore#setDefault(String, double)
	 * 
	 * @param name
	 *            The preference name
	 * @param value
	 *            The default value to be stored
	 * @param project
	 *            The project
	 */
	public void setProjectDefaultValue(final String name, final double value, final IProject project);

	/**
	 * Sets the value for this name in this project if project is not null and project specific preferences are enabled
	 * for this project, else the value will be stored in the workspace preferences
	 * 
	 * @see IPreferenceStore#setValue(String, float)
	 * 
	 * @param name
	 *            The preference name
	 * @param value
	 *            The value to be stored
	 * @param project
	 *            The project, null sets workspace preference
	 */
	public void setValue(final String name, final float value, final IProject project);

	/**
	 * Sets the value for this name in this project's preferences
	 * 
	 * @see IPreferenceStore#setValue(String, float)
	 * 
	 * @param name
	 *            The preference name
	 * @param value
	 *            The value to be stored
	 * @param project
	 *            The project
	 */
	public void setProjectValue(final String name, final float value, final IProject project);

	/**
	 * Sets the default value for this name in this project if project is not null and project specific preferences are
	 * enabled for this project, else the default value will be stored in the workspace preferences
	 * 
	 * @see IPreferenceStore#setDefault(String, float)
	 * 
	 * @param name
	 *            The preference name
	 * @param value
	 *            The default value to be stored
	 * @param project
	 *            The project, null sets workspace preference
	 */
	public void setDefaultValue(final String name, final float value, final IProject project);

	/**
	 * Sets the default value for this name in this project's preferences
	 * 
	 * @see IPreferenceStore#setDefault(String, float)
	 * 
	 * @param name
	 *            The preference name
	 * @param value
	 *            The default value to be stored
	 * @param project
	 *            The project
	 */
	public void setProjectDefaultValue(final String name, final float value, final IProject project);

	/**
	 * Sets the value for this name in this project if project is not null and project specific preferences are enabled
	 * for this project, else the value will be stored in the workspace preferences
	 * 
	 * @see IPreferenceStore#setValue(String, int)
	 * 
	 * @param name
	 *            The preference name
	 * @param value
	 *            The value to be stored
	 * @param project
	 *            The project, null sets workspace preference
	 */
	public void setValue(final String name, final int value, final IProject project);

	/**
	 * Sets the value for this name in this project's preferences
	 * 
	 * @see IPreferenceStore#setValue(String, int)
	 * 
	 * @param name
	 *            The preference name
	 * @param value
	 *            The value to be stored
	 * @param project
	 *            The project
	 */
	public void setProjectValue(final String name, final int value, final IProject project);

	/**
	 * Sets the default value for this name in this project if project is not null and project specific preferences are
	 * enabled for this project, else the default value will be stored in the workspace preferences
	 * 
	 * @see IPreferenceStore#setDefault(String, int)
	 * 
	 * @param name
	 *            The preference name
	 * @param value
	 *            The default value to be stored
	 * @param project
	 *            The project, null sets workspace preference
	 */
	public void setDefaultValue(final String name, final int value, final IProject project);

	/**
	 * Sets the default value for this name in this project's preferences
	 * 
	 * @see IPreferenceStore#setDefault(String, int)
	 * 
	 * @param name
	 *            The preference name
	 * @param value
	 *            The default value to be stored
	 * @param project
	 *            The project
	 */
	public void setProjectDefaultValue(final String name, final int value, final IProject project);

	/**
	 * Sets the value for this name in this project if project is not null and project specific preferences are enabled
	 * for this project, else the value will be stored in the workspace preferences
	 * 
	 * @see IPreferenceStore#setValue(String, long)
	 * 
	 * @param name
	 *            The preference name
	 * @param value
	 *            The value to be stored
	 * @param project
	 *            The project, null sets workspace preference
	 */
	public void setValue(final String name, final long value, final IProject project);

	/**
	 * Sets the value for this name in this project's preferences
	 * 
	 * @see IPreferenceStore#setValue(String, long)
	 * 
	 * @param name
	 *            The preference name
	 * @param value
	 *            The value to be stored
	 * @param project
	 *            The project
	 */
	public void setProjectValue(final String name, final long value, final IProject project);

	/**
	 * Sets the default value for this name in this project if project is not null and project specific preferences are
	 * enabled for this project, else the default value will be stored in the workspace preferences
	 * 
	 * @see IPreferenceStore#setDefault(String, long)
	 * 
	 * @param name
	 *            The preference name
	 * @param value
	 *            The default value to be stored
	 * @param project
	 *            The project, null sets workspace preference
	 */
	public void setDefaultValue(final String name, final long value, final IProject project);

	/**
	 * Sets the default value for this name in this project's preferences
	 * 
	 * @see IPreferenceStore#setDefault(String, long)
	 * 
	 * @param name
	 *            The preference name
	 * @param value
	 *            The default value to be stored
	 * @param project
	 *            The project
	 */
	public void setProjectDefaultValue(final String name, final long value, final IProject project);

	/**
	 * Sets the value for this name in this project if project is not null and project specific preferences are enabled
	 * for this project, else the value will be stored in the workspace preferences
	 * 
	 * @see IPreferenceStore#setValue(String, String)
	 * 
	 * @param name
	 *            The preference name
	 * @param value
	 *            The value to be stored
	 * @param project
	 *            The project, null sets workspace preference
	 */
	public void setValue(final String name, final String value, final IProject project);

	/**
	 * Sets the value for this name in this project's preferences
	 * 
	 * @see IPreferenceStore#setValue(String, String)
	 * 
	 * @param name
	 *            The preference name
	 * @param value
	 *            The value to be stored
	 * @param project
	 *            The project
	 */
	public void setProjectValue(final String name, final String value, final IProject project);

	/**
	 * Sets the default value for this name in this project if project is not null and project specific preferences are
	 * enabled for this project, else the default value will be stored in the workspace preferences
	 * 
	 * @see IPreferenceStore#setDefault(String, String)
	 * 
	 * @param name
	 *            The preference name
	 * @param value
	 *            The default value to be stored
	 * @param project
	 *            The project, null sets workspace preference
	 */
	public void setDefaultValue(final String name, final String value, final IProject project);

	/**
	 * Sets the default value for this name in this project's preferences
	 * 
	 * @see IPreferenceStore#setDefault(String, String)
	 * 
	 * @param name
	 *            The preference name
	 * @param value
	 *            The default value to be stored
	 * @param project
	 *            The project
	 */
	public void setProjectDefaultValue(final String name, final String value, final IProject project);

	/* Others */

	/**
	 * Enables / disables project specific preferences for the passed {@code IProject}
	 * 
	 * @param project
	 *            The project for which to enable / disable specific preferences, not null
	 * @param enabled
	 *            If project specific settings shall be enabled
	 */
	public void setProjectSpecificPreferences(final IProject project, final boolean enabled);

	/**
	 * Tests if project specific preferences are enabled for the passed {@code IProject}
	 * 
	 * @param project
	 *            The project to test, null returns false
	 * @return If project specific preferences are enabled
	 */
	public boolean projectSpecificPreferencesEnabled(final IProject project);

	/**
	 * Returns if the PreferenceStore contains a preference with this name. Uses the project specific preferences, if
	 * enabled. Else it uses the workspace preferences
	 * 
	 * @param name
	 *            The preference name
	 * @param project
	 *            The project whose properties shall be tested
	 * @return If the preferences contain an entry with given name
	 */
	public boolean contains(final String name, final IProject project);
}
