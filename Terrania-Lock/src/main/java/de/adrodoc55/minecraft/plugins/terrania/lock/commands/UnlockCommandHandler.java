package de.adrodoc55.minecraft.plugins.terrania.lock.commands;

import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import de.adrodoc55.minecraft.plugins.common.command.CommandContext;
import de.adrodoc55.minecraft.plugins.common.utils.MinecraftUtils;
import de.adrodoc55.minecraft.plugins.magic_protection.protection.ProtectionManager;

public class UnlockCommandHandler extends AbstractLockCommand {

	public static final String COMMAND = "unlock";

	public UnlockCommandHandler() {
		super(COMMAND);
	}

	@Override
	public boolean execute(CommandContext context, Player player, Block block) {
		OfflinePlayer blockProtector = ProtectionManager
				.getBlockProtector(block);
		if (!player.equals(blockProtector)) {
			String message = "Dieser Block wird nicht von dir beschützt.";
			MinecraftUtils.sendError(player, message);
			return false;
		}
		ProtectionManager.removeBlockProtection(block);
		String message = "Der Block ist jetzt nicht mehr abgeschlossen.";
		MinecraftUtils.sendInfo(player, message);
		return true;

	}

}
