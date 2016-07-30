package de.adrodoc55.minecraft.plugins.common.utils;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class WorldguardUtils {

  /**
   * Durchsucht alle Welten nach dieser Region und liefert die erste Welt zurüch, in der die Region
   * gefunden wurde. Ansonsten null.
   * 
   * @return the world of the region, if it can be found
   */
  public static World getWorldOfRegion(ProtectedRegion region) {
    WorldGuardPlugin worldGuardPlugin = JavaPlugin.getPlugin(WorldGuardPlugin.class);
    List<World> worlds = Bukkit.getServer().getWorlds();
    for (World world : worlds) {
      RegionManager regionManager = worldGuardPlugin.getRegionManager(world);
      if (regionManager.hasRegion(region.getId())) {
        return world;
      }
    }
    return null;
  }

}
