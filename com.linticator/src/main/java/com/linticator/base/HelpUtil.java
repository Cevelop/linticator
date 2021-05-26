package com.linticator.base;

import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;

public class HelpUtil {
	public static void setFlexeLintConfigurationHelp(final Control control) {
		PlatformUI.getWorkbench().getHelpSystem().setHelp(control, "com.linticator.linticator_configuration");
	}

	public static void setFlexeLintConfigurationEntryDetailsHelp(final Control control) {
		PlatformUI.getWorkbench().getHelpSystem()
				.setHelp(control, "com.linticator.linticator_configuration_entry_details");
	}

	public static void setInhibitionOptionsHelp(final Control control) {
		PlatformUI.getWorkbench().getHelpSystem().setHelp(control, "com.linticator.linticator_inhibition_options");
	}

	public static void setLicenseWizardHelp(final Control control) {
		PlatformUI.getWorkbench().getHelpSystem().setHelp(control, "com.linticator.license_wizard");
	}
}
