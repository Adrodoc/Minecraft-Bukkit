package de.adrodoc55.minecraft.plugins.terrania.gs.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import de.adrodoc55.common.collections.Closure;
import de.adrodoc55.common.collections.CollectionUtils;
import de.adrodoc55.minecraft.plugins.common.command.CommandContext;
import de.adrodoc55.minecraft.plugins.common.command.CommandException;
import de.adrodoc55.minecraft.plugins.common.command.Parameter;
import de.adrodoc55.minecraft.plugins.common.command.ParameterList;
import de.adrodoc55.minecraft.plugins.common.command.TabCompleteContext;
import de.adrodoc55.minecraft.plugins.common.utils.MinecraftUtils;
import de.adrodoc55.minecraft.plugins.terrania.gs.Grundstueck;
import de.adrodoc55.minecraft.plugins.terrania.gs.GsManager;

public abstract class ConcreteGsCommand extends GsCommand {

  private static final String GS = "gs";
  private static final String WORLD = "world";

  protected ConcreteGsCommand(String name) {
    super(name);
  }

  @Override
  protected final ParameterList getParamDefinition() {
    ParameterList pl = new ParameterList();
    pl.add(new Parameter(GS, "grundstück"));
    pl.add(new Parameter(WORLD, true));
    addAdditionalParams(pl);
    return pl;
  }

  @Override
  protected final boolean execute(CommandContext context) throws CommandException {
    String gsName = context.get(GS);
    String worldName = context.get(WORLD);
    World world;
    if (worldName != null) {
      world = Bukkit.getWorld(worldName);
      if (world == null) {
        String message = String.format("Die Welt %s konnte nicht gefunden werden.", worldName);
        MinecraftUtils.sendError(context.getSender(), message);
        return false;
      }
    } else {
      if (context.getSender() instanceof Player) {
        Player player = (Player) context.getSender();
        world = player.getWorld();
      } else {
        String message =
            "Du musst eine Welt angeben um diesen Befehl aus der Konsole benutzen zu können.";
        MinecraftUtils.sendError(context.getSender(), message);
        return false;
      }
    }
    Grundstueck gs = GsManager.getGSManager(world).find(gsName);
    if (gs == null) {
      String message =
          String.format("Das Grundstück %s konnte nicht in der Welt %s gefunden werden.", gsName,
              world.getName());
      MinecraftUtils.sendError(context.getSender(), message);
      context.setUsage("");
      return false;
    }
    return execute(context, gs);
  }

  protected abstract boolean execute(CommandContext context, Grundstueck grundstueck)
      throws CommandException;

  protected abstract void addAdditionalParams(ParameterList parameterList);

  @Override
  protected List<String> tabComplete(TabCompleteContext context) {
    String key = context.getKeyToComplete();
    if (GS.equals(key)) {
      if (context.getSender() instanceof Player) {
        Player player = (Player) context.getSender();
        GsManager gsm = GsManager.getGSManager(player.getWorld());
        Set<Grundstueck> grundstuecke = gsm.getGrundstuecke();
        final List<String> gsNames =
            CollectionUtils.collect(grundstuecke, new Closure<Grundstueck, String>() {
              @Override
              public String call(Grundstueck grundstueck) {
                return grundstueck.getName();
              }
            });
        return gsNames;
      }
    } else if (WORLD.equals(key)) {
      List<World> worlds = Bukkit.getWorlds();
      List<String> worldNames = CollectionUtils.collect(worlds, new Closure<World, String>() {
        @Override
        public String call(World world) {
          return world.getName();
        }
      });
      return worldNames;
    }
    // else if (completeArgs.length == 1) {
    // List<World> worlds = Bukkit.getWorlds();
    // List<String> worldNames = CollectionUtils.collect(worlds,
    // new Closure<World, String>() {
    // @Override
    // public String call(World world) {
    // return world.getName();
    // }
    // });
    // return worldNames;
    // }
    else {
      return tabCompleteAdditionalParams(context);
    }
    return new ArrayList<String>(0);
  }

  protected abstract List<String> tabCompleteAdditionalParams(TabCompleteContext context);
}
