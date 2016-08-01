package de.adrodoc55.minecraft.plugins.terrania.gs;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlRootElement;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import de.adrodoc55.common.CommonUtils;
import de.adrodoc55.minecraft.plugins.common.utils.MinecraftUtils;
import de.adrodoc55.minecraft.plugins.terrania.gs.xml.XmlGs;
import de.adrodoc55.minecraft.plugins.terrania.gs.xml.XmlSign;
import me.leepreechaun.terraniacore.oekonomie.Wirtschaftsmanager;

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
    expiration = LocalDate.ofEpochDay(gs.getExpiration());
    XmlSign xmlSign = gs.getSign();
    int signX = xmlSign.getX();
    int signY = xmlSign.getY();
    int signZ = xmlSign.getZ();
    Block block = world.getBlockAt(signX, signY, signZ);
    sign = block;
    updateSignContent();
  }

  /**
   * Returns the world of this gs. Never null.
   *
   * @return world - the world of this gs.
   * @throws ValidationException if this gs is invalid
   * @see Grundstueck#validate()
   */
  public World getWorld() throws ValidationException {
    validate();
    return world;
  }

  /**
   * Returns the world of this gs. May be null. This method does not validate this gs.
   *
   * @return world - the {@link #world} of this gs.
   */
  public @Nullable World getInvalidWorld() {
    return world;
  }

  /**
   * Returns the region of this gs. Never null.
   *
   * @return region - the region of this gs.
   * @throws ValidationException if this gs is invalid
   * @see Grundstueck#validate()
   */
  public ProtectedRegion getRegion() {
    validate();
    return getInvalidRegion();
  }

  /**
   * Returns the region of this gs. May be null. This method does not validate this gs.
   *
   * @return region - the region of this gs.
   */
  private @Nullable ProtectedRegion getInvalidRegion() {
    return JavaPlugin.getPlugin(WorldGuardPlugin.class).getRegionManager(world).getRegion(name);
  }

  /**
   * Returns the name of this gs. Never null.
   *
   * @return name - the name of this gs.
   * @throws ValidationException if this gs is invalid
   * @see Grundstueck#validate()
   */
  public String getName() {
    validate();
    return name;
  }

  /**
   * Returns the name of this gs. May be null. This method does not validate this gs.
   *
   * @return name - the {@link #name} of this gs.
   */
  public @Nullable String getInvalidName() {
    return name;
  }

  /**
   * Returns the sign of this gs. Never null.
   *
   * @return sign - the sign of this gs.
   * @throws ValidationException if this gs is invalid
   * @see Grundstueck#validate()
   */
  public Sign getSign() throws ValidationException {
    validate();
    return getInvalidSign();
  }

  /**
   * Returns the sign of this gs. May be null. This method does not validate this gs.
   *
   * @return sign - the sign of this gs.
   */
  private @Nullable Sign getInvalidSign() {
    BlockState state = sign.getState();
    if (state instanceof Sign) {
      return (Sign) state;
    }
    return null;
  }

  /**
   * Returns the owner of this gs. May be null. This method does not validate this gs.
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
   * Sets the owner of this gs. This also adds the new owner to the region. The old owner is removed
   * from the region.
   *
   * @throws ValidationException if this gs is invalid
   * @see Grundstueck#validate()
   */
  public void setOwner(@Nullable OfflinePlayer newOwner) throws ValidationException {
    DefaultDomain owners = getRegion().getOwners();
    if (owner != null) {
      owners.removePlayer(owner);
    }
    if (newOwner != null) {
      owners.addPlayer(newOwner.getUniqueId());
      owner = newOwner.getUniqueId();
    }
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
   * @throws ValidationException if this gs is invalid
   * @see Grundstueck#validate()
   */
  public LocalDate getExpiration() throws ValidationException {
    validate();
    return expiration;
  }

  /**
   * Returns whether this gs is actively rented. If a gs expires it is no longer actively rented,
   * but can not yet be rented again (See {@link #canBeRented()}).
   *
   * @throws ValidationException if this gs is invalid
   * @see Grundstueck#validate()
   */
  public boolean isActivelyRented() throws ValidationException {
    validate();
    return LocalDate.now().isBefore(expiration);
  }

  /**
   * Returns whether this gs can be rented. If the gs expires the owner has three days left to
   * extend the rent.
   *
   * @throws ValidationException if this gs is invalid
   * @see Grundstueck#validate()
   */
  public boolean canBeRented() throws ValidationException {
    validate();
    return owner == null || !LocalDate.now().minusDays(3).isBefore(expiration);
  }

  public void update() {
    updateSignContent();
    if (canBeRented()) {
      removeOwnerAndMember();
    } else if (isActivelyRented()) {
      ProtectedRegion region = getRegion();
      region.setFlag(DefaultFlag.BUILD, State.ALLOW);
      region.setFlag(DefaultFlag.BLOCK_BREAK, State.ALLOW);
      region.setFlag(DefaultFlag.BLOCK_PLACE, State.ALLOW);
    } else { // Ausgelaufen
      ProtectedRegion region = getRegion();
      region.setFlag(DefaultFlag.BUILD, State.DENY);
      region.setFlag(DefaultFlag.BLOCK_BREAK, State.DENY);
      region.setFlag(DefaultFlag.BLOCK_PLACE, State.DENY);
    }
  }

  /**
   * Remove all owner and member.
   */
  private void removeOwnerAndMember() {
    owner = null;
    ProtectedRegion region = getRegion();
    region.getOwners().clear();
    region.getMembers().clear();
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
    sign.update();
    removeOwnerAndMember();
    expiration = null;
    price = Double.MAX_VALUE;
  }

  /**
   * Validates this gs, throwing a {@link ValidationException} if this gs is invalid.<br>
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
      throw new ValidationException("Invalides Grundstück: world ist null");
    }
    if (name == null) {
      throw new ValidationException("Invalides Grundstück: name ist null");
    }
    if (sign == null) {
      throw new ValidationException("Invalides Grundstück: sign ist null");
    }
    if (expiration == null) {
      throw new ValidationException("Invalides Grundstück: expiration ist null");
    }
    if (getInvalidRegion() == null) {
      throw new ValidationException("Invalides Grundstück: region existiert nicht");
    }
    if (!world.equals(sign.getWorld())) {
      throw new ValidationException("Invalides Grundstück: sign existiert nicht in dieser Welt");
    }
    if (getInvalidSign() == null) {
      throw new ValidationException("Invalides Grundstück: sign existiert nicht");
    }
  }

  public void updateSignContent() throws ValidationException {
    Sign sign = getSign();
    String[] signContent = getSignContent();
    for (int x = 0; x < signContent.length; x++) {
      sign.setLine(x, signContent[x]);
    }
    sign.update();
  }

  private String[] getSignContent() throws ValidationException {
    String[] content = new String[4];
    if (isActivelyRented()) {
      content[0] = "[Vermietet]";
      content[1] = name;
      content[2] = getOwner().getName();
      content[3] = "Noch " + getDaysLeft() + " Tage";
    } else if (canBeRented()) {
      content[0] = "[Zu vermieten]";
      content[1] = name;
      content[2] = "Preis: " + CommonUtils.doubleToString(price);
      content[3] = "";
    } else {
      content[0] = "[Abgelaufen]";
      content[1] = name;
      content[2] = getOwner().getName();
      content[3] = "Noch " + (getDaysLeft() + 3) + " Tage";
    }
    return content;
  }

  public long getDaysLeft() throws ValidationException {
    validate();
    return ChronoUnit.DAYS.between(LocalDate.now(), expiration);
  }

  public void rent(OfflinePlayer player) throws ValidationException {
    if (!canBeRented()) {
      throw new IllegalStateException("Dieses Grundstück ist bereits vermietet.");
    }
    boolean paid = Wirtschaftsmanager.removeMoney(player.getUniqueId(), price);
    Player onlinePlayer = player.getPlayer();
    if (paid) {
      setOwner(player);
      expiration = LocalDate.now().plusDays(1);
      update();
      String message = "Du hast dieses Grundstück erfolgreich für einen Tag gemietet.";
      MinecraftUtils.sendInfo(onlinePlayer, message);
    } else {
      String message = "Du hast nicht genug Geld um dieses Grundstück zu mieten.";
      MinecraftUtils.sendError(onlinePlayer, message);
    }
  }

  public void extendRent(long anzahlTage) throws ValidationException {
    if (canBeRented()) {
      throw new IllegalArgumentException(
          "Ein abgelaufenes Grundstück kann nicht verlängert werden.");
    }
    long daysLeft = getDaysLeft();
    if (daysLeft + anzahlTage > MAX_RENT_DAYS) {
      Player player = getOwner().getPlayer();
      String message =
          String.format("Ein Grundstück kann für maximal %s Tage gemietet werden.", MAX_RENT_DAYS);
      MinecraftUtils.sendError(player, message);
      return;
    }
    boolean paid = Wirtschaftsmanager.removeMoney(getOwner().getUniqueId(), price);
    Player onlinePlayer = getOwner().getPlayer();
    if (!paid) {
      String message = "Du hast nicht genug Geld um die Miete zu verlängern.";
      MinecraftUtils.sendError(onlinePlayer, message);
    } else {
      expiration = expiration.plusDays(anzahlTage);

      update();
      String message =
          String.format("Deine Miete wurde verlängert. Dir bleiben noch %d Tage", getDaysLeft());
      MinecraftUtils.sendInfo(onlinePlayer, message);
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
