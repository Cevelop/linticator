package com.linticator;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.statushandlers.StatusManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

import com.linticator.beans.Beans;

public class Linticator extends AbstractUIPlugin {

	// DO NOT CHANGE THIS! Existing installations rely on this exact string.
	public static final String PLUGIN_ID = "com.linticator.Linticator"; //$NON-NLS-1$
	public static String PLUGIN_NAME;
	private static final IPath ICONS_PATH = new Path("$nl$/resources"); //$NON-NLS-1$

	private static Beans beans;
	private static Linticator plugin;

	public Linticator() {
		plugin = this;
	}

	@Override
	public IPreferenceStore getPreferenceStore() {
		return super.getPreferenceStore();
	}

	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		PLUGIN_NAME = getBundle().getHeaders().get(Constants.BUNDLE_NAME);
		beans = new Beans();

	}

	public static Beans getBeans() {
		return beans;
	}

	public static Linticator getDefault() {
		return plugin;
	}

	public static String linticatorVersionInformation() {
		return "This is Linticator version \"" + getDefault().getBundle().getHeaders().get("Bundle-Version").toString() + "\".\n";
	}

	public static ImageDescriptor getImageDescriptor(final String relativePath) {
		final IPath path = ICONS_PATH.append(relativePath);
		return createImageDescriptor(getDefault().getBundle(), path);
	}

	private static ImageDescriptor createImageDescriptor(final Bundle bundle, final IPath path) {
		final URL url = FileLocator.find(bundle, path, null);
		return ImageDescriptor.createFromURL(url);
	}

	public static IPath getFormatFile() {
		return getDefault().getStateLocation().append("format.lnt"); //$NON-NLS-1$
	}

	public void handleWarning(final String msg) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				StatusManager.getManager().handle(new Status(Status.WARNING, "com.linticator", msg));
			}
		});
	}

	public void handleError(final String errorkeyPrefix, final Exception e) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				final String msg = errorkeyPrefix + '.' + e.getClass().getSimpleName();
				StatusManager.getManager().handle(new Status(Status.ERROR, "com.linticator", Status.OK, msg, e));
			}
		});
	}

	public BundleContext getBundleContext() {
		return getBundle().getBundleContext();
	}
}