package com.linticator.documentation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.markers.MarkerItem;

import com.linticator.Linticator;


public class DocumentationView extends ViewPart {
	private static final String PLEASE_ENTER_A_VALID_LINT_MESSAGE_ID = "Please enter a valid Lint message ID.";

	public DocumentationView() {
	}
	public static final String ID = "com.linticator.view.DocumentationView"; //$NON-NLS-1$
	private ISelectionListener pageSelectionListener;
	private Label lblMessage;
	private StyledText text;
	private final IDocumentation documentation = Linticator.getBeans().getDocumentation();
	private Text messageNumberText;
	private Composite composite;
	private Button btnShow;

	@Override
	public void createPartControl(final Composite parent) {
		final Composite panel = new Composite(parent, SWT.NONE);
		final GridLayout gl_panel = new GridLayout(1, false);
		gl_panel.marginWidth = 0;
		gl_panel.marginHeight = 0;
		panel.setLayout(gl_panel);

		composite = new Composite(panel, SWT.NONE);
		composite.setLayout(new GridLayout(3, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		lblMessage = new Label(composite, SWT.NONE);
		lblMessage.setText("Message ID: ");

		messageNumberText = new Text(composite, SWT.BORDER | SWT.RIGHT);
		messageNumberText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		messageNumberText.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
				updateDocumentation();
			}
		});

		btnShow = new Button(composite, SWT.NONE);
		btnShow.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				updateDocumentation();
			}
		});
		btnShow.setText("Show");

		text = new StyledText(panel, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		text.setText("Select a Linticator entry in the Problems view.");
		text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		text.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		
		try {
			// setMargins is not public in Eclipse < 3.5, so we call it using reflection.
			Method setMargin = text.getClass().getMethod("setMargins", new Class[]{int.class, int.class, int.class, int.class});
			setMargin.invoke(text, 4, 2, 2, 2);
		} catch (SecurityException e1) {
		} catch (NoSuchMethodException e1) {
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}
		
		hookPageSelection();
	}

	private void updateDocumentation() {
		try {
			showDocumentation(null, Integer.parseInt(messageNumberText.getText()));
		} catch (final NumberFormatException _) {
			text.setText(PLEASE_ENTER_A_VALID_LINT_MESSAGE_ID);
		}
	}

	private void hookPageSelection() {
		pageSelectionListener = new ISelectionListener() {
			@Override
			public void selectionChanged(final IWorkbenchPart part, final ISelection selection) {
				pageSelectionChanged(part, selection);
			}
		};
		getSite().getPage().addPostSelectionListener(pageSelectionListener);
	}

	@Override
	public void dispose() {
		getSite().getPage().removePostSelectionListener(pageSelectionListener);
		super.dispose();
	}

	protected void pageSelectionChanged(final IWorkbenchPart part, final ISelection selection) {
		if (part == this)
			return;
		showDocumentationForSelection(selection);
	}

	public void showDocumentationForSelection(final ISelection selection) {
		if (!(selection instanceof IStructuredSelection))
			return;

		final IStructuredSelection sel = (IStructuredSelection) selection;
		final Iterator<?> itr = sel.iterator();
		while (itr.hasNext()) {
			final Object item = itr.next();
			if (item instanceof MarkerItem) {
				final MarkerItem mItem = (MarkerItem) item;
				try {
					final int code = Integer.parseInt(mItem.getAttributeValue(IMarker.PROBLEM, "0")); //$NON-NLS-1$
					if (code > 0) {
						messageNumberText.setText(String.valueOf(code));
						showDocumentation(mItem.getMarker(), code);
					}
				} catch (final Exception e) {
					continue;
				}
			}
		}
	}

	private void showDocumentation(final IMarker marker, final int code) {
		try {
			text.setText(documentation.documentationForMarker(code, marker, DocumentationFromMsgTxt.PLAIN_SEPARATOR));
		} catch (final EntryNotFoundException e) {
			text.setText(PLEASE_ENTER_A_VALID_LINT_MESSAGE_ID);
		}
	}

	@Override
	public void setFocus() {
	}

}
