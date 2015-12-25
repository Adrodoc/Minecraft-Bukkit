package de.adrodoc55.minecraft.plugins.terrania.gs;

//import java.time.LocalDate;
//import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

import javax.xml.bind.annotation.XmlRootElement;

import me.leepreechaun.terraniacore.economy.EconomyManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.joda.time.Days;
import org.joda.time.LocalDate;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import de.adrodoc55.common.CommonUtils;
import de.adrodoc55.minecraft.plugins.common.utils.MinecraftUtils;
import de.adrodoc55.minecraft.plugins.terrania.gs.xml.XmlGs;
import de.adrodoc55.minecraft.plugins.terrania.gs.xml.XmlSign;

@XmlRootElement
public class Grundstueck {

	private static final long MAX_RENT_DAYS = 30;
	private final World world;
	private final String name;
	private final Block sign;
	private UUID owner;
	private double price;
	private LocalDate expiration;

	public Grundstueck(World world, String name, Sign sign) {
		this.world = world;
		this.name = name;
		this.sign = sign.getBlock();
		expiration = LocalDate.now().minusDays(1);
		updateSignConent();
	}

	public Grundstueck(World world, XmlGs gs) {
		this.world = world;
		name = gs.getName();
		String owner = gs.getOwner();
		if (owner == null) {
			this.owner = null;
		} else {
			this.owner = UUID.fromString(owner);
		}
		price = gs.getPrice();
		// TODO Java 8:
		// expiration = LocalDate.ofEpochDay(gs.getExpiration());
		// Java 7:
		expiration = LocalDate.fromDateFields(new Date(0)).plusDays(
				(int) gs.getExpiration());
		XmlSign xmlSign = gs.getSign();
		int signX = xmlSign.getX();
		int signY = xmlSign.getY();
		int signZ = xmlSign.getZ();
		Block block = world.getBlockAt(signX, signY, signZ);
		sign = block;
		updateSignConent();
	}

	public void validate() throws ValidationException {
		if (world == null) {
			throw new ValidationException(
					"Invalides Grundst�ck: world ist null");
		}
		if (name == null) {
			throw new ValidationException("Invalides Grundst�ck: name ist null");
		}
		if (sign == null) {
			throw new ValidationException("Invalides Grundst�ck: sign ist null");
		}
		if (expiration == null) {
			throw new ValidationException(
					"Invalides Grundst�ck: expiration ist null");
		}
		if (getRegion() == null) {
			throw new ValidationException(
					"Invalides Grundst�ck: region existiert nicht");
		}
		if (!world.equals(sign.getWorld())) {
			throw new ValidationException(
					"Invalides Grundst�ck: sign existiert nicht in dieser Welt");
		}
		if (getSign() == null) {
			throw new ValidationException(
					"Invalides Grundst�ck: sign existiert nicht");
		}
	}

	public World getWorld() {
		return world;
	}

	public ProtectedRegion getRegion() {
		// TODO: GS k�nnte invalid sein
		return JavaPlugin.getPlugin(WorldGuardPlugin.class)
				.getRegionManager(world).getRegion(name);
	}

	public String getName() {
		return name;
	}

	public Sign getSign() {
		// TODO: GS k�nnte invalid sein
		BlockState state = sign.getState();
		if (state instanceof Sign) {
			return (Sign) state;
		}
		return null;
	}

	public OfflinePlayer getOwner() {
		if (owner == null) {
			return null;
		} else {
			return Bukkit.getOfflinePlayer(owner);
		}
	}

	public void setOwner(OfflinePlayer newOwner) {
		DefaultDomain owners = getRegion().getOwners();
		if (owner != null) {
			owners.removePlayer(owner);
		}
		owners.addPlayer(newOwner.getUniqueId());
		owner = newOwner.getUniqueId();
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	/**
	 * Gibt den Ablauf-Tag der Grundst�ck Miete zur�ck. Niemals null.
	 * 
	 * @return Date of Expiration
	 */
	public LocalDate getExpiration() {
		return expiration;
	}

	// public void setExpiration(LocalDate expiration) {
	// this.expiration = expiration;
	// }

	public boolean isRented() {
		return expiration.isAfter(LocalDate.now());
	}

	public void updateSignConent() {
		String[] signContent = getSignContent();
		Sign sign = getSign();
		for (int x = 0; x < signContent.length; x++) {
			sign.setLine(x, signContent[x]);
		}
		sign.update();
	}

	private String[] getSignContent() {
		String[] content = new String[4];
		content[1] = name;
		if (isRented()) {
			content[0] = "[Vermietet]";
			content[2] = getOwner().getName();
			content[3] = "Noch " + getDaysLeft() + " Tage";
		} else {
			content[0] = "[Zu vermieten]";
			content[2] = "Preis: " + CommonUtils.doubleToString(price);
			content[3] = "";
		}
		return content;
	}

	public long getDaysLeft() {
		// TODO Java 8:
		// long timeLeft = ChronoUnit.DAYS.between(LocalDate.now(), expiration);
		// Java 7:
		long timeLeft = Days.daysBetween(LocalDate.now(), expiration).getDays();
		return timeLeft;
	}

	public void mieten(OfflinePlayer player) {
		if (isRented()) {
			throw new IllegalStateException(
					"Dieses Grundst�ck ist bereits vermietet.");
		}
		boolean paid = EconomyManager.payMoney(player, price);
		Player onlinePlayer = player.getPlayer();
		if (paid) {
			setOwner(player);
			expiration = LocalDate.now().plusDays(1);
			updateSignConent();
			String message = ChatColor.YELLOW
					+ "Du hast dieses Grundst�ck erfolgreich f�r einen Tag gemietet.";
			MinecraftUtils.sendMessage(onlinePlayer, message);
		} else {
			String message = ChatColor.RED
					+ "Du hast nicht genug Geld um dieses Grundst�ck zu mieten.";
			MinecraftUtils.sendMessage(onlinePlayer, message);
		}
	}

	public void mieteVerlaengern(long anzahlTage) {
		if (!isRented()) {
			throw new IllegalArgumentException(
					"Ein abgelaufenes Grundst�ck kann nicht verl�ngert werden.");
		}
		long daysLeft = getDaysLeft();
		if (daysLeft + anzahlTage > MAX_RENT_DAYS) {
			Player player = getOwner().getPlayer();
			String message = ChatColor.RED
					+ String.format(
							"Ein Grundst�ck kann f�r maximal %s Tage gemietet werden.",
							MAX_RENT_DAYS);
			MinecraftUtils.sendMessage(player, message);
			return;
		}
		boolean paid = EconomyManager.payMoney(getOwner(), price);
		Player onlinePlayer = getOwner().getPlayer();
		if (paid) {
			// TODO Java 8:
			// expiration = expiration.plusDays(anzahlTage);
			// Java 7:
			expiration = expiration.plusDays((int) anzahlTage);

			updateSignConent();
			String message = ChatColor.YELLOW
					+ String.format(
							"Deine Miete wurde verl�ngert. Dir bleiben noch %d Tage",
							getDaysLeft());
			MinecraftUtils.sendMessage(onlinePlayer, message);
		} else {
			String message = ChatColor.RED
					+ "Du hast nicht genug Geld um die Miete zu verl�ngern.";
			MinecraftUtils.sendMessage(onlinePlayer, message);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((world == null) ? 0 : world.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Grundstueck other = (Grundstueck) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (world == null) {
			if (other.world != null)
				return false;
		} else if (!world.equals(other.world))
			return false;
		return true;
	}

}
