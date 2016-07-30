package de.adrodoc55.minecraft.plugins.terrania.friends;

import static de.adrodoc55.minecraft.plugins.terrania.friends.TerraniaFriendsPlugin.logger;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import de.adrodoc55.common.collections.CollectionUtils;
import de.adrodoc55.minecraft.plugins.terrania.friends.xml.XmlFriend;
import de.adrodoc55.minecraft.plugins.terrania.friends.xml.XmlFriendOwner;

public class FriendsManager {

  // private static FriendsManager INSTANCE;
  //
  // public static FriendsManager getInstance() {
  // if (INSTANCE == null) {
  // INSTANCE = new FriendsManager();
  // }
  // return INSTANCE;
  // }

  private static File getFriendsDir() {
    File friendsDir = new File(TerraniaFriendsPlugin.instance().getDataFolder(), "freunde");
    friendsDir.mkdirs();
    return friendsDir;
  }

  private static File getPlayerFile(OfflinePlayer player) {
    File playerFile = new File(getFriendsDir(), player.getUniqueId() + ".xml");
    return playerFile;
  }

  // private static File getExistingPlayerFile(OfflinePlayer player) {
  // File gsFile = getPlayerFile(player);
  // if (!gsFile.exists()) {
  // new GsManager(world, new XmlGsRoot()).save();
  // }
  // return gsFile;
  // }

  private static final Map<UUID, Set<UUID>> CACHE = new HashMap<UUID, Set<UUID>>();

  private static Set<UUID> getFriendUuids(OfflinePlayer player) {
    Set<UUID> friendUuids = CACHE.get(player.getUniqueId());
    if (friendUuids == null) {
      friendUuids = load(player);
    }
    return friendUuids;
  }

  public static Set<OfflinePlayer> getFriends(OfflinePlayer player) {
    Set<UUID> friendUuids = getFriendUuids(player);
    Set<OfflinePlayer> friends = uuidsToPlayers(friendUuids);
    return friends;
  }

  public static void addFriend(OfflinePlayer player, OfflinePlayer friend) {
    Set<UUID> friendUuids = getFriendUuids(player);
    UUID friendUuid = friend.getUniqueId();
    friendUuids.add(friendUuid);
  }

  public static void removeFriend(OfflinePlayer player, OfflinePlayer friend) {
    Set<UUID> friendUuids = getFriendUuids(player);
    UUID friendUuid = friend.getUniqueId();
    friendUuids.remove(friendUuid);
  }

  public static boolean isFriend(OfflinePlayer player, OfflinePlayer friend) {
    Set<UUID> friendUuids = getFriendUuids(player);
    UUID friendUuid = friend.getUniqueId();
    return friendUuids.contains(friendUuid);
  }

  private static Set<UUID> xmlPlayersToUuids(Set<XmlFriend> xmlFriends) {
    Set<UUID> friendUuids = new HashSet<UUID>();
    for (XmlFriend xmlFriend : xmlFriends) {
      UUID friendUuid = UUID.fromString(xmlFriend.getUuid());
      friendUuids.add(friendUuid);
    }
    return friendUuids;
  }

  private static Set<OfflinePlayer> uuidsToPlayers(Set<UUID> friendUuids) {
    Set<OfflinePlayer> friends = new HashSet<OfflinePlayer>();
    for (UUID friendUuid : friendUuids) {
      OfflinePlayer friend = Bukkit.getOfflinePlayer(friendUuid);
      friends.add(friend);
    }
    return friends;
  }

  private static Set<UUID> load(OfflinePlayer player) {
    String format = "Lade Freunde des Spielers '%s'";
    String message = String.format(format, player.getName());
    logger().info(message);
    File playerFile = getPlayerFile(player);
    Set<UUID> friendUuids;
    if (!playerFile.exists()) {
      friendUuids = new HashSet<UUID>();
    } else {
      try {
        JAXBContext jaxbContext = JAXBContext.newInstance(XmlFriendOwner.class);
        Unmarshaller unmarshaller;
        unmarshaller = jaxbContext.createUnmarshaller();
        XmlFriendOwner root = (XmlFriendOwner) unmarshaller.unmarshal(playerFile);

        Set<XmlFriend> xmlFriends = root.getFriends();
        friendUuids = xmlPlayersToUuids(xmlFriends);
      } catch (JAXBException ex) {
        String errorMessage =
            String.format("JAXBException beim Laden der Freunde des Spielers '%s' mit UUID '%s'",
                player.getName(), player.getUniqueId());
        throw new RuntimeException(errorMessage, ex);
      }
    }
    CACHE.put(player.getUniqueId(), friendUuids);
    return friendUuids;
  }

  public static void save(OfflinePlayer player) {
    try {
      // TODO: setDirty für Player
      logger().info("Speichere Freunde des Spielers " + player.getName());
      JAXBContext jaxbContext = JAXBContext.newInstance(XmlFriendOwner.class);
      Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
      // TODO: format output rausnehmen
      jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

      Set<OfflinePlayer> friends = getFriends(player);
      XmlFriendOwner root = new XmlFriendOwner(player, friends);
      jaxbMarshaller.marshal(root, getPlayerFile(player));
      CACHE.remove(player.getUniqueId());
    } catch (JAXBException ex) {
      String errorMessage =
          String.format("JAXBException beim Speichern der Freunde des Spielers '%s' mit UUID '%s'",
              player.getName(), player.getUniqueId());
      throw new RuntimeException(errorMessage, ex);
    }
  }

  public static void saveAll() {
    ArrayList<UUID> playerUuids = CollectionUtils.newArrayList(CACHE.keySet());
    for (UUID playerUuid : playerUuids) {
      OfflinePlayer player = Bukkit.getOfflinePlayer(playerUuid);
      save(player);
    }
  }
}
