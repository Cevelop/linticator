package com.linticator.view.preferences.fieldeditors;

import java.io.File;

import org.eclipse.cdt.core.cdtvariables.CdtVariableException;
import org.eclipse.cdt.core.cdtvariables.ICdtVariable;
import org.eclipse.cdt.internal.core.cdtvariables.DefaultVariableContextInfo;
import org.eclipse.cdt.internal.core.cdtvariables.ICoreVariableContextInfo;
import org.eclipse.cdt.ui.newui.BuildVarListDialog;
import org.eclipse.cdt.utils.cdtvariables.SupplierBasedCdtVariableManager;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.linticator.base.VariablesUtil;
import com.linticator.functional.Function1;

@SuppressWarnings("restriction")
public class FileAndVariablesFieldEditor extends FileFieldEditor {

	public FileAndVariablesFieldEditor(final String name, final String labelText, final Composite parent) {
		super(name, labelText, parent);
	}

	@Override
	protected void doFillIntoGrid(final Composite parent, final int numColumns) {
		super.doFillIntoGrid(parent, numColumns - 1);
		final Button variablesButton = new Button(parent, SWT.NONE);
		variablesButton.setText("Variables...");
		variablesButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {

				final ICdtVariable[] variables = SupplierBasedCdtVariableManager.getVariables(
						new DefaultVariableContextInfo(ICoreVariableContextInfo.CONTEXT_WORKSPACE, ResourcesPlugin
								.getWorkspace()), true);

				final BuildVarListDialog dialog = new BuildVarListDialog(getShell(), variables);
				dialog.setTitle("Variables");
				if (dialog.open() == Window.OK) {
					final Object[] selected = dialog.getResult();
					if (selected.length > 0) {
						final String s = ((ICdtVariable) selected[0]).getName();
						getTextControl().insert("${" + s.trim() + "}");
					}
				}
			}
		});
		final GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		final int widthHint = convertHorizontalDLUsToPixels(variablesButton, IDialogConstants.BUTTON_WIDTH);
		gd.widthHint = Math.max(widthHint, variablesButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
		variablesButton.setLayoutData(gd);
	}

	@Override
	public int getNumberOfControls() {
		return super.getNumberOfControls() + 1;
	}

	@Override
	protected void adjustForNumColumns(final int numColumns) {
		super.adjustForNumColumns(numColumns - 1);
	}

	@Override
	protected boolean checkState() {
		try {
			final String fullPath = VariablesUtil.resolveWorkspaceVariables(getTextControl().getText());
			if (new File(fullPath).isFile()) {
				return true;
			}
		} catch (final CdtVariableException e) {
		}
		return super.checkState();
	}

	public static StringFieldEditor create(final Composite container, final String preferenceConstant, final IPreferenceStore preferenceStore,
			final String displayName, final DialogPage page, final Function1<String, Void> valueChanged) {
		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		final FileAndVariablesFieldEditor fieldEditor = new FileAndVariablesFieldEditor(preferenceConstant, displayName,
				container) {
			@Override
			public void setStringValue(final String value) {
				super.setStringValue(value);
				valueChanged.apply(value);
			}
		};
		fieldEditor.getLabelControl(container).setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		fieldEditor.getTextControl(container).setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		fieldEditor.setEmptyStringAllowed(false);
		fieldEditor.setPage(page);
		fieldEditor.setPreferenceStore(preferenceStore);
		return fieldEditor;
	}
}