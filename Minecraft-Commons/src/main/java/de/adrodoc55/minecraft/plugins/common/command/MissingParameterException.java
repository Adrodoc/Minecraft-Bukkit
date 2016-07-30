package de.adrodoc55.minecraft.plugins.common.command;

public class MissingParameterException extends ParameterException {
  private static final long serialVersionUID = 1L;

  private static String constructMessage(Parameter parameter) {
    String message =
        String.format("Du musst den Parameter %s angeben um diesen Befehl benutzen zu können.",
            parameter.getDisplay());
    return message;
  }

  public MissingParameterException(Parameter parameter) {
    super(constructMessage(parameter));
  }
}
