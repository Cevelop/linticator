package com.linticator.config;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashSet;

import org.eclipse.core.resources.IResource;

import com.linticator.base.FileUtil;

class FileListConfigurator extends ConfigFileConfigurator {
	public static void updateFileListConfigFile(final ProjectConfig config, final File configFile, final IResource[] startingPoints) throws IOException {

		final LinkedHashSet<IResource> allFiles = new LinkedHashSet<IResource>();
		for (final IResource r : startingPoints) {
			allFiles.addAll(config.getFiles(r));
		}

		FileUtil.writeFile(configFile, getFileListContent(allFiles));
	}

	private static String getFileListContent(final Collection<IResource> sourceFiles) {
		final StringBuilder fileList = new StringBuilder();
		for (final IResource file : sourceFiles) {
			fileList.append(String.format("\"%s\"%s", file.getLocation(), System.getProperty(LINE_SEPARATOR)));
		}
		return fileList.toString();
	}

}
