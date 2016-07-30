package de.adrodoc55.minecraft.plugins.terrania.gs.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.OfflinePlayer;
import org.bukkit.block.Sign;

import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.domains.PlayerDomain;

import de.adrodoc55.common.CommonUtils;
import de.adrodoc55.minecraft.plugins.common.command.CommandContext;
import de.adrodoc55.minecraft.plugins.common.command.ParameterList;
import de.adrodoc55.minecraft.plugins.common.command.TabCompleteContext;
import de.adrodoc55.minecraft.plugins.common.utils.MinecraftUtils;
import de.adrodoc55.minecraft.plugins.terrania.gs.Grundstueck;

public class InfoGsCommand extends ConcreteGsCommand {

  protected InfoGsCommand() {
    super("info");
  }

  @Override
  protected boolean execute(CommandContext context, Grundstueck gs) {
    StringBuilder sb = new StringBuilder();
    sb.append("Informationen zum Grundstück " + gs.getName() + ":\n");
    OfflinePlayer owner = gs.getOwner();
    if (owner != null) {
      sb.append(" - Besitzer: " + owner.getName() + "\n");
    }
    sb.append(" - Preis: " + CommonUtils.doubleToString(gs.getPrice()) + "\n");
    sb.append(" - verbleibende Tage: " + gs.getDaysLeft() + "\n");
    sb.append(" - Welt: " + gs.getWorld().getName() + "\n");
    Sign sign = gs.getSign();
    sb.append(" - Schild: x=" + sign.getX() + " y=" + sign.getY() + " z=" + sign.getZ() + "\n");
    DefaultDomain members = gs.getRegion().getMembers();
    PlayerDomain players = members.getPlayerDomain();
    Set<String> playerNames = players.getPlayers();
    // Set<UUID> playerIds = players.getUniqueIds();
    // GroupDomain groupDomain = members.getGroupDomain();
    // Set<String> groups = groupDomain.getGroups();
    if (!(playerNames.isEmpty()
    // && playerIds.isEmpty()
    // && groups.isEmpty()
    )) {
      sb.append(" - Members:");
      for (String name : playerNames) {
        sb.append("    " + name);
      }
    }
    sb.append("\n");
    String message = sb.toString();
    MinecraftUtils.sendInfo(context.getSender(), message);
    return true;
  }

  @Override
  protected void addAdditionalParams(ParameterList parameterList) {

  }

  @Override
  protected List<String> tabCompleteAdditionalParams(TabCompleteContext context) {
    return new ArrayList<String>(0);
  }

}
