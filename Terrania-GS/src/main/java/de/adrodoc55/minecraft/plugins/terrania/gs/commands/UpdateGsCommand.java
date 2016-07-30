package de.adrodoc55.minecraft.plugins.terrania.gs.commands;

import org.bukkit.command.CommandSender;

import de.adrodoc55.minecraft.plugins.common.command.CommandContext;
import de.adrodoc55.minecraft.plugins.common.utils.InsufficientPermissionException;
import de.adrodoc55.minecraft.plugins.common.utils.MinecraftUtils;
import de.adrodoc55.minecraft.plugins.terrania.gs.GsManager;

public class UpdateGsCommand extends ParamlessGsCommand {

  protected UpdateGsCommand() {
    super("update");
  }

  @Override
  protected boolean execute(CommandContext context) throws InsufficientPermissionException {
    CommandSender sender = context.getSender();
    MinecraftUtils.checkPermission(sender, getPermissionKey());

    for (GsManager gsm : GsManager.getActiveInstances()) {
      gsm.updateAlleGrundstuecke();
    }
    String message = "Alle Grundstücke wurden geupdated.";
    MinecraftUtils.sendInfo(sender, message);
    return true;
  }

}
