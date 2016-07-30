package de.adrodoc55.minecraft.plugins.common.command;

/**
 * This exception should be thrown by subclasses of {@link AbstractCommandHandler} if the Command
 * Syntax is correct, but the execution can still not be performed. For example if a command is
 * executed by the console, but may only be used by a player.
 *
 * @author Adrodoc55
 */
public class CommandException extends Exception {
  private static final long serialVersionUID = 1L;

  public CommandException() {
    super();
  }

  public CommandException(String message, Throwable cause) {
    super(message, cause);
  }

  public CommandException(String message) {
    super(message);
  }

  public CommandException(Throwable cause) {
    super(cause);
  }
}
