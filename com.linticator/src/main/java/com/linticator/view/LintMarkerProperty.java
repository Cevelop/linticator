package com.linticator.view;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.views.markers.MarkerItem;

public class LintMarkerProperty extends PropertyTester {

	public LintMarkerProperty() {
	}

	@Override
	public boolean test(final Object receiver, final String property, final Object[] args, final Object expectedValue) {

		assert (args.length == 1 && args[0] instanceof String);

		if (receiver instanceof MarkerItem) {

			final MarkerItem markerItem = (MarkerItem) receiver;

			final IMarker marker = markerItem.getMarker();

			if (marker == null) {
				return false;
			}

			try {
				final String type = marker.getType();
				return type.equals(args[0]);
			} catch (final CoreException e) {
				return false;
			}
		}
		return false;
	}
}
