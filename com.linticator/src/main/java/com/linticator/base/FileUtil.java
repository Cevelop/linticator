package com.linticator.base;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.resources.IResource;

public abstract class FileUtil {

	public static String readFile(final File file) throws IOException {
		return read(new FileInputStream(file));
	}

	public static String readFile(final File file, final String sourceEncoding) throws IOException {
		return read(new FileInputStream(file), sourceEncoding);
	}

	public static String readFile(final URL url) throws IOException {
		return read(url.openStream());
	}

	public static String readFile(final URL url, final String sourceEncoding) throws IOException {
		return read(url.openStream(), sourceEncoding);
	}

	public static String read(final InputStream inputStream) throws IOException {
		return read(inputStream, "UTF-8");
	}

	public static String read(final InputStream inputStream, final String sourceEncoding) throws IOException {
		final BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, sourceEncoding));
		String tmp = in.readLine();
		final StringBuilder sb = new StringBuilder(tmp != null ? tmp : ""); //$NON-NLS-1$
		while ((tmp = in.readLine()) != null) {
			sb.append(System.getProperty("line.separator")); //$NON-NLS-1$
			sb.append(tmp);
		}
		return sb.toString();
	}

	public static void writeFile(final File file, final String content) throws IOException {
		final BufferedWriter out = new BufferedWriter(new FileWriter(file));
		out.write(content);
		out.close();
	}

	public static Set<IResource> filterExtensions(final Set<IResource> resources,
			final Set<String> fileExtensions) {
		final Set<IResource> filtered = new LinkedHashSet<IResource>();
		for (final IResource resource : resources) {
			if (fileExtensions.contains(resource.getFileExtension())) {
				filtered.add(resource);
			}
		}
		return filtered;
	}
	
	public static Collection<File> allFilesRecursively(final File file) {
		final ArrayList<File> result = new ArrayList<File>();
		if (file.isDirectory()) {
			for (final File child : file.listFiles()) {
				if(child != null) {
					result.addAll(allFilesRecursively(child));
				}
			}
		} else {
			result.add(file);
		}
		return result;
	}
}
