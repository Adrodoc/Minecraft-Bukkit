package de.adrodoc55.minecraft.plugins.magic_protection.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.adrodoc55.minecraft.plugins.magic_protection.player.PlayerManager;

public class RangCommandExecutor implements CommandExecutor {

  public static final String COMMAND = "rang";

  @SuppressWarnings("deprecation")
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!COMMAND.equals(command.getName()))
      return false;
    if (args.length < 2)
      return false;
    String action = args[0];
    String playerName = args[1];
    OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
    if (player == null) {
      String message = String
          .format(ChatColor.RED + "Der Spieler '%s' konnte nicht gefunden werden", playerName);
      sender.sendMessage(message);
      return false;
    }
    if ("get".equals(action)) {
      command.setUsage(ChatColor.RED + "/" + COMMAND + " get <player>");
      if (args.length != 2)
        return false;
      int rang = PlayerManager.getPlayerRanking(player);
      String message = String.format("%s hat den Rang %d", playerName, rang);
      sender.sendMessage(message);
      return true;
    } else if ("set".equals(action)) {
      command.setUsage(ChatColor.RED + "/" + COMMAND + " set <player> <value>");
      if (args.length != 3)
        return false;
      int rank;
      try {
        rank = Integer.parseInt(args[2]);
      } catch (NumberFormatException e) {
        String message = String.format(ChatColor.RED + "'%s' ist keine Zahl", args[2]);
        sender.sendMessage(message);
        return false;
      }
      PlayerManager.setPlayerRanking(player, rank);
      String[] messages = new String[2];
      messages[0] = String.format("Der Rang von %s wurde auf %d gesetzt", playerName, rank);
      messages[1] =
          ChatColor.YELLOW + "Der tatsächliche Rang ist " + PlayerManager.getPlayerRanking(player);
      sender.sendMessage(messages);
      return true;
    }

    return false;
  }

}
