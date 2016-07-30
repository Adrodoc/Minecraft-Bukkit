package de.adrodoc55.minecraft.plugins.terrania.gs.commands;

import de.adrodoc55.minecraft.plugins.common.command.CommandContext;
import de.adrodoc55.minecraft.plugins.common.utils.MinecraftUtils;
import de.adrodoc55.minecraft.plugins.terrania.gs.GsManager;

public class SaveGsCommand extends ParamlessGsCommand {

  protected SaveGsCommand() {
    super("save");
  }

  @Override
  protected boolean execute(CommandContext context) {
    GsManager.saveAll();
    String message = "Alle Grundstücke wurden gespeichert.";
    MinecraftUtils.sendInfo(context.getSender(), message);
    return true;
  }

}
