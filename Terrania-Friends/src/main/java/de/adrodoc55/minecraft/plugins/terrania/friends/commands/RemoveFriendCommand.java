package de.adrodoc55.minecraft.plugins.terrania.friends.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import de.adrodoc55.common.collections.Closure;
import de.adrodoc55.common.collections.CollectionUtils;
import de.adrodoc55.minecraft.plugins.common.command.CommandContext;
import de.adrodoc55.minecraft.plugins.common.command.Parameter;
import de.adrodoc55.minecraft.plugins.common.command.ParameterList;
import de.adrodoc55.minecraft.plugins.common.command.TabCompleteContext;
import de.adrodoc55.minecraft.plugins.common.utils.MinecraftUtils;
import de.adrodoc55.minecraft.plugins.terrania.friends.FriendsManager;

public class RemoveFriendCommand extends FriendsCommand {

  private static final String PLAYER = "player";

  protected RemoveFriendCommand() {
    super("remove");
  }

  @Override
  protected ParameterList getParamDefinition() {
    ParameterList paramDef = new ParameterList();
    paramDef.add(new Parameter(PLAYER));
    return paramDef;
  }

  @Override
  protected boolean execute(CommandContext context) {
    if (!(context.getSender() instanceof Player)) {
      String message = "Dieser Befehl kann nur von Spielern ausgeführt werden.";
      MinecraftUtils.sendError(context.getSender(), message);
      return false;
    }
    Player player = (Player) context.getSender();
    String playerName = context.get(PLAYER);

    @SuppressWarnings("deprecation")
    Player friend = Bukkit.getPlayer(playerName);
    if (friend == null) {
      String format = "Der Spieler %s konnte nicht gefunden werden.";
      String message = String.format(format, playerName);
      MinecraftUtils.sendError(player, message);
      return false;
    }

    FriendsManager.removeFriend(player, friend);
    String format = "%s ist jetzt nicht mehr dein Freund.";
    String message = String.format(format, friend.getName());
    MinecraftUtils.sendInfo(player, message);
    return true;
  }

  @Override
  protected List<String> tabComplete(TabCompleteContext context) {
    if (!(context.getSender() instanceof Player)) {
      return new ArrayList<String>(0);
    }
    String key = context.getKeyToComplete();
    if (!PLAYER.equals(key)) {
      return new ArrayList<String>(0);
    }

    Player player = (Player) context.getSender();
    Set<OfflinePlayer> friends = FriendsManager.getFriends(player);
    friends.remove(player);
    List<String> friendNames =
        CollectionUtils.collect(friends, new Closure<OfflinePlayer, String>() {
          @Override
          public String call(OfflinePlayer p) {
            return p.getName();
          }
        });
    return friendNames;
  }

}
