package de.adrodoc55.minecraft.plugins.terrania.gs.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.bukkit.OfflinePlayer;

import de.adrodoc55.minecraft.plugins.terrania.gs.Grundstueck;

@XmlRootElement(name = "grundstueck")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlGs {

  public XmlGs() {}

  public XmlGs(Grundstueck gs) {
    name = gs.getName();
    OfflinePlayer offlinePlayer = gs.getOwner();
    if (offlinePlayer == null) {
      owner = null;
    } else {
      owner = offlinePlayer.getUniqueId().toString();
    }
    // Java 8:
    expiration = gs.getExpiration().toEpochDay();
    // Java 7:
    // expiration = Days.daysBetween(LocalDate.fromDateFields(new Date(0)),
    // gs.getExpiration()).getDays();
    price = gs.getPrice();
    sign = new XmlSign(gs.getSign());
  }

  @XmlAttribute
  private String name;
  @XmlAttribute
  private String owner; // UUID
  @XmlAttribute
  private long expiration;
  @XmlAttribute
  private double price;
  @XmlElement(name = "sign")
  private XmlSign sign;

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the owner
   */
  public String getOwner() {
    return owner;
  }

  /**
   * @param owner the owner to set
   */
  public void setOwner(String owner) {
    this.owner = owner;
  }

  /**
   * @return the expiration
   */
  public long getExpiration() {
    return expiration;
  }

  /**
   * @param expiration the expiration to set
   */
  public void setExpiration(long expiration) {
    this.expiration = expiration;
  }

  /**
   * @return the price
   */
  public double getPrice() {
    return price;
  }

  /**
   * @param price the price to set
   */
  public void setPrice(double price) {
    this.price = price;
  }

  /**
   * @return the sign
   */
  public XmlSign getSign() {
    return sign;
  }

  /**
   * @param sign the sign to set
   */
  public void setSign(XmlSign sign) {
    this.sign = sign;
  }

}
