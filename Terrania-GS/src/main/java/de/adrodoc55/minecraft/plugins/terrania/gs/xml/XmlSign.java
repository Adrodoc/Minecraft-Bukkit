package de.adrodoc55.minecraft.plugins.terrania.gs.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.bukkit.block.Sign;

@XmlRootElement(name = "sign")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlSign {

	@XmlAttribute
	private int x;
	@XmlAttribute
	private int y;
	@XmlAttribute
	private int z;

	public XmlSign() {
	}

	public XmlSign(Sign sign) {
		x = sign.getX();
		y = sign.getY();
		z = sign.getZ();
	}

	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}

	/**
	 * @param x
	 *            the x to set
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}

	/**
	 * @param y
	 *            the y to set
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * @return the z
	 */
	public int getZ() {
		return z;
	}

	/**
	 * @param z
	 *            the z to set
	 */
	public void setZ(int z) {
		this.z = z;
	}

}
