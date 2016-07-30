package de.adrodoc55.minecraft.plugins.terrania.gs.commands;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import de.adrodoc55.common.CommonUtils;
import de.adrodoc55.common.collections.Closure;
import de.adrodoc55.common.collections.CollectionUtils;
import de.adrodoc55.minecraft.plugins.common.command.CommandContext;
import de.adrodoc55.minecraft.plugins.common.utils.InsufficientPermissionException;
import de.adrodoc55.minecraft.plugins.common.utils.MinecraftUtils;
import de.adrodoc55.minecraft.plugins.terrania.gs.Grundstueck;
import de.adrodoc55.minecraft.plugins.terrania.gs.GsManager;

public class ListGsCommand extends ParamlessGsCommand {

  protected ListGsCommand() {
    super("list");
  }

  @Override
  protected boolean execute(CommandContext context) throws InsufficientPermissionException {
    CommandSender sender = context.getSender();
    MinecraftUtils.checkPermission(sender, "terrania.gs.commands.gs." + getName());

    StringBuilder sb = new StringBuilder();
    sb.append("Grundstücke:\n");
    Collection<GsManager> gsManagers = GsManager.getActiveInstances();
    boolean empty = true;
    for (GsManager gsm : gsManagers) {
      Set<Grundstueck> grundstuecke = gsm.getGrundstuecke();
      if (!grundstuecke.isEmpty()) {
        empty = false;
        sb.append(" Welt " + gsm.getWorld().getName() + ":\n");
        List<String> names =
            CollectionUtils.collect(grundstuecke, new Closure<Grundstueck, String>() {
              @Override
              public String call(Grundstueck parameter) {
                return "  - " + parameter.getName();
              }
            });
        sb.append(CommonUtils.join("\n", names));
        sb.append("\n");
      }
    }
    if (empty) {
      sb.append(ChatColor.GOLD + "  keine Grundstücke vorhanden.\n");
    }
    String message = sb.toString();
    MinecraftUtils.sendInfo(sender, message);
    return true;
  }
}
