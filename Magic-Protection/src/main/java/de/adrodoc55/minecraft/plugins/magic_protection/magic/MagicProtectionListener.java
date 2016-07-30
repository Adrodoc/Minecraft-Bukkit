package de.adrodoc55.minecraft.plugins.magic_protection.magic;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.darkblade12.particleeffect.ParticleEffect;

import de.adrodoc55.minecraft.plugins.common.utils.MinecraftUtils;
import de.adrodoc55.minecraft.plugins.magic_protection.protection.ProtectionManager;

public class MagicProtectionListener implements Listener {

  @EventHandler(priority = EventPriority.HIGHEST)
  public void blockClicked(PlayerInteractEvent e) {
    Action action = e.getAction();
    if (action == Action.LEFT_CLICK_BLOCK) {
      Player clicker = e.getPlayer();
      if (clicker.isSneaking()) {
        blockShiftLeftClicked_WithWand(e);
      } else {
        blockLeftClicked(e);
      }
    } else if (action == Action.RIGHT_CLICK_BLOCK) {
      blockRightClicked_WithWand(e);
    }
  }

  private void blockLeftClicked(PlayerInteractEvent e) {
    if (e.getAction() != Action.LEFT_CLICK_BLOCK) {
      return;
    }
    Block block = e.getClickedBlock();
    OfflinePlayer protector = ProtectionManager.getBlockProtector(block);
    if (protector != null) {
      Location particleLocation = MinecraftUtils
          .getCenteredBlockLocation(block, e.getBlockFace());
      ParticleEffect.SPELL_WITCH.display(0.1f, 0, 0.1f, 0, 100,
          particleLocation, e.getPlayer());
    }

  }

  private void blockShiftLeftClicked_WithWand(PlayerInteractEvent e) {
    if (e.getAction() != Action.LEFT_CLICK_BLOCK) {
      return;
    }
    Player clicker = e.getPlayer();
    ItemStack itemInHand = clicker.getItemOnCursor();
    boolean holdingWand = itemInHand.isSimilar(MagicCraftingManager
        .getNewNecroticWand())
        || itemInHand.isSimilar(MagicCraftingManager.getNewMagicWand())
        || itemInHand
            .isSimilar(MagicCraftingManager.getNewMasterWand());
    if (!holdingWand) {
      return;
    }
    Block block = e.getClickedBlock();
    OfflinePlayer protector = ProtectionManager.getBlockProtector(block);
    Location particleLocation = MinecraftUtils.getCenteredBlockLocation(
        block, e.getBlockFace());
    if (clicker.equals(protector)) {
      ProtectionManager.removeBlockProtection(block);
      ParticleEffect.CRIT_MAGIC.display(0.1f, 0, 0.1f, 0, 100,
          particleLocation, clicker);
      String message = ChatColor.AQUA
          + "Dieser Block wird nun nicht mehr von dir geschützt";
      MinecraftUtils.sendMessage(clicker, message);
      return;
    }
  }

  private static final double NECROTIC_COST = 1.0;
  private static final double MAGIC_COST = 0.1;
  private static final double MASTER_COST = 0;

  private void blockRightClicked_WithWand(PlayerInteractEvent e) {
    Action action = e.getAction();
    if (action != Action.RIGHT_CLICK_BLOCK)
      return;
    Player clicker = e.getPlayer();
    ItemStack itemInHand = clicker.getItemOnCursor();
    double protectionCost;
    if (itemInHand.isSimilar(MagicCraftingManager.getNewNecroticWand())) {
      protectionCost = NECROTIC_COST;
    } else if (itemInHand.isSimilar(MagicCraftingManager.getNewMagicWand())) {
      protectionCost = MAGIC_COST;
    } else if (itemInHand
        .isSimilar(MagicCraftingManager.getNewMasterWand())) {
      protectionCost = MASTER_COST;
    } else {
      return;
    }
    double experience = (double) clicker.getLevel()
        + (double) clicker.getExp();
    if (experience < protectionCost
        && clicker.getGameMode() != GameMode.CREATIVE) {
      String format = "Du benötigst mindestens %s Level um einen Block zu beschützen";
      String message = String.format(format, protectionCost);
      MinecraftUtils
          .sendMessage(clicker, ChatColor.DARK_PURPLE + message);
      return;
    }
    Block block = e.getClickedBlock();
    Location particleLocation = MinecraftUtils.getCenteredBlockLocation(
        block, e.getBlockFace());
    OfflinePlayer protector = ProtectionManager.getBlockProtector(block);
    if (protector != null) {
      ParticleEffect.VILLAGER_ANGRY.display(0, 0, 0, 0, 1,
          particleLocation, clicker);
      String message = "Dieser Block ist bereits geschützt";
      MinecraftUtils.sendError(clicker, message);
      return;
    }
    if (ProtectionManager.setBlockProtector(block, clicker)) {
      ParticleEffect.SPELL_WITCH.display(0.1f, 0, 0.1f, 0, 100,
          particleLocation, clicker);
      MinecraftUtils.setXp(clicker, experience - protectionCost);
      String message = ChatColor.DARK_PURPLE
          + "Dieser Block wird nun von dir geschützt";
      MinecraftUtils.sendMessage(clicker, message);
      return;
    } else {
      ParticleEffect.VILLAGER_ANGRY.display(0, 0, 0, 0, 1,
          particleLocation, clicker);
      String message = "Dieser Block kann nicht geschützt werden";
      MinecraftUtils.sendError(clicker, message);
      return;
    }
  }

}
