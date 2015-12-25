package de.adrodoc55.minecraft.plugins.terrania.friends;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import de.adrodoc55.minecraft.plugins.common.Logger;
import de.adrodoc55.minecraft.plugins.terrania.friends.commands.FriendsCommand;
import de.adrodoc55.minecraft.plugins.terrania.friends.commands.FriendsCommandDelegator;

public class TerraniaFriendsPlugin extends JavaPlugin {

	private static Logger LOGGER;

	public static Logger logger() {
		return LOGGER;
	}

	private static TerraniaFriendsPlugin INSTANCE;

	public static TerraniaFriendsPlugin instance() {
		return INSTANCE;
	}

	public TerraniaFriendsPlugin() {
		LOGGER = Logger.getLogger(this);
		INSTANCE = this;
	}

	@Override
	public void onEnable() {
		PluginCommand gs = getCommand(FriendsCommand.COMMAND);
		FriendsCommandDelegator gsCommand = new FriendsCommandDelegator();
		gs.setExecutor(gsCommand);
		gs.setTabCompleter(gsCommand);

		// PluginManager pluginManager = getServer().getPluginManager();
		// pluginManager.registerEvents(new GsListener(), this);

		// Instantiate the GSManagers
		// for (World world : getServer().getWorlds()) {
		// GsManager.getGSManager(world);
		// }
	}

	@Override
	public void onDisable() {
		FriendsManager.saveAll();
	}

}
