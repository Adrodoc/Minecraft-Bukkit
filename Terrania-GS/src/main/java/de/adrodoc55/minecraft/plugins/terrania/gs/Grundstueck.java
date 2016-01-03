package de.adrodoc55.minecraft.plugins.terrania.gs;

//import java.time.LocalDate;
//import java.time.temporal.ChronoUnit;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
        // Java 8:
        expiration = LocalDate.ofEpochDay(gs.getExpiration());
        // Java 7:
        // expiration = LocalDate.fromDateFields(new Date(0)).plusDays(
        // (int) gs.getExpiration());
        XmlSign xmlSign = gs.getSign();
        int signX = xmlSign.getX();
        int signY = xmlSign.getY();
        int signZ = xmlSign.getZ();
        Block block = world.getBlockAt(signX, signY, signZ);
        sign = block;
        updateSignConent();
    }

    /**
     * Invalidates this gs. This also clears the sign content.
     *
     * @since 0.8
     */
    public void invalidate() {
        Sign sign = getSign();
        for (int x = 0; x < 4; x++) {
            sign.setLine(x, "");
        }
        expiration = null;
        owner = null;
        price = Double.MAX_VALUE;
    }

    /**
     * Validates this gs, throwing a {@link ValidationException} if this gs is
     * invalid.<br>
     * A gs is considered invalid when:
     * <ul>
     * <li>It's world is null
     * <li>It's name is null
     * <li>It's sign is null
     * <li>It's expiration is null
     * <li>It's region is null
     * <li>It's sign does not exist in it's world
     * </ul>
     *
     * @throws ValidationException
     */
    public void validate() throws ValidationException {
        if (world == null) {
            throw new ValidationException(
                    "Invalides Grundstück: world ist null");
        }
        if (name == null) {
            throw new ValidationException("Invalides Grundstück: name ist null");
        }
        if (sign == null) {
            throw new ValidationException("Invalides Grundstück: sign ist null");
        }
        if (expiration == null) {
            throw new ValidationException(
                    "Invalides Grundstück: expiration ist null");
        }
        if (getInvalidRegion() == null) {
            throw new ValidationException(
                    "Invalides Grundstück: region existiert nicht");
        }
        if (!world.equals(sign.getWorld())) {
            throw new ValidationException(
                    "Invalides Grundstück: sign existiert nicht in dieser Welt");
        }
        if (getInvalidSign() == null) {
            throw new ValidationException(
                    "Invalides Grundstück: sign existiert nicht");
        }
    }

    /**
     * Returns the world of this gs. Never null.
     *
     * @return world - the world of this gs.
     * @throws ValidationException
     *             if this gs is invalid
     * @see Grundstueck#validate()
     */
    public World getWorld() throws ValidationException {
        validate();
        return world;
    }

    /**
     * Returns the region of this gs. Never null.
     *
     * @return region - the region of this gs.
     * @throws ValidationException
     *             if this gs is invalid
     * @see Grundstueck#validate()
     */
    public ProtectedRegion getRegion() {
        validate();
        return getInvalidRegion();
    }

    /**
     * Returns the region of this gs. May be null. This method does not validate
     * this gs.
     *
     * @return region - the region of this gs.
     */
    private ProtectedRegion getInvalidRegion() {
        return JavaPlugin.getPlugin(WorldGuardPlugin.class)
                .getRegionManager(world).getRegion(name);
    }

    /**
     * Returns the name of this gs. Never null.
     *
     * @return name - the name of this gs.
     * @throws ValidationException
     *             if this gs is invalid
     * @see Grundstueck#validate()
     */
    public String getName() {
        validate();
        return name;
    }

    /**
     * Returns the sign of this gs. Never null.
     *
     * @return sign - the sign of this gs.
     * @throws ValidationException
     *             if this gs is invalid
     * @see Grundstueck#validate()
     */
    public Sign getSign() {
        validate();
        return getInvalidSign();
    }

    /**
     * Returns the sign of this gs. May be null. This method does not validate
     * this gs.
     *
     * @return sign - the sign of this gs.
     */
    private Sign getInvalidSign() {
        BlockState state = sign.getState();
        if (state instanceof Sign) {
            return (Sign) state;
        }
        return null;
    }

    /**
     * Returns the owner of this gs. May be null. This method does not validate
     * this gs.
     *
     * @return owner - the owner of this gs.
     */
    public OfflinePlayer getOwner() {
        if (owner == null) {
            return null;
        } else {
            return Bukkit.getOfflinePlayer(owner);
        }
    }

    /**
     * Sets the owner of this gs. This also adds the new owner to the region.
     * The old owner is removed from the region.
     *
     * @throws ValidationException
     *             if this gs is invalid
     * @see Grundstueck#validate()
     */
    public void setOwner(OfflinePlayer newOwner) throws ValidationException {
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
     * Gibt den Ablauf-Tag der Grundstück Miete zurück. Niemals null.
     *
     * @return Date of Expiration
     * @throws ValidationException
     *             if this gs is invalid
     * @see Grundstueck#validate()
     */
    public LocalDate getExpiration() throws ValidationException {
        validate();
        return expiration;
    }

    // public void setExpiration(LocalDate expiration) {
    // this.expiration = expiration;
    // }

    /**
     * Returns whether this gs is rented.
     *
     * @throws ValidationException
     *             if this gs is invalid
     * @see Grundstueck#validate()
     */
    public boolean isRented() throws ValidationException {
        validate();
        return expiration.isAfter(LocalDate.now());
    }

    public void updateSignConent() throws ValidationException {
        Sign sign = getSign();
        String[] signContent = getSignContent();
        for (int x = 0; x < signContent.length; x++) {
            sign.setLine(x, signContent[x]);
        }
        sign.update();
    }

    private String[] getSignContent() throws ValidationException {
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

    public long getDaysLeft() throws ValidationException {
        validate();
        // Java 8:
        long timeLeft = ChronoUnit.DAYS.between(LocalDate.now(), expiration);
        // Java 7:
        // long timeLeft = Days.daysBetween(LocalDate.now(),
        // expiration).getDays();
        return timeLeft;
    }

    public void mieten(OfflinePlayer player) throws ValidationException {
        if (isRented()) {
            throw new IllegalStateException(
                    "Dieses Grundstück ist bereits vermietet.");
        }
        boolean paid = EconomyManager.payMoney(player, price);
        Player onlinePlayer = player.getPlayer();
        if (paid) {
            setOwner(player);
            expiration = LocalDate.now().plusDays(1);
            updateSignConent();
            String message = ChatColor.YELLOW
                    + "Du hast dieses Grundstück erfolgreich für einen Tag gemietet.";
            MinecraftUtils.sendMessage(onlinePlayer, message);
        } else {
            String message = ChatColor.RED
                    + "Du hast nicht genug Geld um dieses Grundstück zu mieten.";
            MinecraftUtils.sendMessage(onlinePlayer, message);
        }
    }

    public void mieteVerlaengern(long anzahlTage) throws ValidationException {
        if (!isRented()) {
            throw new IllegalArgumentException(
                    "Ein abgelaufenes Grundstück kann nicht verlängert werden.");
        }
        long daysLeft = getDaysLeft();
        if (daysLeft + anzahlTage > MAX_RENT_DAYS) {
            Player player = getOwner().getPlayer();
            String message = ChatColor.RED
                    + String.format(
                            "Ein Grundstück kann für maximal %s Tage gemietet werden.",
                            MAX_RENT_DAYS);
            MinecraftUtils.sendMessage(player, message);
            return;
        }
        boolean paid = EconomyManager.payMoney(getOwner(), price);
        Player onlinePlayer = getOwner().getPlayer();
        if (paid) {
            // Java 8:
            expiration = expiration.plusDays(anzahlTage);
            // Java 7:
            // expiration = expiration.plusDays((int) anzahlTage);

            updateSignConent();
            String message = ChatColor.YELLOW
                    + String.format(
                            "Deine Miete wurde verlängert. Dir bleiben noch %d Tage",
                            getDaysLeft());
            MinecraftUtils.sendMessage(onlinePlayer, message);
        } else {
            String message = ChatColor.RED
                    + "Du hast nicht genug Geld um die Miete zu verlängern.";
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
