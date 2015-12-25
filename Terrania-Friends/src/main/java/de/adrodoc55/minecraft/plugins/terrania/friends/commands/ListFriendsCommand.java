package de.adrodoc55.minecraft.plugins.terrania.friends.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import de.adrodoc55.common.CommonUtils;
import de.adrodoc55.common.collections.Closure;
import de.adrodoc55.common.collections.CollectionUtils;
import de.adrodoc55.minecraft.plugins.common.command.CommandContext;
import de.adrodoc55.minecraft.plugins.common.command.Parameter;
import de.adrodoc55.minecraft.plugins.common.command.ParameterList;
import de.adrodoc55.minecraft.plugins.common.command.TabCompleteContext;
import de.adrodoc55.minecraft.plugins.common.utils.MinecraftUtils;
import de.adrodoc55.minecraft.plugins.terrania.friends.FriendsManager;

public class ListFriendsCommand extends FriendsCommand {

	private static final String PLAYER = "player";

	protected ListFriendsCommand() {
		super("list");
	}

	@Override
	protected ParameterList getParamDefinition() {
		ParameterList paramDef = new ParameterList();
		paramDef.add(new Parameter(PLAYER, true));
		return paramDef;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected boolean execute(CommandContext context) {
		String playerName = context.get(PLAYER);
		OfflinePlayer player;
		if (playerName != null) {
			player = Bukkit.getOfflinePlayer(playerName);
		} else {
			if (context.getSender() instanceof Player) {
				player = (Player) context.getSender();
			} else {
				String message = "Du musst einen Spieler angeben um diesen Befehl aus der Konsole benutzen zu können.";
				MinecraftUtils.sendError(context.getSender(), message);
				return false;
			}
		}

		Set<OfflinePlayer> friends = FriendsManager.getFriends(player);
		StringBuilder sb = new StringBuilder();
		sb.append("Freunde:\n");
		if (friends.isEmpty()) {
			sb.append(" " + ChatColor.GOLD + player.getName()
					+ " hat keine Freunde.\n");
		} else {
			List<String> names = CollectionUtils.collect(friends,
					new Closure<OfflinePlayer, String>() {
						@Override
						public String call(OfflinePlayer friend) {
							return " - " + friend.getName();
						}
					});
			sb.append(CommonUtils.join("\n", names));
			sb.append("\n");
		}
		String message = sb.toString();
		MinecraftUtils.sendInfo(context.getSender(), message);
		return true;
	}

	@Override
	protected List<String> tabComplete(TabCompleteContext context) {
		ArrayList<OfflinePlayer> offlinePlayers = CollectionUtils
				.newArrayList(Bukkit.getOfflinePlayers());
		List<String> playerNames = CollectionUtils.collect(offlinePlayers,
				new Closure<OfflinePlayer, String>() {
					@Override
					public String call(OfflinePlayer player) {
						return player.getName();
					}
				});
		return playerNames;
	}

}
