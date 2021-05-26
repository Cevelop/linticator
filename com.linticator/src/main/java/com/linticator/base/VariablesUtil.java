package com.linticator.base;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.cdtvariables.CdtVariableException;
import org.eclipse.cdt.core.cdtvariables.ICdtVariableManager;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.internal.core.cdtvariables.CoreVariableSubstitutor;
import org.eclipse.cdt.internal.core.cdtvariables.DefaultVariableContextInfo;
import org.eclipse.cdt.internal.core.cdtvariables.ICoreVariableContextInfo;
import org.eclipse.cdt.utils.cdtvariables.CdtVariableResolver;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;

import com.linticator.config.ProjectConfig;

@SuppressWarnings("restriction")
public class VariablesUtil {

	public static String resolveWorkspaceVariables(final String value) throws CdtVariableException {
		final IWorkspace ws = ResourcesPlugin.getWorkspace();
		final DefaultVariableContextInfo i = new DefaultVariableContextInfo(ICoreVariableContextInfo.CONTEXT_WORKSPACE, ws);
		return CdtVariableResolver.resolveToString(value, new CoreVariableSubstitutor(i, null, null));
	}

	public static String resolveProjectVariables(final ProjectConfig projectConfig, final String value)
			throws CdtVariableException {
		final ICProjectDescription projectDescription = CoreModel.getDefault().getProjectDescription(projectConfig.getProject());
		final ICConfigurationDescription cfgd = projectDescription.getActiveConfiguration();
		final ICdtVariableManager vm = CCorePlugin.getDefault().getCdtVariableManager();
		
		final ArrayList<String> entries = new ArrayList<String>();
		

		try {
			final BufferedReader reader = new BufferedReader(new StringReader(value));
			String line;
			
			while((line = reader.readLine()) != null) {
				entries.add(vm.resolveValue(line, null, null, cfgd));
			}
			
		} catch (final IOException e) {
			return value;
		}
		
		return StringUtil.join("\n", entries);
	}
}
