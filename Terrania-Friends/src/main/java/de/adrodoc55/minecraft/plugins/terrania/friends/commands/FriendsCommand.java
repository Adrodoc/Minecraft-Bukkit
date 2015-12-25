package de.adrodoc55.minecraft.plugins.terrania.friends.commands;

import java.util.ArrayList;
import java.util.List;

import de.adrodoc55.minecraft.plugins.common.command.AbstractCommandHandler;

public abstract class FriendsCommand extends AbstractCommandHandler {

	public static final AddFriendCommand ADD = new AddFriendCommand();
	public static final RemoveFriendCommand REMOVE = new RemoveFriendCommand();
	public static final ListFriendsCommand LIST = new ListFriendsCommand();

	public static List<FriendsCommand> getCommands() {
		List<FriendsCommand> commands = new ArrayList<FriendsCommand>();
		commands.add(ADD);
		commands.add(REMOVE);
		commands.add(LIST);
		return commands;
	}

	public static FriendsCommand getInstance(String subCommandName) {
		if (subCommandName == null) {
			return null;
		}
		for (FriendsCommand gsCommand : getCommands()) {
			if (subCommandName.equals(gsCommand.getName())) {
				return gsCommand;
			}
		}
		return null;
	}

	public static final String COMMAND = "friends";

	protected FriendsCommand(String name) {
		super(new String[] { COMMAND, name });
	}
}
