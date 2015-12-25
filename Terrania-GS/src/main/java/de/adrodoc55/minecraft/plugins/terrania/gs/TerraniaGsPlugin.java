package de.adrodoc55.minecraft.plugins.terrania.gs;

import org.bukkit.World;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import de.adrodoc55.minecraft.plugins.common.Logger;
import de.adrodoc55.minecraft.plugins.terrania.gs.commands.GsCommand;
import de.adrodoc55.minecraft.plugins.terrania.gs.commands.GsCommandDelegator;

public class TerraniaGsPlugin extends JavaPlugin {

	private static Logger LOGGER;

	public static Logger logger() {
		return LOGGER;
	}

	private static TerraniaGsPlugin INSTANCE;

	public static TerraniaGsPlugin instance() {
		return INSTANCE;
	}

	public TerraniaGsPlugin() {
		LOGGER = Logger.getLogger(this);
		INSTANCE = this;
	}

	@Override
	public void onEnable() {
		PluginCommand gs = getCommand(GsCommand.COMMAND);
		GsCommandDelegator gsCommand = new GsCommandDelegator();
		gs.setExecutor(gsCommand);
		gs.setTabCompleter(gsCommand);

		PluginManager pluginManager = getServer().getPluginManager();
		pluginManager.registerEvents(new GsListener(), this);

		// Instantiate the GSManagers
		for (World world : getServer().getWorlds()) {
			GsManager.getGSManager(world);
		}
	}

	@Override
	public void onDisable() {
		GsManager.saveAll();
	}

}
