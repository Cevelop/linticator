package com.linticator.config;

import java.io.File;
import java.util.Arrays;

import org.eclipse.cdt.core.cdtvariables.CdtVariableException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.preference.IPreferenceStore;

import com.linticator.base.VariablesUtil;

public class ConfigurationEntry {

	/**
	 * @formatter:off
	 */
	public static ConfigurationEntry fromId(final String id, final IPreferenceStore prefs) {
		return new ConfigurationEntry(id,
				prefs.getString(id + PreferenceConstants.LINT_CONFIG_NAME),
				prefs.getString(id + PreferenceConstants.LINT_COMPILER_CONFIG),
				prefs.getString(id + PreferenceConstants.LINT_COMPILER_CONFIG_HEADER),
				prefs.getString(id + PreferenceConstants.LINT_MAKE_FILE),
				prefs.getString(id + PreferenceConstants.LINT_DOCUMENTATION),
				prefs.getString(id + PreferenceConstants.LINT_EXECUTABLE),
				prefs.getString(id + PreferenceConstants.FILE_EXTENSIONS));
	}

	private final String uniqueId;
	private final String name;
	private final String compilerConfig;
	private final String compilerConfigHeader;
	private final String makeFile;
	private final String documentationFile;
	private final String executable;
	private final String fileExtensions;

	public ConfigurationEntry(final String uniqueId, final String name, final String compilerConfig,
			final String compilerConfigHeader, final String makeFile, final String documentationFile,
			final String executable,
			final String fileExtensions) {
		this.uniqueId = uniqueId;
		this.name = name;
		this.compilerConfig = compilerConfig;
		this.compilerConfigHeader = compilerConfigHeader;
		this.makeFile = makeFile;
		this.documentationFile = documentationFile;
		this.executable = executable;
		this.fileExtensions = fileExtensions;
	}

	public File getInstallLocation() {

//		final String[] s1 = new Path(compilerConfig).segments();
//		final String[] s2 = new Path(compilerConfigHeader).segments();
		final String[] s3 = new Path(documentationFile).segments();
		final String[] s4 = new Path(executable).segments();

		final int minLength = getMinimalLength(new int[] { s3.length, s4.length });

		String longestCommonPrefix = "/";

		for (int i = 0; i < minLength; i++) {
			if (s3[i].equals(s4[i]))
				longestCommonPrefix += s3[i] + "/";
			else
				break;
		}

		try {
			return new File(VariablesUtil.resolveWorkspaceVariables(longestCommonPrefix));
		} catch (final CdtVariableException e) {
			return new File(longestCommonPrefix);
		}
	}

	private int getMinimalLength(final int[] lengths) {
		Arrays.sort(lengths);
		return lengths[0];
	}

	public String getCompilerConfig() {
		return compilerConfig;
	}

	public String getCompilerConfigHeader() {
		return compilerConfigHeader;
	}

	public String getMakeFile() {
		return makeFile;
	}

	public String getDocumentationFile() {
		return documentationFile;
	}

	public String getExecutable() {
		return executable;
	}

	public String getFileExtensions() {
		return fileExtensions;
	}

	public String getName() {
		return name;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	@Override
	public int hashCode() {
		return uniqueId.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ConfigurationEntry other = (ConfigurationEntry) obj;
		return uniqueId.equals(other.uniqueId);
	}
}
