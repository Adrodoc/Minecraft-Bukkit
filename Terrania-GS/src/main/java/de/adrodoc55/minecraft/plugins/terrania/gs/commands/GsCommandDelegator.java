package de.adrodoc55.minecraft.plugins.terrania.gs.commands;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import de.adrodoc55.common.CommonUtils;
import de.adrodoc55.minecraft.plugins.common.command.CommandHandler;
import de.adrodoc55.minecraft.plugins.common.utils.CommandUtils;

public class GsCommandDelegator implements CommandHandler {

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    List<String> subCommandNames = getSubCommandNames(sender);
    String usage = ChatColor.RED + "/" + GsCommand.COMMAND + " <"
        + CommonUtils.join(" | ", subCommandNames) + ">";
    command.setUsage(usage);
    if (args.length < 1) {
      return false;
    }
    GsCommand subCommand = GsCommand.getInstance(args[0]);
    if (subCommand == null) {
      return false;
    }
    return subCommand.onCommand(sender, command, label, args);
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String alias,
      String[] args) {
    List<String> subCommandNames = getSubCommandNames(sender);
    if (args.length == 0) {
      return new ArrayList<String>(0);
    } else if (args.length == 1) {
      String toComplete = args[0];
      return CommandUtils.elementsStartsWith(subCommandNames, toComplete);
    } else {
      GsCommand gsCommand = GsCommand.getInstance(args[0]);
      if (gsCommand == null) {
        return new ArrayList<String>(0);
      }
      return gsCommand.onTabComplete(sender, command, alias, args);
    }
  }

  /**
   * Get the names of all sub commands that the sender has permission to use.
   *
   * @param sender whos permissions are to be checked
   * @return a list of sub command names
   */
  private List<String> getSubCommandNames(CommandSender sender) {
    List<GsCommand> subCommands = GsCommand.getCommands();
    List<String> subCommandNames = subCommands.stream()//
        .filter(c -> c.mightHavePermission(sender))//
        .map(c -> c.getName()).collect(toList());
    return subCommandNames;
  }

}
