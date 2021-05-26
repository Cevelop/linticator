package com.linticator.config;

import java.io.File;
import java.io.IOException;

import org.eclipse.cdt.core.cdtvariables.CdtVariableException;
import org.eclipse.core.runtime.IPath;

import com.linticator.base.FileUtil;

class CompilerConfigHeaderConfigurator extends ConfigFileConfigurator {

	public static void updateCompilerConfigHeader(final File out, final ProjectConfig projectConfig, final IPath macroFile, final IPath compilerConfigHeader) throws IOException, CdtVariableException {
		if (GenericPluginConfig.isConfiguredForGcc(projectConfig.getPreferenceStore())) {
			FileUtil.writeFile(out,getLintCompilerConfigHeaderContent(projectConfig, macroFile, compilerConfigHeader));
		} else if (isConfigured(compilerConfigHeader)) {
			// do it in any case to sanitize the default co-gcc.h file.
			FileUtil.writeFile(out,getLintCompilerConfigHeaderContent(projectConfig, macroFile, compilerConfigHeader));
		} else {
			FileUtil.writeFile(out, "");
		}
	}

	private static boolean isConfigured(final IPath compilerConfigHeader) {
		return !compilerConfigHeader.isEmpty();
	}

	private static String getLintCompilerConfigHeaderContent(final ProjectConfig projectConfig, final IPath macroFile, final IPath lintCompilerConfigHeader) throws IOException, CdtVariableException {
		String lintCompilerConfigHeaderContent = FileUtil.readFile(lintCompilerConfigHeader.toFile());
		final String newLine = System.getProperty(LINE_SEPARATOR);
		final StringBuilder toReplace = new StringBuilder();
		toReplace.append("#if defined(__cplusplus)");
		toReplace.append(newLine);
		toReplace.append("#       include \"lint_cppmac.h\"");
		toReplace.append(newLine);
		toReplace.append("#else");
		toReplace.append(newLine);
		toReplace.append("#       include \"lint_cmac.h\"");
		toReplace.append(newLine);
		toReplace.append("#endif");
		final StringBuilder newIncludeStatement = new StringBuilder();
		newIncludeStatement.append("#include ");
		newIncludeStatement.append("\"");
		newIncludeStatement.append(macroFile);
		newIncludeStatement.append("\"");
		lintCompilerConfigHeaderContent = lintCompilerConfigHeaderContent.replace(toReplace.toString(), newIncludeStatement.toString());

		return lintCompilerConfigHeaderContent + newLine /*to avoid a FlexeLint warning*/;
	}
}
