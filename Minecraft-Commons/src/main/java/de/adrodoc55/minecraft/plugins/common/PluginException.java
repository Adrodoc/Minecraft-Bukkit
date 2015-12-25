package de.adrodoc55.minecraft.plugins.common;


public class PluginException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private static String prefixMessageWithErrorCode(int errorCode,
			String message) {
		return "[ERROR " + String.valueOf(errorCode) + "] " + message;
	}

	public PluginException(int errorCode, String message) {
		super(prefixMessageWithErrorCode(errorCode, message));
	}

	public PluginException(int errorCode, String message, Throwable cause) {
		super(prefixMessageWithErrorCode(errorCode, message), cause);
	}

}
