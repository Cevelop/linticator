package com.linticator.quickfixes.inhibitmessages;

import static java.lang.String.format;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNodeSelector;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

import com.linticator.base.ILintMarker;
import com.linticator.base.StringUtil;
import com.linticator.config.ProjectConfig;
import com.linticator.documentation.EntryNotFoundException;
import com.linticator.documentation.IDocumentation;
import com.linticator.functional.Function1;
import com.linticator.quickfixes.LinticatorQuickfix;

@SuppressWarnings("restriction")
public class InhibitMessages {

	private static final Collection<String> FileNamePatterns = Arrays.asList("'FileName'", "FileName", "'file name'");
	private static final Collection<String> SymbolPatterns = Arrays.asList("'Symbol'", "'Name'");

	public static void writeMessageInhibitionOptions(final PrintWriter out, final IDocument document,
			final Collection<MessageInhibitionConfigurationEntry> messages, final IDocumentation documentation) {

		for (final MessageInhibitionConfigurationEntry entry : messages) {
			if (entry.isGlobal()) {
				write(out, inhibitGlobal(entry.getProblem()));
			}
			if (entry.isFile()) {
				write(out, inhibitFile(entry.getProblem(), entry.getMarker(), documentation));
			}
			if (entry.isSym()) {
				write(out, inhibitSymOrName(entry.getProblem(), entry.getMarker(), documentation));
			}
			if (entry.isFunc()) {
				write(out, inhibitFunc(document, entry));
			}
		}
	}

	private static String inhibitFunc(final IDocument document, final MessageInhibitionConfigurationEntry entry) {

		final int invocationLine = entry.getLineStartingFromZero();

		final IASTFunctionDefinition enclosingFunction = findEnclosingFunction(document, invocationLine,
				entry.getMarker());

		if (enclosingFunction == null)
			return "";

		final IBinding binding = enclosingFunction.getDeclarator().getName().resolveBinding();

		final Object[] qualifiedName = CPPVisitor.getQualifiedName(binding);

		final String fullName = StringUtil.join("::", qualifiedName);

		return "-efunc(" + entry.getProblem() + ", " + fullName + ")";
	}

	private static IASTFunctionDefinition findEnclosingFunction(final IDocument document, final int invocationLine,
			final IMarker marker) {

		if (!(marker.getResource() instanceof IFile))
			return null;

		final IFile file = (IFile) marker.getResource();

		IASTTranslationUnit tu;

		try {
			tu = LinticatorQuickfix.getCurrentTranslationUnitWithIndex(file, marker);
		} catch (final CoreException e1) {
			return null;
		}

		final IASTNodeSelector nodeSelector = tu.getNodeSelector(null /* root */);

		int offset;
		try {
			offset = document.getLineOffset(invocationLine);
		} catch (final BadLocationException e) {
			return null;
		}

		final IASTNode firstNodeAtLintMessageLine = nodeSelector.findEnclosingNode(offset, 0);

		if (firstNodeAtLintMessageLine == null) {
			return null;
		}

		IASTFunctionDefinition enclosingFunction = null;
		IASTNode parent = firstNodeAtLintMessageLine;

		do {
			parent = parent.getParent();
			if (parent instanceof IASTFunctionDefinition &&
			/* to avoid false positives, we require that the function doesn't start on the same line as the message */
			functionSpansMoreThanOneLine(parent)) {
				enclosingFunction = (IASTFunctionDefinition) parent;
			}
		} while (parent != null && enclosingFunction == null);

		return enclosingFunction;
	}

	private static boolean functionSpansMoreThanOneLine(final IASTNode parent) {
		return parent.getFileLocation().getStartingLineNumber() != parent.getFileLocation().getEndingLineNumber();
	}

	private static String inhibitFile(final int problem, final IMarker marker, final IDocumentation documentation) {
		return inhibitBasedOnDocumentation(problem, marker, documentation, "-efile(%s, %s)", FileNamePatterns,
				new Function1<String, String>() {

					@Override
					public String apply(final String s) {
						return s;
					}
				});
	}

