package com.linticator.lint.parsing;

import static com.linticator.base.ParserUtil.ESCAPED_LINE_SEPARATOR;
import static com.linticator.base.ParserUtil.parseIntOrZero;
import static com.linticator.base.ParserUtil.trimLintMultilineText;
import static com.linticator.base.ParserUtil.trimStringOrEmptyOnNull;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.linticator.base.ParserUtil;
import com.linticator.markers.InvalidMessageLevelException;
import com.linticator.markers.Message;
import com.linticator.markers.MessageFactory;
import com.linticator.markers.MessageParameters;

public class Parser {
	private static final Pattern FORMATTED_LINT_MESSAGE = Pattern.compile(ParserUtil.FORMATTED_LINT_MESSAGE_REGEX);
	private static final Pattern LINT_MESSAGE = Pattern.compile("((.+\\s+)(\\d+)\\s+)?(\\w+)\\s+(\\d+):\\s+((.|" //$NON-NLS-1$
			+ ESCAPED_LINE_SEPARATOR + "\\s{4}[^\\s])+)"); //$NON-NLS-1$
	private static final Pattern GLOBAL_MSG_PATTERN = Pattern.compile("(.*)line (\\d*), file (.*?)(, module .*)?\\).*"); //$NON-NLS-1$
	private static final int LOCATION_CITED_IN_PRIOR_MESSAGE = 830;
	private static final int REFERENCE_CITED_IN_PRIOR_MESSAGE = 831;

	private final IProgressMonitor progressMonitor;
	private final int workLoad;
	private final LineParserFactory lineParserFactory;

	public Parser() {
		this(new NullProgressMonitor());
	}

	public Parser(final IProgressMonitor progressMonitor) {
		this(progressMonitor, 50);
	}

	public Parser(final IProgressMonitor progressMonitor, final int workLoad) {
		this.progressMonitor = progressMonitor;
		this.workLoad = workLoad;
		lineParserFactory = new LineParserFactory();
	}

	public Set<Message> parseAllMessagesIn(final String lintOutput) {
		Set<Message> messages = Collections.emptySet();
		messages = parseFormatted(lintOutput);
		progressMonitor.worked(workLoad / 2);
		if (messages.isEmpty()) {
			messages.addAll(parseUnFormatted(lintOutput));
		}
		progressMonitor.worked(workLoad / 2);
		return messages;
	}

	public Set<Message> parse(final String lintOutput) {
		return removeUnwantedMassages(parseAllMessagesIn(lintOutput));
	}

	private Set<Message> parseFormatted(final String output) {
		final Set<Message> messages = new LinkedHashSet<Message>();
		final Matcher matcher = FORMATTED_LINT_MESSAGE.matcher(output);
		int pos = 0;
		while (matcher.find(pos)) {
			int group = 2;
			String sourceFile = matcher.group(group++);
			final String lineString = matcher.group(group++);
			final int column = parseIntOrZero(matcher.group(group++));
			final String messageLevel = matcher.group(group++).toLowerCase(); // PC-Lint Plus has lowercase levels
			final int messageCode = parseIntOrZero(matcher.group(group++));
			final String description = matcher.group(group++).replaceAll(System.getProperty("line.separator"), " "); //$NON-NLS-1$//$NON-NLS-2$
			final LineParser lineParser = lineParserFactory.getLineParser(messageCode);
			final int quickfixLine = lineParser.getQuickfixLineNumber(lineString, description);
			int line = lineParser.getLineNumber(lineString, description);
			if (sourceFile != null && sourceFile.endsWith(".lnt")) { //$NON-NLS-1$ //Bad hack but keeps me sane
				sourceFile = null;
				line = 0;
			}
			if (sourceFile == null && line == 0) {
				final Matcher m = GLOBAL_MSG_PATTERN.matcher(description);
				if (m.find()) {
					sourceFile = m.group(3);
					line = parseIntOrZero(m.group(2));
				}
			}
			try {
				messages.add(MessageFactory.create(new MessageParameters(sourceFile, line, quickfixLine, column, messageCode, description), messageLevel));
			} catch (final InvalidMessageLevelException Ignored) {
			}
			pos = matcher.end();
		}
		return messages;
	}

	protected int getLine(final String lineString, final String description) {
		return parseIntOrZero(lineString);
	}

	private Set<Message> parseUnFormatted(final String output) {
		final Set<Message> messages = new LinkedHashSet<Message>();
		int pos = 0;
		final Matcher matcher = LINT_MESSAGE.matcher(output);
		while (matcher.find(pos)) {
			int group = 2;
			final String sourceFile = trimStringOrEmptyOnNull(matcher.group(group++));
			final int line = parseIntOrZero(matcher.group(group++));
			final String messageLevel = matcher.group(group++).toLowerCase();
			final int messageCode = parseIntOrZero(matcher.group(group++));
			final String description = trimLintMultilineText(matcher.group(group++));
			try {
				messages.add(MessageFactory.create(new MessageParameters(sourceFile, line, 0, messageCode, description), messageLevel));
			} catch (final InvalidMessageLevelException Ignored) {
			}
			pos = matcher.end();
		}
		return messages;
	}

	private Set<Message> removeUnwantedMassages(final Set<Message> messages) {
		final Set<Message> filtered = new LinkedHashSet<Message>();
		for (final Message msg : messages) {
			if (!isFollowUpLocationMessage(msg)) {
				filtered.add(msg);
			}
		}
		return filtered;
	}

	private boolean isFollowUpLocationMessage(final Message message) {
		final int messageCode = message.getMessageCode();
		return messageCode == LOCATION_CITED_IN_PRIOR_MESSAGE || messageCode == REFERENCE_CITED_IN_PRIOR_MESSAGE;
	}
}
