package com.linticator.view.console;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.ui.console.FileLink;

import com.linticator.base.WorkspaceUtil;

class FileLinkForExternalFiles extends FileLink {
	private final IPath path;
	private final int line;

	public FileLinkForExternalFiles(final IPath path) {
		this(path, -1);
	}

	public FileLinkForExternalFiles(final IPath path, final int line) {
		super(ResourcesPlugin.getWorkspace().getRoot().getFile(path), null, -1, -1, line);
		this.path = path;
		this.line = line;
	}

	@Override
	public void linkActivated() {
		WorkspaceUtil.openFileAtLine(path, line);
	}
}