package com.linticator.view;

import java.lang.reflect.Field;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.internal.views.markers.ExtendedMarkersView;
import org.eclipse.ui.views.markers.MarkerSupportView;

import com.linticator.base.WorkspaceUtil;
import com.linticator.markers.Message;


@SuppressWarnings("restriction")
public class LibraryProblemsView extends MarkerSupportView {

	public LibraryProblemsView() {
		super("com.linticator.libraryProblemsGenerator");
	}

	@Override
	public void createPartControl(final Composite parent) {
		super.createPartControl(parent);

		try {
			final Field viewerField = ExtendedMarkersView.class.getDeclaredField("viewer");

			viewerField.setAccessible(true);

			final StructuredViewer viewer = (StructuredViewer) viewerField.get(this);

			viewer.addDoubleClickListener(new IDoubleClickListener() {

				@Override
				public void doubleClick(final DoubleClickEvent event) {

					for (final IMarker iMarker : getSelectedMarkers()) {
						try {
							final Object loc = iMarker.getAttribute(Message.EXTERNAL_FILE_LOCATION);
							final Object line = iMarker.getAttribute(Message.EXTERNAL_FILE_LINE);

							if (loc instanceof String && line instanceof Integer) {
								WorkspaceUtil.openFileAtLine(new Path((String) loc), ((Integer) line).intValue());
							}

						} catch (final CoreException e) {
							e.printStackTrace();
						}
					}
				}
			});

		} catch (final SecurityException e) {
		} catch (final IllegalArgumentException e) {
		} catch (final NoSuchFieldException e) {
		} catch (final IllegalAccessException e) {
		}
	}
}
