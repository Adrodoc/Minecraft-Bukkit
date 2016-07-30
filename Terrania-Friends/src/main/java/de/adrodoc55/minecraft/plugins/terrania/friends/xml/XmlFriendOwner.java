package de.adrodoc55.minecraft.plugins.terrania.friends.xml;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.bukkit.OfflinePlayer;

@XmlRootElement(name = "player")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlFriendOwner {

  @XmlAttribute
  private String uuid;

  @XmlElement(name = "friend")
  private Set<XmlFriend> friends;

  public XmlFriendOwner() {}

  public XmlFriendOwner(OfflinePlayer player, Set<OfflinePlayer> friends) {
    this.uuid = String.valueOf(player.getUniqueId());
    Set<XmlFriend> xmlFriendSet = new HashSet<XmlFriend>(friends.size());
    for (OfflinePlayer friend : friends) {
      XmlFriend xmlFriend = new XmlFriend(friend);
      xmlFriendSet.add(xmlFriend);
    }
    this.friends = xmlFriendSet;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public Set<XmlFriend> getFriends() {
    if (friends == null) {
      friends = new HashSet<XmlFriend>();
    }
    return friends;
  }

  public void setFriends(Set<XmlFriend> friends) {
    this.friends = friends;
  }

}
