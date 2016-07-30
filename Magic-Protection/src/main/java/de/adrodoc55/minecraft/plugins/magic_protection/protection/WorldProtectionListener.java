package de.adrodoc55.minecraft.plugins.magic_protection.protection;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.event.world.WorldUnloadEvent;

public class WorldProtectionListener implements Listener {

  @EventHandler(priority = EventPriority.MONITOR)
  public void chunkLoaded(ChunkLoadEvent e) {
    ProtectionManager protectionManager = ProtectionManager.getProtectionManager(e.getWorld());
    protectionManager.loadChunkProtection(e.getChunk());
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void chunkUnloaded(ChunkUnloadEvent e) {
    if (e.isCancelled()) {
      return;
    }
    ProtectionManager protectionManager = ProtectionManager.getProtectionManager(e.getWorld());
    protectionManager.unloadChunk(e.getChunk());
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void worldSaved(WorldSaveEvent e) {
    ProtectionManager protectionManager = ProtectionManager.getProtectionManager(e.getWorld());
    protectionManager.save();
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void worldUnloaded(WorldUnloadEvent e) {
    if (e.isCancelled()) {
      return;
    }
    ProtectionManager protectionManager = ProtectionManager.getProtectionManager(e.getWorld());
    protectionManager.unload();
  }

}
