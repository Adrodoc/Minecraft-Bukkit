package de.adrodoc55.minecraft.plugins.terrania.lock.commands;

import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import de.adrodoc55.minecraft.plugins.common.command.CommandContext;
import de.adrodoc55.minecraft.plugins.common.utils.MinecraftUtils;
import de.adrodoc55.minecraft.plugins.magic_protection.protection.ProtectionManager;

public class LockCommandHandler extends AbstractLockCommand {

  public static final String COMMAND = "lock";

  public LockCommandHandler() {
    super(COMMAND);
  }

  @Override
  public boolean execute(CommandContext context, Player player, Block block) {
    OfflinePlayer blockProtector = ProtectionManager.getBlockProtector(block);
    if (blockProtector != null) {
      String message = "Dieser Block wird bereits beschützt.";
      MinecraftUtils.sendError(player, message);
      return false;
    }
    if (ProtectionManager.setBlockProtector(block, player)) {
      String message = "Der Block ist jetzt abgeschlossen.";
      MinecraftUtils.sendInfo(player, message);
      return true;
    } else {
      String message = "Dieser Block kann nicht beschützt werden.";
      MinecraftUtils.sendError(player, message);
      return false;
    }
  }

}
