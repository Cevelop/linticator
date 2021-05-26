package com.linticator.markers;

public abstract class MessageFactory {
	public static Message create(final MessageParameters params, final String messageLevel) throws InvalidMessageLevelException {
		
		if (messageLevel.equals(InformationMessage.MESSAGE_LEVEL)) {
			return new InformationMessage(params);
		}
		if (messageLevel.equals(WarningMessage.MESSAGE_LEVEL)) {
			return new WarningMessage(params);
		}
		if (messageLevel.equals(ErrorMessage.MESSAGE_LEVEL)) {
			return new ErrorMessage(params);
		}
		if (messageLevel.equals(InformationMessage.MESSAGE_LEVEL_NOTE)) {
			return new InformationMessage(params);
		}
		throw new InvalidMessageLevelException();
	}
}
