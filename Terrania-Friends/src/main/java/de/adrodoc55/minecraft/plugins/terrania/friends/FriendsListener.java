package de.adrodoc55.minecraft.plugins.terrania.friends;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.event.world.WorldUnloadEvent;

public class FriendsListener implements Listener {

  @EventHandler(priority = EventPriority.MONITOR)
  public void worldSaved(WorldSaveEvent e) {
    FriendsManager.saveAll();
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void worldUnloaded(WorldUnloadEvent e) {
    if (e.isCancelled()) {
      return;
    }
    FriendsManager.saveAll();
  }

}
