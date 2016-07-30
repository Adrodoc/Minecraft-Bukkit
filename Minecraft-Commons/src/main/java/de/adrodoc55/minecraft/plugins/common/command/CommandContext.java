package de.adrodoc55.minecraft.plugins.common.command;

import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CommandContext extends AbstractCommandHandlerContext {
  private final Map<String, String> params;

  public CommandContext(CommandSender sender, Command command, String label, String[] args,
      ParameterList paramDef) throws ParameterException {
    super(sender, command, label);

    if (args.length > paramDef.getParams().size()) {
      String message = "Du hast zu viele Parameter angegeben.";
      throw new ParameterException(message);
    }
    paramDef.fill(args);
    this.params = paramDef.toMap();
  }

  public String get(String param) {
    return params.get(param);
  }

}
