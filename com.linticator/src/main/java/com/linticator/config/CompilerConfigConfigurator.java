package com.linticator.config;

import java.io.File;
import java.io.IOException;

import org.eclipse.cdt.core.cdtvariables.CdtVariableException;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;

import com.linticator.base.Constants;
import com.linticator.base.FileUtil;

class CompilerConfigConfigurator extends ConfigFileConfigurator {
	public static void updateCompilerConfigFile(final File file, final ProjectConfig projectConfig, final IPath generatedCompilerConfigHeaderFile) throws IOException, CdtVariableException {
		
		final StringBuilder generatedContent = new StringBuilder();
		
		// Add the directory where the .lnt files are located to the include path, so that existing relative includes continue to work:
		addIncludeStatement(generatedContent, projectConfig.getLintCompilerConfig().toFile().getParent());
		
		// Add our configuration directory to the include paths.
		final IFolder configurationDirectory = projectConfig.getConfigurationDirectory(projectConfig.getProject());
		addIncludeStatement(generatedContent, configurationDirectory.getLocation().toOSString());
		
		if (GenericPluginConfig.isConfiguredForGcc(projectConfig.getPreferenceStore())) {
			final String lintCompilerConfigContent = getLintCompilerConfigContent(projectConfig, generatedCompilerConfigHeaderFile);
			generatedContent.append(lintCompilerConfigContent);
			
		} else {
			
			String fileContent = FileUtil.readFile(projectConfig.getLintCompilerConfig().toFile());
			
			if(projectConfig.getLintCompilerConfigHeader().isEmpty()) {
				// don't replace anything
			} else {
				String configuredCorrespondingHeader = projectConfig.getLintCompilerConfigHeader().lastSegment();
				fileContent = fileContent.replace(configuredCorrespondingHeader, generatedCompilerConfigHeaderFile.lastSegment());
			}
			
			generatedContent.append(fileContent);
		}
		
		String fileContent = generatedContent.toString();

		// for backwards compatibility, replace this too:
		fileContent = fileContent.replace("co-gcc.h", generatedCompilerConfigHeaderFile.lastSegment());
		
		// we get these from CDT
		fileContent = fileContent.replace("gcc-include-path.lnt", "// gcc-include-path.lnt");
		
		final IPath sizeOptionsPath = projectConfig.getSizeOptionsFile();
		if(sizeOptionsPath.toFile().exists()) {
			fileContent = fileContent.replace("size-options.lnt", "\""+ sizeOptionsPath.toOSString() +"\"");
		}
		
		FileUtil.writeFile(file, fileContent);
	}

	@SuppressWarnings("nls")
	private static String getLintCompilerConfigContent(final ProjectConfig projectConfig, final IPath generatedCompilerConfigHeaderFile) throws IOException, CdtVariableException {
		final StringBuilder lintCompilerConfigContent = new StringBuilder();
		String fileContent = FileUtil.readFile(projectConfig.getLintCompilerConfig().toFile());
		fileContent = fileContent.replace("-rw_asgn(__alignof__,__alignof)","// -rw_asgn(__alignof__,__alignof)");

		lintCompilerConfigContent.append("-fff");
		lintCompilerConfigContent.append(System.getProperty(LINE_SEPARATOR));
		lintCompilerConfigContent.append(fileContent);

		return lintCompilerConfigContent.toString();
	}

	private static void addIncludeStatement(final StringBuilder lintCompilerConfigContent, final String includeLocation) {
		lintCompilerConfigContent.append(Constants.INCLUDE_INSTRUCTION);
		lintCompilerConfigContent.append("\"");
		lintCompilerConfigContent.append(includeLocation);
		lintCompilerConfigContent.append("\"");
		lintCompilerConfigContent.append(System.getProperty(LINE_SEPARATOR));
	}
}
