package de.adrodoc55.minecraft.plugins.terrania.gs.commands;

import org.bukkit.command.CommandSender;

import de.adrodoc55.minecraft.plugins.common.command.CommandContext;
import de.adrodoc55.minecraft.plugins.common.utils.InsufficientPermissionException;
import de.adrodoc55.minecraft.plugins.common.utils.MinecraftUtils;
import de.adrodoc55.minecraft.plugins.terrania.gs.GsManager;

public class SaveGsCommand extends ParamlessGsCommand {

  protected SaveGsCommand() {
    super("save");
  }

  @Override
  protected boolean execute(CommandContext context) throws InsufficientPermissionException {
    CommandSender sender = context.getSender();
    MinecraftUtils.checkPermission(sender, getPermissionKey());

    GsManager.saveAll();
    String message = "Alle Grundstücke wurden gespeichert.";
    MinecraftUtils.sendInfo(sender, message);
    return true;
  }

}
