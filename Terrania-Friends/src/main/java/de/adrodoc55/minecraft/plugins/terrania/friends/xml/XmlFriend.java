package de.adrodoc55.minecraft.plugins.terrania.friends.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.bukkit.OfflinePlayer;

@XmlRootElement(name = "friend")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlFriend {

	@XmlAttribute
	private String uuid;

	public XmlFriend() {
	}

	public XmlFriend(OfflinePlayer player) {
		this.uuid = String.valueOf(player.getUniqueId());
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

}
