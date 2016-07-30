package de.adrodoc55.minecraft.plugins.terrania.gs.commands;

import static de.adrodoc55.minecraft.plugins.terrania.gs.TerraniaGsPlugin.logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

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
import de.adrodoc55.minecraft.plugins.terrania.gs.ValidationException;

public class CreateGsCommand extends GsCommand {

  private static final String REGION = "region";
  private static final String WORLD = "world";

  protected CreateGsCommand() {
    super("create");
  }

  @Override
  protected boolean execute(CommandContext context) throws CommandException {
    Player player;
    try {
      player = (Player) context.getSender();
    } catch (ClassCastException ex) {
      throw new CommandException("Dieser Befehl kann nur von Spielern ausgeführt werden.", ex);
    }
    MinecraftUtils.checkPermission(player, "terrania.gs.commands.gs." + getName());

    String regionName = context.get(REGION);
    String worldName = context.get(WORLD);
    World world;
    if (worldName != null) {
      world = Bukkit.getWorld(worldName);
    } else {
      world = player.getWorld();
    }
    WorldGuardPlugin worldGuardPlugin = JavaPlugin.getPlugin(WorldGuardPlugin.class);
    ProtectedRegion region = worldGuardPlugin.getRegionManager(world).getRegion(regionName);
    if (region == null) {
      String message =
          String.format("Es konnte keine Region mit dem Namen '%s' in der Welt %s gefunden werden.",
              regionName, world.getName());
      MinecraftUtils.sendError(context.getSender(), message);
      return false;
    }
    // Selection selection = JavaPlugin.getPlugin(WorldEditPlugin.class)
    // .getSelection(player);
    // if (selection == null) {
    // String message =
    // "Du musst eine World Edit Selection haben um diesen Befehl aus zu führen.";
    // MinecraftUtils.sendError(player, message);
    // context.setUsage("");
    // return false;
    // }
    // String missingSignMessage =
    // "Du musst genau ein Schild ausgewählt habem um diesen Befehl auszuführen.";
    // if (selection.getArea() != 1) {
    // MinecraftUtils.sendError(player, missingSignMessage);
    // return false;
    // }
    // World signWorld = selection.getWorld();
    // Location signLocation = selection.getMaximumPoint();
    // Block signBlock = signWorld.getBlockAt(signLocation);
    // BlockState state = signBlock.getState();
    // if (!(state instanceof Sign)) {
    // MinecraftUtils.sendError(player, missingSignMessage);
    // return false;
    // }
    Block signBlock = player.getTargetBlock((Set<Material>) null, 10);
    World signWorld = signBlock.getWorld();
    BlockState state = signBlock.getState();
    if (!(state instanceof Sign)) {
      MinecraftUtils.sendError(player, "Du musst ein Schild ansehen um diesen Befehl auszuführen.");
      return false;
    }
    Sign sign = (Sign) state;
    if (!world.equals(signWorld)) {
      String message = "Die Region und das Schild müssen sich in der selben Welt befinden.";
      MinecraftUtils.sendError(player, message);
      context.setUsage("");
      return false;
    }
    Grundstueck grundstueck = new Grundstueck(world, regionName, sign);
    try {
      boolean didNotYetExist = GsManager.add(grundstueck);
      if (!didNotYetExist) {
        String message = String.format("Das Grundstück %s existiert bereits in der Welt %s.",
            grundstueck.getName(), world.getName());
        MinecraftUtils.sendError(player, message);
        context.setUsage("");
        return false;
      }
    } catch (ValidationException ex) {
      String message = ex.getMessage();
      logger().error(message);
      ex.printStackTrace();
      MinecraftUtils.sendError(context.getSender(), message);
      context.setUsage("");
      return false;
    }
    String message = String.format("Das Grundstück %s wurde erfolgreich in der Welt %s erstellt.",
        grundstueck.getName(), world.getName());
    MinecraftUtils.sendInfo(player, message);
    return true;
  }

  @Override
  protected ParameterList getParamDefinition() {
    ParameterList pl = new ParameterList();
    pl.add(new Parameter(REGION));
    pl.add(new Parameter(WORLD, true));
    return pl;
  }

  @Override
  protected List<String> tabComplete(TabCompleteContext context) {
    String key = context.getKeyToComplete();
    if (REGION.equals(key)) { // complete Region name
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
        RegionManager rm =
            JavaPlugin.getPlugin(WorldGuardPlugin.class).getRegionManager(player.getWorld());
        Set<String> allRegionNames = rm.getRegions().keySet();
        List<String> usableRegionNames =
            CollectionUtils.findAll(allRegionNames, new Closure<String, Boolean>() {
              @Override
              public Boolean call(String regionName) {
                return !gsNames.contains(regionName);
              }
            });
        return usableRegionNames;
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
    return new ArrayList<String>(0);
  }

}
