package de.adrodoc55.minecraft.plugins.magic_protection.player;

import static de.adrodoc55.minecraft.plugins.magic_protection.MagicProtectionPlugin.logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.bukkit.Achievement;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import de.adrodoc55.minecraft.plugins.common.PluginException;
import de.adrodoc55.minecraft.plugins.magic_protection.MagicProtectionPlugin;

public class PlayerManager {

	private static File getPlayersDir() {
		File playersDir = new File(MagicProtectionPlugin.instance()
				.getDataFolder(), "players");
		playersDir.mkdirs();
		return playersDir;
	}

	private static final String RANG = "rang";
	private static final Map<UUID, Properties> CACHE = new HashMap<UUID, Properties>();

	private static Properties getPlayerProperties(OfflinePlayer player) {
		Properties props = CACHE.get(player.getUniqueId());
		if (props != null) {
			return props;
		}
		props = new Properties();
		File propsFile = getPlayerPropertiesFile(player);
		try {
			BufferedReader reader = new BufferedReader(
					new FileReader(propsFile));
			props.load(reader);
		} catch (IOException ex) {
			String message = String.format(
					"IOException beim Laden der Properties des Spielers '%s'",
					player.getName());
			throw new PluginException(502, message, ex);
		}
		CACHE.put(player.getUniqueId(), props);
		return props;
	}

	private static void setPlayerProperty(OfflinePlayer player, String key,
			String value) {
		File propsFile = getPlayerPropertiesFile(player);
		try {
			Properties props = getPlayerProperties(player);
			props.put(key, value);
			BufferedWriter writer = new BufferedWriter(
					new FileWriter(propsFile));
			String comments = String
					.format("Properties für den Spieler '%s' für das Plugin MagicProtectionPlugin",
							player.getName());
			props.store(writer, comments);
		} catch (IOException e) {
			String message = String.format(
					"IOException beim Setzen der Properties des Spielers '%s'",
					player.getName());
			throw new PluginException(503, message, e);
		}
	}

	private static String getPlayerProperty(OfflinePlayer player, String key) {
		Properties props = getPlayerProperties(player);
		Object value = props.get(key);
		if (value == null) {
			return null;
		}
		return String.valueOf(value);
	}

	private static File getPlayerPropertiesFile(OfflinePlayer player) {
		File file = new File(getPlayersDir(), player.getUniqueId().toString()
				.concat(".properties"));
		try {
			file.createNewFile();
		} catch (IOException ex) {
			String message = String
					.format("IOException beim Erstellen der Properties-Datei des Spielers '%s'",
							player.getName());
			throw new PluginException(504, message, ex);
		}
		return file;
	}

	/**
	 * Gibt die Anzahl der Achievments zurück, die der Spieler bereits erreicht
	 * hat. Falls der Spieler null ist, wird 0 zurückgegeben.
	 *
	 * @param player
	 *            Nullable
	 * @return
	 */
	public static int getPlayerAchievementCount(OfflinePlayer player) {
		if (player == null) {
			return 0;
		}
		Player onlinePlayer = player.getPlayer();
		if (onlinePlayer == null) {
			return 0;
		}
		int count = 0;
		for (Achievement a : Achievement.values()) {
			if (onlinePlayer.hasAchievement(a)) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Gibt den effektiven Rang des Spielers zurück. Falls der Spieler null ist,
	 * wird 0 zurückgegeben.
	 *
	 * @param player
	 *            Nullable
	 * @return Rang des Spielers
	 */
	public static int getPlayerRanking(OfflinePlayer player) {
		if (player == null) {
			return 0;
		}
		int achievementCount = getPlayerAchievementCount(player);
		String rang = getPlayerProperty(player, RANG);
		if (rang == null) {
			String message = String.format(
					"Setzte Rang des neuen Spielers '%s' auf "
							+ achievementCount, player.getName());
			logger().info(message);
			setPlayerProperty(player, RANG, String.valueOf(achievementCount));
			return achievementCount;
		}
		int savedRank = 0;
		try {
			savedRank = Integer.parseInt(rang);
		} catch (RuntimeException ex) {
			String message = String
					.format("Problem beim Laden des Ranges vom Spieler '%s': Der Wert '%s' ist keine Zahl. Setzte Rang auf "
							+ achievementCount, player.getName(), rang);
			logger().warn(message);
			setPlayerProperty(player, RANG, String.valueOf(achievementCount));
			return achievementCount;
		}
		if (savedRank < achievementCount) {
			String message = String.format(
					"Aktualisiere den Rang des Spielers '%s' auf %d",
					player.getName(), achievementCount);
			logger().info(message);
			setPlayerProperty(player, RANG, String.valueOf(achievementCount));
			return achievementCount;
		} else {
			return savedRank;
		}
	}

	@Deprecated
	/**
	 * Der Rank entspricht der Achievements Anzahl und sollte daher nicht gesetzt werden.
	 *
	 * @param player
	 * @param rank
	 */
	public static void setPlayerRanking(OfflinePlayer player, int rang) {
		if (player == null) {
			throw new IllegalArgumentException("player must not be null");
		}
		setPlayerProperty(player, RANG, String.valueOf(rang));
	}

	/**
	 * Gibt zurück, ob der Spieler etwas entfernen/zerstören kann, was vom
	 * Protector beschützt wird.
	 *
	 * @param player
	 *            Nullable
	 * @param protector
	 *            Nullable
	 * @return
	 */
	public static boolean canRemove(OfflinePlayer player,
			OfflinePlayer protector) {
		int playerRanking = getPlayerRanking(player);
		int protectorRanking = getPlayerRanking(protector);
		return protectorRanking <= playerRanking;
	}

	/**
	 * Gibt zurück, ob der Spieler etwas öffnen kann, was vom Protector
	 * beschützt wird.
	 *
	 * @param player
	 *            Nullable
	 * @param protector
	 *            Nullable
	 * @return
	 */
	public static boolean canOpen(OfflinePlayer player, OfflinePlayer protector) {
		if (player == null) {
			return false;
		}
		if (protector == null) {
			return true;
		}
		return player.equals(protector);
	}

}
