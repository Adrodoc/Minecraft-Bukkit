package de.adrodoc55.minecraft.plugins.terrania.gs.commands;

import java.util.ArrayList;
import java.util.List;

import de.adrodoc55.minecraft.plugins.common.command.CommandContext;
import de.adrodoc55.minecraft.plugins.common.command.Parameter;
import de.adrodoc55.minecraft.plugins.common.command.ParameterList;
import de.adrodoc55.minecraft.plugins.common.command.TabCompleteContext;
import de.adrodoc55.minecraft.plugins.common.utils.MinecraftUtils;
import de.adrodoc55.minecraft.plugins.terrania.gs.Grundstueck;

public class SetpriceGsCommand extends ConcreteGsCommand {

  private static final String PRICE = "price";

  protected SetpriceGsCommand() {
    super("setprice");
  }

  @Override
  protected boolean execute(CommandContext context, Grundstueck gs) {
    String price = context.get(PRICE);
    try {
      gs.setPrice(Double.parseDouble(price));
      gs.updateSignContent();
    } catch (NumberFormatException ex) {
      String message = String.format("%s ist keine gültige Zahl.", price);
      MinecraftUtils.sendError(context.getSender(), message);
      return false;
    }
    String message =
        String.format("Der Preis des Grundstückes %s in der Welt %s wurde auf %s gesetzt.",
            gs.getName(), gs.getWorld().getName(), gs.getPrice());
    MinecraftUtils.sendInfo(context.getSender(), message);
    return true;
  }

  @Override
  protected void addAdditionalParams(ParameterList pl) {
    pl.add(new Parameter(PRICE, "preis"));
  }

  @Override
  protected List<String> tabCompleteAdditionalParams(TabCompleteContext context) {
    return new ArrayList<String>(0);
  }

}
