package com.linticator.view.preferences.addnewwizard;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import com.linticator.base.FileUtil;
import com.linticator.config.ConfigurationEntry;

public class InstallationDiscovery {
	public static boolean isValidLocation(final File parent) {
		if (parent.isDirectory()) {
			return hasFileWithName(parent, "msg.txt") || hasFileWithName(parent, "msg.xml");
		}
		return false;
	}

	private static boolean hasFileWithName(final File parent, final String fileName) {

		if (parent == null || !parent.isDirectory() || parent.list() == null) {
			return false;
		}

		for (final String file : parent.list()) {
			if (file.equals(fileName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @formatter:off
	 */
	public static ConfigurationEntry createConfigurationFromLocation(final File file, final String id) {
		final Collection<File> all = FileUtil.allFilesRecursively(file);

		return new ConfigurationEntry(
				id,
				"Flexe/PC-lint",
				getPathToOrEmptyString("co-gcc.lnt", all),
				getPathToOrEmptyString("co-gcc.h", all),
				getPathToOrEmptyString("co-gcc.mak", all),
				getPathToOrEmptyString("msg.txt", all),
				getPathToLintExecutableOrEmptyString(all),
				"c cc cpp cxx"
		);
	}

	private static String getPathToLintExecutableOrEmptyString(final Collection<File> files) {
		final ArrayList<File> executables = new ArrayList<File>();

		for (final File candidate : files) {
			if(isExecutable(candidate)) {
				executables.add(candidate);
			}
		}

		if(executables.size() == 1) {
			return executables.get(0).getAbsolutePath();
		} else if(executables.size() > 1) {
			for (final File file : executables) {
				if(file.getName().contains("LINT-NT")) {
					return file.getAbsolutePath();
				}
				if(file.getName().contains("lint")) {
					return file.getAbsolutePath();
				}
				if(file.getName().contains("pclp32")) {
					return file.getAbsolutePath();
				}
				if(file.getName().contains("pclp64")) {
					return file.getAbsolutePath();
				}
			}
			return "";
		} else{
			return "";
		}
	}

	private static boolean isExecutable(final File file) {
		/*
		 * file.canExecute doesn't work as expected in Windows
		 * */
		if(System.getProperty("os.name").toLowerCase().contains("win")) {
			return file.getName().endsWith("exe");
		} else {
			return file.canExecute();
		}
	}

	public static String getPathToOrEmptyString(final String file, final Collection<File> files) {
		for (final File candidate : files) {
			if (candidate.getName().equals(file)) {
				return candidate.getAbsolutePath();
			}
		}
		return "";
	}
}
