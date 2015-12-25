package de.adrodoc55.minecraft.plugins.common;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Logger {

	private final JavaPlugin plugin;

	private static final Map<JavaPlugin, Logger> CACHE = new HashMap<JavaPlugin, Logger>();

	public static Logger getLogger(JavaPlugin plugin) {
		Logger logger = CACHE.get(plugin);
		if (logger == null) {
			logger = new Logger(plugin);
			CACHE.put(plugin, logger);
		}
		return logger;
	}

	private Logger(JavaPlugin plugin) {
		this.plugin = plugin;
	}

	private String getMessage(Object message) {
		return "[" + plugin.getName() + "] " + String.valueOf(message);
	}

	public void fine(Object message) {
		Bukkit.getLogger().fine(getMessage(message));
	}

	public void info(Object message) {
		Bukkit.getLogger().info(getMessage(message));
	}

	public void warn(Object message) {
		Bukkit.getLogger().warning(
				(char) 27 + "[33m" + getMessage(message) + (char) 27 + "[0m");
	}

	public void error(Object message) {
		Bukkit.getLogger().severe(
				(char) 27 + "[31m" + getMessage(message) + (char) 27 + "[0m");
	}

}
