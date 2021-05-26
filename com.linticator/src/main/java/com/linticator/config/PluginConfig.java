package com.linticator.config;

import java.util.Set;

import org.eclipse.cdt.core.cdtvariables.CdtVariableException;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;

public interface PluginConfig {
	Set<String> getFileExtensions();

	IPath getLintCompilerConfig() throws CdtVariableException;
	
	IPath getLintCompilerConfigHeader() throws CdtVariableException;

	IPath getLintDocumentationFile() throws CdtVariableException;

	IPath getLintExecutable() throws CdtVariableException;
	
	IFolder getConfigurationDirectory(IProject project);
}
