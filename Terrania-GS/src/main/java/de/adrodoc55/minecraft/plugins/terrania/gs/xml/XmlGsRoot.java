package de.adrodoc55.minecraft.plugins.terrania.gs.xml;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.adrodoc55.minecraft.plugins.terrania.gs.Grundstueck;
import de.adrodoc55.minecraft.plugins.terrania.gs.GsManager;

@XmlRootElement(name = "grundstuecke")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlGsRoot {

  @XmlElement(name = "grundstueck")
  private Set<XmlGs> grundstueck;

  public XmlGsRoot() {}

  public XmlGsRoot(GsManager gsm) {
    Set<XmlGs> xmlGsSet = new HashSet<XmlGs>(gsm.getGrundstuecke().size());
    for (Grundstueck grundstueck : gsm.getGrundstuecke()) {
      XmlGs xmlGs = new XmlGs(grundstueck);
      xmlGsSet.add(xmlGs);
    }
    grundstueck = xmlGsSet;
  }

  /**
   * @return the grundstueck. Never null.
   */
  public Set<XmlGs> getGrundstueck() {
    if (grundstueck == null) {
      grundstueck = new HashSet<XmlGs>();
    }
    return grundstueck;
  }

  /**
   * @param grundstueck the grundstueck to set
   */
  public void setGrundstueck(Set<XmlGs> grundstueck) {
    this.grundstueck = grundstueck;
  }

}
