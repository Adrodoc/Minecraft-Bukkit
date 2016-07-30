package de.adrodoc55.minecraft.plugins.common.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public abstract class AbstractCommandHandlerContext {
  private final CommandSender sender;
  private final Command command;
  private final String alias;
  // protected final String[] args;

  public AbstractCommandHandlerContext(CommandSender sender, Command command, String alias) {
    this.sender = sender;
    this.command = command;
    this.alias = alias;
    // this.args = args;
  }

  // public String get(int index) {
  // return args[0];
  // }

  // public int getArgsLength() {
  // return args.length;
  // }

  /**
   * Sets the example usage of the command. This is a conveniance method for
   * getCommand.setUsage(usage);
   *
   * @param usage new example usage
   * @return the command object, for chaining
   */
  public Command setUsage(String usage) {
    return command.setUsage(usage);
  }

  public CommandSender getSender() {
    return sender;
  }

  public Command getCommand() {
    return command;
  }

  public String getLabel() {
    return alias;
  }

  public String getAlias() {
    return alias;
  }

}
