package de.adrodoc55.minecraft.plugins.terrania.friends.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import de.adrodoc55.common.collections.Closure;
import de.adrodoc55.common.collections.CollectionUtils;
import de.adrodoc55.minecraft.plugins.common.command.CommandContext;
import de.adrodoc55.minecraft.plugins.common.command.Parameter;
import de.adrodoc55.minecraft.plugins.common.command.ParameterList;
import de.adrodoc55.minecraft.plugins.common.command.TabCompleteContext;
import de.adrodoc55.minecraft.plugins.common.utils.MinecraftUtils;
import de.adrodoc55.minecraft.plugins.terrania.friends.FriendsManager;

public class AddFriendCommand extends FriendsCommand {

	private static final String PLAYER = "player";

	protected AddFriendCommand() {
		super("add");
	}

	@Override
	protected ParameterList getParamDefinition() {
		ParameterList paramDef = new ParameterList();
		paramDef.add(new Parameter(PLAYER));
		return paramDef;
	}

	@Override
	protected boolean execute(CommandContext context) {
		if (!(context.getSender() instanceof Player)) {
			String message = "Dieser Befehl kann nur von Spielern ausgeführt werden.";
			MinecraftUtils.sendError(context.getSender(), message);
			return false;
		}
		Player player = (Player) context.getSender();
		String playerName = context.get(PLAYER);

		@SuppressWarnings("deprecation")
		Player friend = Bukkit.getPlayer(playerName);
		if (friend == null) {
			String format = "Der Spieler %s konnte nicht gefunden werden.";
			String message = String.format(format, playerName);
			MinecraftUtils.sendError(player, message);
			return false;
		}

		FriendsManager.addFriend(player, friend);
		String playerFormat = "%s ist jetzt dein Freund.";
		String playerMessage = String.format(playerFormat, friend.getName());
		MinecraftUtils.sendInfo(player, playerMessage);
		String friendFormat = ChatColor.GREEN + "[Friends] " + ChatColor.YELLOW
				+ "%s hat dich als Freund hinzugefügt.";
		String friendMessage = String.format(friendFormat, friend.getName());
		MinecraftUtils.sendMessage(player, friendMessage);
		return true;
	}

	@Override
	protected List<String> tabComplete(TabCompleteContext context) {
		if (!(context.getSender() instanceof Player)) {
			return new ArrayList<String>(0);
		}
		String key = context.getKeyToComplete();
		if (!PLAYER.equals(key)) {
			return new ArrayList<String>(0);
		}

		Player player = (Player) context.getSender();
		ArrayList<? extends Player> onlinePlayers = CollectionUtils
				.newArrayList(Bukkit.getOnlinePlayers());
		onlinePlayers.remove(player);
		List<String> onlinePlayerNames = CollectionUtils.collect(onlinePlayers,
				new Closure<Player, String>() {
					@Override
					public String call(Player p) {
						return p.getName();
					}
				});
		return onlinePlayerNames;
	}

}
