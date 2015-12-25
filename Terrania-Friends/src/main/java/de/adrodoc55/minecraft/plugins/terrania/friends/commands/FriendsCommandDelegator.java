package de.adrodoc55.minecraft.plugins.terrania.friends.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import de.adrodoc55.common.CommonUtils;
import de.adrodoc55.common.collections.Closure;
import de.adrodoc55.common.collections.CollectionUtils;
import de.adrodoc55.minecraft.plugins.common.command.CommandHandler;
import de.adrodoc55.minecraft.plugins.common.utils.CommandUtils;

public class FriendsCommandDelegator implements CommandHandler {

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		List<FriendsCommand> commands = FriendsCommand.getCommands();
		List<String> commandNames = CollectionUtils.collect(commands,
				new Closure<FriendsCommand, String>() {
					@Override
					public String call(FriendsCommand friendsCommand) {
						return friendsCommand.getName();
					}
				});
		String usage = ChatColor.RED + "/" + FriendsCommand.COMMAND + " <"
				+ CommonUtils.join(" | ", commandNames) + ">";
		command.setUsage(usage);
		if (args.length < 1) {
			return false;
		}
		FriendsCommand subCommand = FriendsCommand.getInstance(args[0]);
		if (subCommand == null) {
			return false;
		}
		return subCommand.onCommand(sender, command, label, args);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command,
			String alias, String[] args) {
		List<FriendsCommand> subCommands = FriendsCommand.getCommands();
		List<String> subCommandNames = CollectionUtils.collect(subCommands,
				new Closure<FriendsCommand, String>() {
					@Override
					public String call(FriendsCommand c) {
						return c.getName();
					}
				});
		if (args.length == 0) {
			return new ArrayList<String>(0);
		} else if (args.length == 1) {
			String toComplete = args[0];
			return CommandUtils.elementsStartsWith(subCommandNames, toComplete);
		} else {
			FriendsCommand friendsCommand = FriendsCommand.getInstance(args[0]);
			if (friendsCommand == null) {
				return new ArrayList<String>(0);
			}
			return friendsCommand.onTabComplete(sender, command, alias, args);
		}
	}

}
