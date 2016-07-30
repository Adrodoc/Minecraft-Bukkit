package de.adrodoc55.minecraft.plugins.common.utils;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MinecraftUtils {

  public static void setXp(Player player, double xp) {
    int level = (int) xp;
    float exp = (float) (xp - (double) level);
    player.setLevel(level);
    player.setExp(exp);
  }

  public static Location getCenteredBlockLocation(Block block) {
    Location location = block.getLocation().add(0.5f, 0.5f, 0.5f);
    return location;
  }

  public static Location getCenteredBlockLocation(Block block, BlockFace blockFace) {
    Location location = getCenteredBlockLocation(block);
    double modX = ((double) blockFace.getModX()) / 2;
    double modY = ((double) blockFace.getModY()) / 2;
    double modZ = ((double) blockFace.getModZ()) / 2;
    Location modifier = new Location(block.getWorld(), modX, modY, modZ);
    location.add(modifier);
    return location;
  }

  private static Thread thread;
  private static Map<CommandSender, String> lastMessages = new HashMap<CommandSender, String>();

  /**
   * Sendet eine Nachricht an den Spieler, falls in den letzten 9 Sekunden nicht die selbe Nachricht
   * bereits an diesen Spieler geschickt wurde.
   * 
   * Diese Methode tut nichts, wenn eines der Argumente null ist.
   * 
   * @param commandSender
   * @param message
   */
  public static void sendMessage(final CommandSender commandSender, String message) {
    if (commandSender == null || message == null) {
      return;
    }
    String lastMessage = lastMessages.get(commandSender);
    if (message.equals(lastMessage)) {
      return;
    }
    if (thread != null)
      thread.interrupt();
    lastMessages.put(commandSender, message);
    commandSender.sendMessage(message);
    thread = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          Thread.sleep(9000);
          lastMessages.remove(commandSender);
        } catch (InterruptedException e) {
          // NOT lastMessages.remove(player);
        }
      }
    });
    thread.start();
  }

  public static void sendInfo(final CommandSender commandSender, String message) {
    sendMessage(commandSender, ChatColor.YELLOW + message);
  }

  public static void sendError(final CommandSender commandSender, String message) {
    sendMessage(commandSender, ChatColor.RED + message);
  }

}
