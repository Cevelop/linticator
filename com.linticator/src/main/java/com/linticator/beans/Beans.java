package com.linticator.beans;

import java.io.FileInputStream;

import org.eclipse.core.runtime.IPath;

import com.linticator.Linticator;
import com.linticator.config.GenericPluginConfig;
import com.linticator.documentation.DocumentationFromMsgTxt;
import com.linticator.documentation.DocumentationFromMsgXml;
import com.linticator.documentation.IDocumentation;
import com.linticator.view.preferences.helpers.PropAndPrefHelper;

public class Beans {
	private IDocumentation documentation;

	public IDocumentation getDocumentation() {
		if (documentation == null) {
			try {
				GenericPluginConfig genericPluginConfig = new GenericPluginConfig(new PropAndPrefHelper().getWorkspacePreferences());
				IPath lintDocumentationFile = genericPluginConfig.getLintDocumentationFile();
				FileInputStream source = new FileInputStream(lintDocumentationFile.toFile());
				if (lintDocumentationFile.getFileExtension().equals("txt")) {
					documentation = new DocumentationFromMsgTxt();
					documentation.parse(source);
				} else if (lintDocumentationFile.getFileExtension().equals("xml")) {
					documentation = new DocumentationFromMsgXml();
					documentation.parse(source);
				}
			} catch (Exception e) {
				Linticator.getDefault().handleError("Beans", e);
			}
		}
		return documentation;
	}

}
