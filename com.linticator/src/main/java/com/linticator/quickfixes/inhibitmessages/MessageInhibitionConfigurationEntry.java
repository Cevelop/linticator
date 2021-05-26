package com.linticator.quickfixes.inhibitmessages;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.IDocument;

import com.linticator.documentation.IDocumentation;
import com.linticator.functional.Function1;
import com.linticator.functional.Function2;

class ConfigEntryTriple {

	final String name;
	final Function1<MessageInhibitionConfigurationEntry, Boolean> getter;
	final Function2<MessageInhibitionConfigurationEntry, Boolean, Void> setter;
	final Function1<MessageInhibitionConfigurationEntry, Boolean> editable;

	public ConfigEntryTriple(final String name, final Function1<MessageInhibitionConfigurationEntry, Boolean> getter,
			final Function2<MessageInhibitionConfigurationEntry, Boolean, Void> setter,
			final Function1<MessageInhibitionConfigurationEntry, Boolean> editable) {
		this.name = name;
		this.getter = getter;
		this.setter = setter;
		this.editable = editable;
	}
}

public class MessageInhibitionConfigurationEntry {

	private boolean file = false;

	private boolean func = false;

	private boolean global = false;

	private boolean sym = false;

	private final IMarker marker;

	private final IDocumentation documentation;

	private final IDocument document;

	public MessageInhibitionConfigurationEntry(final IDocument document, final IMarker marker,
			final IDocumentation documentation) {
		this.document = document;
		this.marker = marker;
		this.documentation = documentation;
	}

	public IProject getProject() {
		return marker.getResource().getProject();
	}

	public String displayString() {
		return marker.getAttribute(IMarker.MESSAGE, "?");
	}

	public int getLineStartingFromZero() {
		return marker.getAttribute(IMarker.LINE_NUMBER, 0) - 1;
	}

	public IMarker getMarker() {
		return marker;
	}

	public boolean isFile() {
		return file;
	}

	public boolean isFunc() {
		return func;
	}

	public boolean isGlobal() {
		return global;
	}

	public boolean isSym() {
		return sym;
	}

	public int getProblem() {
		return marker.getAttribute(IMarker.PROBLEM, 0);
	}

	public static Collection<ConfigEntryTriple> getAllConfigurableOptions() {
		final Collection<ConfigEntryTriple> entries = new ArrayList<ConfigEntryTriple>();

		entries.add(new ConfigEntryTriple("Global", new Function1<MessageInhibitionConfigurationEntry, Boolean>() {

			@Override
			public Boolean apply(final MessageInhibitionConfigurationEntry t) {
				return t.global;
			}
		}, new Function2<MessageInhibitionConfigurationEntry, Boolean, Void>() {

			@Override
			public Void apply(final MessageInhibitionConfigurationEntry t, final Boolean u) {
				t.global = u;
				return null;
			}
		}, new Function1<MessageInhibitionConfigurationEntry, Boolean>() {

			@Override
			public Boolean apply(final MessageInhibitionConfigurationEntry t) {
				return true;
			}
		}));

		entries.add(new ConfigEntryTriple("File", new Function1<MessageInhibitionConfigurationEntry, Boolean>() {

			@Override
			public Boolean apply(final MessageInhibitionConfigurationEntry t) {
				return t.file;
			}
		}, new Function2<MessageInhibitionConfigurationEntry, Boolean, Void>() {

			@Override
			public Void apply(final MessageInhibitionConfigurationEntry t, final Boolean u) {
				t.file = u;
				return null;
			}
		}, new Function1<MessageInhibitionConfigurationEntry, Boolean>() {

			@Override
			public Boolean apply(final MessageInhibitionConfigurationEntry t) {
				return InhibitMessages.canInhibitBasedOnFile(t.getProblem(), t.documentation);
			}
		}));

		entries.add(new ConfigEntryTriple("Func", new Function1<MessageInhibitionConfigurationEntry, Boolean>() {

			@Override
			public Boolean apply(final MessageInhibitionConfigurationEntry t) {
				return t.func;
			}
		}, new Function2<MessageInhibitionConfigurationEntry, Boolean, Void>() {

			@Override
			public Void apply(final MessageInhibitionConfigurationEntry t, final Boolean u) {
				t.func = u;
				return null;
			}
		}, new Function1<MessageInhibitionConfigurationEntry, Boolean>() {

			@Override
			public Boolean apply(final MessageInhibitionConfigurationEntry t) {
				return InhibitMessages.canInhibitBasedOnFunction(t.document, t.getLineStartingFromZero(), t.marker);
			}
		}));

		entries.add(new ConfigEntryTriple("Sym", new Function1<MessageInhibitionConfigurationEntry, Boolean>() {

			@Override
			public Boolean apply(final MessageInhibitionConfigurationEntry t) {
				return t.sym;
			}
		}, new Function2<MessageInhibitionConfigurationEntry, Boolean, Void>() {

			@Override
			public Void apply(final MessageInhibitionConfigurationEntry t, final Boolean u) {
				t.sym = u;
				return null;
			}
		}, new Function1<MessageInhibitionConfigurationEntry, Boolean>() {

			@Override
			public Boolean apply(final MessageInhibitionConfigurationEntry t) {
				return InhibitMessages.canInhibitBasedOnSymbol(t.getProblem(), t.documentation);
			}
		}));

		return entries;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getLineStartingFromZero();
		result = prime * result + ((marker == null) ? 0 : marker.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final MessageInhibitionConfigurationEntry other = (MessageInhibitionConfigurationEntry) obj;
		if (getLineStartingFromZero() != other.getLineStartingFromZero())
			return false;
		if (marker == null) {
			if (other.marker != null)
				return false;
		} else if (!marker.equals(other.marker))
			return false;
		return true;
	}
}