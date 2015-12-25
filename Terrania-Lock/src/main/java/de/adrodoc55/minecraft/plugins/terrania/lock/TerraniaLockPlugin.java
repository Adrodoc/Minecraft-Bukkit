package de.adrodoc55.minecraft.plugins.terrania.lock;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import de.adrodoc55.minecraft.plugins.common.Logger;
import de.adrodoc55.minecraft.plugins.terrania.lock.commands.LockCommandHandler;
import de.adrodoc55.minecraft.plugins.terrania.lock.commands.UnlockCommandHandler;

public class TerraniaLockPlugin extends JavaPlugin {

	private static Logger LOGGER;

	public static Logger logger() {
		return LOGGER;
	}

	private static TerraniaLockPlugin INSTANCE;

	public static TerraniaLockPlugin instance() {
		return INSTANCE;
	}

	public TerraniaLockPlugin() {
		LOGGER = Logger.getLogger(this);
		INSTANCE = this;
	}

	@Override
	public void onEnable() {
		PluginCommand lockCommand = getCommand(LockCommandHandler.COMMAND);
		LockCommandHandler lockHandler = new LockCommandHandler();
		lockCommand.setExecutor(lockHandler);
		lockCommand.setTabCompleter(lockHandler);

		PluginCommand unlockCommand = getCommand(UnlockCommandHandler.COMMAND);
		UnlockCommandHandler unlockHandler = new UnlockCommandHandler();
		unlockCommand.setExecutor(unlockHandler);
		unlockCommand.setTabCompleter(unlockHandler);
	}

}
