package de.adrodoc55.minecraft.plugins.terrania.gs;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import de.adrodoc55.minecraft.plugins.common.utils.MinecraftUtils;

public class GsListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void worldSaved(WorldSaveEvent e) {
		GsManager.getGSManager(e.getWorld()).save();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void worldUnloaded(WorldUnloadEvent e) {
		if (e.isCancelled()) {
			return;
		}
		GsManager.getGSManager(e.getWorld()).save();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void grundstueckSignClicked(PlayerInteractEvent e) {
		if (e.isCancelled())
			return;
		Action action = e.getAction();
		if (action != Action.RIGHT_CLICK_BLOCK)
			return;
		Block block = e.getClickedBlock();
		BlockState state = block.getState();
		if (!(state instanceof Sign)) {
			return;
		}
		Sign sign = (Sign) state;
		Grundstueck grundstueck = GsManager.getGSManager(
				e.getClickedBlock().getWorld()).find(sign);
		if (grundstueck == null) {
			return;
		}
		Player clicker = e.getPlayer();
		if (!grundstueck.isRented()) {
			grundstueck.mieten(clicker);
		} else {
			if (!grundstueck.getOwner().equals(clicker)) {
				MinecraftUtils.sendMessage(clicker,
						"Dieses Grundstück ist bereits vermietet");
				return;
			}
			grundstueck.mieteVerlaengern(1);
		}
	}
}
