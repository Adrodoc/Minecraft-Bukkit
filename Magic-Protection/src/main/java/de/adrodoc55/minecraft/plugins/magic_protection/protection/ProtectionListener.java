package de.adrodoc55.minecraft.plugins.magic_protection.protection;

import static de.adrodoc55.minecraft.plugins.magic_protection.MagicProtectionPlugin.logger;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;

import de.adrodoc55.minecraft.plugins.common.utils.MaterialUtils;
import de.adrodoc55.minecraft.plugins.common.utils.MinecraftUtils;
import de.adrodoc55.minecraft.plugins.magic_protection.player.PlayerManager;

public class ProtectionListener implements Listener {

  @EventHandler(priority = EventPriority.HIGHEST)
  public void blockPlaced(BlockPlaceEvent e) {
    if (e.isCancelled())
      return;
    Block block = e.getBlock();
    // Delete previous Block Protection
    ProtectionManager.removeBlockProtection(block);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void leavesDecayed(LeavesDecayEvent e) {
    if (e.isCancelled())
      return;
    Block block = e.getBlock();
    ProtectionManager.removeBlockProtection(block);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void blockBroken(BlockBreakEvent e) {
    if (e.isCancelled())
      return;
    Block block = e.getBlock();
    Player player = e.getPlayer();
    blockAttacked(block, player, e, true);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void blockDamaged(BlockDamageEvent e) {
    if (e.isCancelled())
      return;
    Block block = e.getBlock();
    Player player = e.getPlayer();
    if (e.getInstaBreak()) {
      blockAttacked(block, player, e, true);
    } else {
      blockAttacked(block, player, e, false);
    }
  }

  private void blockAttacked(Block block, Player player, Cancellable e, boolean deleteProtection) {
    OfflinePlayer protector = ProtectionManager.getBlockProtector(block);
    if (protector == null)
      return;
    if (PlayerManager.canRemove(player, protector)) {
      if (deleteProtection) {
        ProtectionManager.removeBlockProtection(block);
        if (!player.equals(protector.getPlayer())) {
          String format = "Der höherrangige Spieler %s hat von %s den Block (%s) zerstört!";
          String message = String.format(format, player.getName(), protector.getName(), block);
          logger().warn(message);
        }
      }
      return;
    }
    String format = "Dieser Block wird von %s beschützt";
    String message = String.format(format, protector.getName());
    MinecraftUtils.sendError(player, message);
    e.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void blockExploded(EntityExplodeEvent e) {
    if (e.isCancelled())
      return;
    List<Block> explodingBlocks = e.blockList();
    List<Block> protectedBlocks = new ArrayList<Block>();
    for (Block block : explodingBlocks) {
      OfflinePlayer protector = ProtectionManager.getBlockProtector(block);
      if (protector != null)
        protectedBlocks.add(block);
    }
    explodingBlocks.removeAll(protectedBlocks);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void blockFromTo(BlockFromToEvent e) {
    if (e.isCancelled())
      return;
    Block block = e.getToBlock();
    blockTriedToExpire(block, e);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void blockFaded(BlockFadeEvent e) {
    if (e.isCancelled())
      return;
    Block block = e.getBlock();
    blockTriedToExpire(block, e);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void blockBurned(BlockBurnEvent e) {
    if (e.isCancelled())
      return;
    Block block = e.getBlock();
    blockTriedToExpire(block, e);
  }

  private void blockTriedToExpire(Block block, Cancellable e) {
    OfflinePlayer protector = ProtectionManager.getBlockProtector(block);
    if (protector != null) {
      e.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void blockPushed(BlockPistonExtendEvent e) {
    if (e.isCancelled())
      return;
    pistonEvent(e.getBlocks(), e);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void blockRetracted(BlockPistonRetractEvent e) {
    if (e.isCancelled())
      return;
    pistonEvent(e.getBlocks(), e);
  }

  private void pistonEvent(List<Block> blocks, Cancellable e) {
    for (Block block : blocks) {
      OfflinePlayer protector = ProtectionManager.getBlockProtector(block);
      if (protector != null) {
        e.setCancelled(true);
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void chestPlaced(BlockPlaceEvent e) {
    if (e.isCancelled())
      return;
    Block block = e.getBlock();
    if (block.getType() != Material.CHEST && block.getType() != Material.TRAPPED_CHEST)
      return;
    Material chestType = block.getType();
    Player placer = e.getPlayer();
    BlockFace[] directions = {BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};
    for (BlockFace direction : directions) {
      Block relative = block.getRelative(direction);
      if (relative.getType() == chestType) {
        OfflinePlayer protector = ProtectionManager.getBlockProtector(relative);
        if (protector != null && !placer.equals(protector.getPlayer())) {
          String message = "Du kannst keine Kiste neben eine geschützte Kiste stellen";
          MinecraftUtils.sendError(placer, message);
          e.setCancelled(true);
          return;
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void doorClickedByPlayer(PlayerInteractEvent e) {
    if (e.isCancelled()) {
      return;
    }
    if (e.getAction() != Action.LEFT_CLICK_BLOCK) {
      return;
    }
    Block block = e.getClickedBlock();
    boolean isDoor = MaterialUtils.isDoor(block.getType())
        || MaterialUtils.isTrapDoor(block.getType()) || MaterialUtils.isFenceGate(block.getType());
    if (!isDoor) {
      return;
    }
    Player opener = e.getPlayer();
    OfflinePlayer protector = ProtectionManager.getBlockProtector(block);
    if (!PlayerManager.canOpen(opener, protector)) {
      String format = "Diese Tür ist von %s abgeschlossen";
      String message = String.format(format, protector.getName());
      MinecraftUtils.sendError(opener, message);
      e.setCancelled(true);
    }
  }

  // TODO: Redstone und Türen
  // @EventHandler(priority = EventPriority.HIGHEST)
  // public void doorClickedByRedstone(BlockRedstoneEvent e) {
  // if (e.isCancelled()) {
  // return;
  // }
  // if (e.getAction() != Action.LEFT_CLICK_BLOCK) {
  // return;
  // }
  // Block block = e.getClickedBlock();
  // boolean isDoor = MaterialUtils.isDoor(block.getType())
  // || MaterialUtils.isTrapDoor(block.getType())
  // || MaterialUtils.isFenceGate(block.getType());
  // if (!isDoor) {
  // return;
  // }
  // Player opener = e.getPlayer();
  // OfflinePlayer protector = ProtectionManager.getBlockProtector(block);
  // if (!PlayerManager.canOpen(opener, protector)) {
  // String format = "Diese Tür ist von %s abgeschlossen";
  // String message = String.format(format, protector.getName());
  // MinecraftUtils.sendError(opener, message);
  // e.setCancelled(true);
  // }
  // }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void inventoryOpened(InventoryOpenEvent e) {
    if (e.isCancelled()) {
      return;
    }
    Player opener = (Player) e.getPlayer();
    InventoryHolder inventoryHolder = e.getInventory().getHolder();
    OfflinePlayer protector;
    if (inventoryHolder instanceof DoubleChest) {
      DoubleChest chest = (DoubleChest) inventoryHolder;
      Block left = ((Chest) chest.getLeftSide()).getBlock();
      Block right = ((Chest) chest.getRightSide()).getBlock();
      OfflinePlayer leftProtector = ProtectionManager.getBlockProtector(left);
      OfflinePlayer rightProtector = ProtectionManager.getBlockProtector(right);
      if (leftProtector == null) {
        if (rightProtector == null) {
          protector = null;
        } else {
          protector = rightProtector;
          ProtectionManager.setBlockProtector(left, rightProtector);
        }
      } else {
        if (rightProtector == null) {
          protector = leftProtector;
          ProtectionManager.setBlockProtector(right, leftProtector);
        } else {
          protector = rightProtector;
          if (!rightProtector.equals(leftProtector)) {
            String format =
                "Es gibt zwei verschiedene Beschützer für die Doppel-Kiste '%s'. Daher wird der Rechte genommen";
            String message = String.format(format, left.toString());
            logger().warn(message);
          }
        }
      }
    } else if (inventoryHolder instanceof BlockState) {
      Block block = ((BlockState) inventoryHolder).getBlock();
      protector = ProtectionManager.getBlockProtector(block);
    } else
      return;
    if (protector == null)
      return;
    if (!PlayerManager.canOpen(opener, protector)) {
      String format = "Dieser Block wird von %s abgeschlossen";
      String message = String.format(format, protector.getName());
      MinecraftUtils.sendError(opener, message);
      e.setCancelled(true);
      return;
    }
  }

}
