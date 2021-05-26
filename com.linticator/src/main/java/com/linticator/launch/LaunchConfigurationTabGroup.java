package com.linticator.launch;

import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.debug.core.ICDTLaunchConfigurationConstants;
import org.eclipse.cdt.launch.ui.CAbstractMainTab;
import org.eclipse.core.resources.IResource;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class LaunchConfigurationTabGroup extends AbstractLaunchConfigurationTabGroup {

	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] { new CAbstractMainTab() {

			@Override
			public void createControl(Composite parent) {
				Composite comp = new Composite(parent, SWT.NONE);
				setControl(comp);

				GridLayout topLayout = new GridLayout();
				comp.setLayout(topLayout);

				createVerticalSpacer(comp, 1);
				createProjectGroup(comp, 1);

				Composite mainComp = new Composite(parent, SWT.NONE);
				fProgText = new Text(mainComp, SWT.SINGLE | SWT.BORDER);
				fProgText.setVisible(false);
			}

			@Override
			public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
				configuration.setAttribute(ICDTLaunchConfigurationConstants.ATTR_PROJECT_NAME, EMPTY_STRING);
			}

			@Override
			public void initializeFrom(ILaunchConfiguration configuration) {
				updateProjectFromConfig(configuration);
			}

			@Override
			public void performApply(ILaunchConfigurationWorkingCopy config) {
				super.performApply(config);

				config.setAttribute(ICDTLaunchConfigurationConstants.ATTR_BUILD_BEFORE_LAUNCH, ICDTLaunchConfigurationConstants.BUILD_BEFORE_LAUNCH_DISABLED);

				ICProject cProject = this.getCProject();
				if (cProject != null && cProject.exists()) {
					config.setMappedResources(new IResource[] { cProject.getProject() });
				} else {
					config.setMappedResources(null);
				}

				config.setAttribute(ICDTLaunchConfigurationConstants.ATTR_PROJECT_NAME, fProjText.getText());
			}

			@Override
			public String getName() {
				return "Main";
			}

			@Override
			protected void handleSearchButtonSelected() {
				// Only needed to select an executable
			}

		}, new CommonTab() };

		setTabs(tabs);
	}

}
