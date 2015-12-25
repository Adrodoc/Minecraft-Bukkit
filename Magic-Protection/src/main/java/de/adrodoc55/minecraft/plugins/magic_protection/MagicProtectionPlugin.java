package de.adrodoc55.minecraft.plugins.magic_protection;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import de.adrodoc55.minecraft.plugins.common.Logger;
import de.adrodoc55.minecraft.plugins.magic_protection.commands.RangCommandExecutor;
import de.adrodoc55.minecraft.plugins.magic_protection.magic.MagicCraftingManager;
import de.adrodoc55.minecraft.plugins.magic_protection.magic.MagicProtectionListener;
import de.adrodoc55.minecraft.plugins.magic_protection.protection.ProtectionListener;
import de.adrodoc55.minecraft.plugins.magic_protection.protection.WorldProtectionListener;

public class MagicProtectionPlugin extends JavaPlugin {

	private static Logger LOGGER;

	public static Logger logger() {
		return LOGGER;
	}

	private static MagicProtectionPlugin INSTANCE;

	public static MagicProtectionPlugin instance() {
		return INSTANCE;
	}

	public MagicProtectionPlugin() {
		LOGGER = Logger.getLogger(this);
		INSTANCE = this;
	}

	@Override
	public void onEnable() {
		PluginManager pluginManager = getServer().getPluginManager();
		getCommand(RangCommandExecutor.COMMAND).setExecutor(
				new RangCommandExecutor());
		pluginManager.registerEvents(new ProtectionListener(), this);
		pluginManager.registerEvents(new WorldProtectionListener(), this);
		pluginManager.registerEvents(new MagicProtectionListener(), this);
		MagicCraftingManager.addServerRecipes(getServer());

		// // Instantiate the ProtectionManagers
		// for (World world : getServer().getWorlds()) {
		// ProtectionManager.getProtectionManager(world);
		// }
	}

	// @Override
	// public void onDisable() {
	// ProtectionManager.saveAll();
	// }

}
