package de.adrodoc55.minecraft.plugins.terrania.gs.commands;

import java.util.ArrayList;
import java.util.List;

import de.adrodoc55.minecraft.plugins.common.command.CommandContext;
import de.adrodoc55.minecraft.plugins.common.command.ParameterList;
import de.adrodoc55.minecraft.plugins.common.command.TabCompleteContext;
import de.adrodoc55.minecraft.plugins.common.utils.MinecraftUtils;
import de.adrodoc55.minecraft.plugins.terrania.gs.Grundstueck;
import de.adrodoc55.minecraft.plugins.terrania.gs.GsManager;

public class DeleteGsCommand extends ConcreteGsCommand {

    protected DeleteGsCommand() {
        super("delete");
    }

    @Override
    protected boolean execute(CommandContext context, Grundstueck grundstueck) {
        String message = String.format(
                "Das Grundstück %s wurde erfolgreich entfernt.",
                grundstueck.getName());
        GsManager.remove(grundstueck);
        MinecraftUtils.sendInfo(context.getSender(), message);
        return true;
    }

    @Override
    protected void addAdditionalParams(ParameterList parameterList) {

    }

    @Override
    protected List<String> tabCompleteAdditionalParams(
            TabCompleteContext context) {
        return new ArrayList<String>(0);
    }

}
