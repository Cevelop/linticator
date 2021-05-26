package com.linticator.quickfixes.ignorefunction;

import java.io.PrintWriter;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;

import com.linticator.Linticator;
import com.linticator.documentation.IDocumentation;
import com.linticator.functional.Function1;
import com.linticator.quickfixes.LinticatorQuickfix;
import com.linticator.quickfixes.inhibitmessages.InhibitMessages;

public class IgnoreFunction extends LinticatorQuickfix {

	public IgnoreFunction(final IMarker marker, final int code) {
		super("Ignore message \"Ignoring return value\" for this function", marker, code);
	}

	@Override
	protected boolean runOnFile(final IMarker marker, final IFile file) throws Exception {

		return InhibitMessages.withCustomLintFile(file.getProject(), new Function1<PrintWriter, Void>() {
			@Override
			public Void apply(final PrintWriter writer) {

				final IDocumentation doc = Linticator.getBeans().getDocumentation();
				final String directive = InhibitMessages.inhibitSymOrName(code, marker, doc);
				InhibitMessages.write(writer, directive);

				return null;
			}
		});
	}
}