	public static String inhibitSymOrName(final int problem, final IMarker marker, final IDocumentation documentation) {
		return inhibitBasedOnDocumentation(problem, marker, documentation, "-esym(%s, %s)", SymbolPatterns,
				new Function1<String, String>() {

					@Override
					public String apply(final String s) {
						// s might contain the full signature, but we only need the symbol name
						final int i = s.indexOf('(');
						if (i >= 0) {
							return s.substring(0, i);
						} else {
							return s;
						}
					}
				});
	}

	private static String inhibitBasedOnDocumentation(final int problem, final IMarker marker,
			final IDocumentation documentation, final String inhibit, final Collection<String> placeholders,
			final Function1<String, String> transform) {

		try {
			final String msg = marker.getAttribute(ILintMarker.PROBLEM_DESCRIPTION, "");

			final String msgTpl = documentation.getEntry(problem);

			final String firstPartOfMessage = msgTpl.substring(0, msgTpl.indexOf("  -- "));

			final Matcher matcher = Pattern.compile(makeRegexPattern(firstPartOfMessage, placeholders)).matcher(msg);

			if (matcher.find()) {
				return createFormattedInhibitionString(problem, inhibit, transform, matcher);
			} else {
				// Lint's message does not always exactly match the documentation,
				// so we need to try a more relaxed pattern:
				final Matcher m = Pattern.compile(".*'(.*?)'.*").matcher(msg);
				if (m.find()) {
					return createFormattedInhibitionString(problem, inhibit, transform, m);
				}
			}
		} catch (final EntryNotFoundException e) {
		}

		return "";
	}

	private static String createFormattedInhibitionString(final int problem, final String inhibit,
			final Function1<String, String> transform, final Matcher matcher) {
		return format(inhibit, problem, transform.apply(matcher.group(1)));
	}

	private static String makeRegexPattern(final String firstPartOfMessage, final Collection<String> placeholder) {
		for (final String n : placeholder) {
			if (firstPartOfMessage.contains(n)) {
				return firstPartOfMessage.replaceFirst("(?ms)" + n + ".*", "'(.*?)'.*");
			}
		}
		return "";
	}

	public static boolean canInhibitBasedOnFile(final int message, final IDocumentation documentation) {
		return canInhibitBasedOnPattern(message, documentation, FileNamePatterns);
	}

	public static boolean canInhibitBasedOnSymbol(final int message, final IDocumentation documentation) {
		return canInhibitBasedOnPattern(message, documentation, SymbolPatterns);
	}

	public static boolean canInhibitBasedOnFunction(final IDocument document, final int invocationLine,
			final IMarker marker) {
		return findEnclosingFunction(document, invocationLine, marker) != null;
	}

	public static boolean withCustomLintFile(final IProject project, final Function1<PrintWriter, Void> out) {

		PrintWriter printWriter = null;

		try {

			final ProjectConfig projectConfig = new ProjectConfig(project);

			if (projectConfig.getCustomLintFile().equals("")
					&& CustomLintFileWizard.open(projectConfig) == Dialog.CANCEL) {
				return false;
			}

			String customLintFile = projectConfig.getCustomLintFile();

			FileWriter fileWriter = new FileWriter(customLintFile, true);
			printWriter = new PrintWriter(fileWriter);

			out.apply(printWriter);

		} catch (final IOException e) {
		} catch (final CoreException e) {
		} finally {
			if (printWriter != null) {
				printWriter.close();
			}
		}
		return true;
	}

	private static boolean canInhibitBasedOnPattern(final int message, final IDocumentation documentation,
			final Collection<String> patterns) {
		try {
			final String msg = documentation.getEntry(message);
			for (final String n : patterns) {
				if (msg.contains(n))
					return true;
			}
		} catch (final EntryNotFoundException e) {
		}
		return false;
	}

	public static void write(final PrintWriter out, final String directive) {
		// we could add some information, e.g. the username, date, message, etc.
		out.println(directive);
	}

	private static String inhibitGlobal(final int i) {
		return "-e" + i;
	}
}
