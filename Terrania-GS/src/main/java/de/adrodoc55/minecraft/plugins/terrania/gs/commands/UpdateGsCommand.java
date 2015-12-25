package de.adrodoc55.minecraft.plugins.terrania.gs.commands;

import de.adrodoc55.minecraft.plugins.common.command.CommandContext;
import de.adrodoc55.minecraft.plugins.common.utils.MinecraftUtils;
import de.adrodoc55.minecraft.plugins.terrania.gs.Grundstueck;
import de.adrodoc55.minecraft.plugins.terrania.gs.GsManager;

public class UpdateGsCommand extends ParamlessGsCommand {

	protected UpdateGsCommand() {
		super("update");
	}

	@Override
	protected boolean execute(CommandContext context) {
		for (GsManager gsm : GsManager.getActiveInstances()) {
			for (Grundstueck gs : gsm.getGrundstuecke()) {
				gs.updateSignConent();
			}
		}
		String message = "Alle Grundstücke wurden geupdated.";
		MinecraftUtils.sendInfo(context.getSender(), message);
		return true;
	}

}
