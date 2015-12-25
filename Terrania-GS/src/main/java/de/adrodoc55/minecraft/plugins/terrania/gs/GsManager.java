package de.adrodoc55.minecraft.plugins.terrania.gs;

import static de.adrodoc55.minecraft.plugins.terrania.gs.TerraniaGsPlugin.logger;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.bukkit.World;
import org.bukkit.block.Sign;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import de.adrodoc55.common.collections.Closure;
import de.adrodoc55.common.collections.CollectionUtils;
import de.adrodoc55.minecraft.plugins.common.PluginException;
import de.adrodoc55.minecraft.plugins.terrania.gs.xml.XmlGs;
import de.adrodoc55.minecraft.plugins.terrania.gs.xml.XmlGsRoot;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.ZoneId;

public class GsManager {

	private static Map<UUID, GsManager> INSTANCES = new HashMap<UUID, GsManager>();

	/**
	 * Gibt eine unmodifiable Collection aller aktiven GSManager zur�ck.
	 *
	 * @return eine Liste aller aktiven GSManager
	 */
	public static Collection<GsManager> getActiveInstances() {
		return Collections.unmodifiableCollection(INSTANCES.values());
	}

	public static GsManager getGSManager(World world) {
		if (world == null) {
			throw new IllegalArgumentException("Parameter world ist null");
		}
		UUID uuid = world.getUID();
		GsManager instance = GsManager.INSTANCES.get(uuid);
		if (instance != null) {
			return instance;
		}
		return load(world);
	}

	private static File getGsDir() {
		File gsDir = new File(TerraniaGsPlugin.instance().getDataFolder(), "grundstuecke");
		gsDir.mkdirs();
		return gsDir;
	}

	private static File getGsFile(World world) {
		File gsFile = new File(getGsDir(), world.getName() + ".xml");
		return gsFile;
	}

	private static File getExistingGsFile(World world) {
		File gsFile = getGsFile(world);
		if (!gsFile.exists()) {
			new GsManager(world, new XmlGsRoot()).save();
		}
		return gsFile;
	}

	public static void saveAll() {
		for (GsManager gsm : getActiveInstances()) {
			gsm.save();
		}
	}

	public void save() {
		try {
			// TODO: jedem Grundst�ck ein eigenes File geben
			// TODO: setDirty f�r Grundst�cke
			logger().info("Speichere Grundst�cke der Welt " + world.getName());
			JAXBContext jaxbContext = JAXBContext.newInstance(XmlGsRoot.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			// TODO: format output rausnehmen
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			XmlGsRoot root = new XmlGsRoot(this);
			jaxbMarshaller.marshal(root, getGsFile(world));
		} catch (JAXBException ex) {
			String errorMessage = String.format(
					"JAXBException beim Speichern des GSManagers f�r Welt '%s' mit UUID '%s'", world.getName(),
					world.getUID());
			throw new PluginException(512, errorMessage, ex);
		}
	}

	private static GsManager load(World world) {
		String worldName = world.getName();
		UUID worldUuid = world.getUID();
		String message = String.format("Lade Grundst�cke f�r Welt '%s'", worldName);
		logger().info(message);
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(XmlGsRoot.class);
			Unmarshaller unmarshaller;
			unmarshaller = jaxbContext.createUnmarshaller();
			File gsFile = getExistingGsFile(world);
			XmlGsRoot root = (XmlGsRoot) unmarshaller.unmarshal(gsFile);

			GsManager instance = new GsManager(world, root);
			INSTANCES.put(worldUuid, instance);
			return instance;
		} catch (JAXBException ex) {
			String errorMessage = String.format("JAXBException beim Laden des GSManagers f�r Welt '%s' mit UUID '%s'",
					worldName, worldUuid);
			throw new PluginException(513, errorMessage, ex);
		}
	}

	private final World world;

	private final Set<Grundstueck> grundstuecke;

	private GsManager(World world, XmlGsRoot root) {
		this.world = world;
		Set<XmlGs> xmlGrundstuecke = root.getGrundstueck();
		grundstuecke = new HashSet<Grundstueck>(xmlGrundstuecke.size());
		for (XmlGs xmlGs : xmlGrundstuecke) {
			grundstuecke.add(new Grundstueck(world, xmlGs));
		}
		init();
	}

	public void init() {
		updateAlleGrundstuecke();

		Timer timer = new Timer(true);
		// TODO Java 8:
		// LocalDateTime atStartOfTomorrow = LocalDate.now().plusDays(1)
		// .atStartOfDay();
		// Java 7:
		LocalDateTime atStartOfTomorrow = LocalDate.now().plusDays(1).toLocalDateTime(new LocalTime(0, 0));

		// TODO Java 8:
		// Date firstTime = Date.from(atStartOfTomorrow.atZone(
		// ZoneId.systemDefault()).toInstant());
		// Java 7:
		Date firstTime = atStartOfTomorrow.toDate(TimeZone.getDefault());
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				// Jeden Tag um 00:00 werden alle abgelaufenen Grundst�cke
				// freigegeben.
				logger().info("Update alle Grundst�cke");
				updateAlleGrundstuecke();
				logger().info("Update alle Grundst�cke FINISHED");
			}
		}, firstTime, 1000 * 60 * 60 * 24 * 7);
	}

	public synchronized void updateAlleGrundstuecke() {
		for (Grundstueck grundstueck : getGrundstuecke()) {
			grundstueck.updateSignConent();
		}
	}

	/**
	 * Findet das Grundst�ck, falls es existiert und gibt es zur�ck. Ansonsten
	 * wird null zur�ckgegeben.
	 *
	 * @param sign
	 *            - das Sign des Grundst�ckes nach dem gesucht werden soll.
	 * @return das Grundst�ck oder null.
	 */
	public Grundstueck find(final Sign sign) {
		Grundstueck grundstueck = CollectionUtils.find(getGrundstuecke(), new Closure<Grundstueck, Boolean>() {
			@Override
			public Boolean call(Grundstueck grundstueck) {
				return grundstueck.getSign().equals(sign);
			}
		});
		return grundstueck;
	}

	/**
	 * Findet das Grundst�ck, falls es existiert und gibt es zur�ck. Ansonsten
	 * wird null zur�ckgegeben.
	 *
	 * @param gsName
	 *            - der Name des Grundst�ckes nach dem gesucht werden soll.
	 * @return das Grundst�ck oder null.
	 */
	public Grundstueck find(final String gsName) {
		Grundstueck grundstueck = CollectionUtils.find(getGrundstuecke(), new Closure<Grundstueck, Boolean>() {
			@Override
			public Boolean call(Grundstueck grundstueck) {
				return grundstueck.getName().equals(gsName);
			}
		});
		return grundstueck;
	}

	/**
	 * Gibt eine unmodifiable Liste der Grundst�cke zur�ck.
	 *
	 * @return the grundstuecke
	 */
	public Set<Grundstueck> getGrundstuecke() {
		entferneAlleInvalidenGrundstuecke();
		return Collections.unmodifiableSet(grundstuecke);
	}

	public void entferneAlleInvalidenGrundstuecke() {
		Iterator<Grundstueck> iterator = grundstuecke.iterator();
		while (iterator.hasNext()) {
			Grundstueck gs = iterator.next();
			try {
				gs.validate();
			} catch (ValidationException ex) {
				String message = String.format("L�sche Grundst�ck %s in Welt %s. Grund: %s", gs.getName(),
						gs.getWorld().getName(), ex.getMessage());
				logger().warn(message);
				iterator.remove();
			}
		}
	}

	public World getWorld() {
		return world;
	}

	/**
	 * F�gt das Grundst�ck dem passenden GSManager hinzu, falls das Element noch
	 * nicht enthalten ist.<br>
	 * <br>
	 * Siehe {@link Set#add Set.add}
	 *
	 * @param grundstueck
	 * @throws ValidationException
	 *             wenn das Grundst�ck invalide ist.
	 * @return true, falls das Element hinzugef�gt wurde. Ansonsten false.
	 */
	public static boolean add(Grundstueck grundstueck) throws ValidationException {
		grundstueck.validate();
		GsManager gsManager = getGSManager(grundstueck.getWorld());
		Grundstueck find = gsManager.find(grundstueck.getSign());
		if (find != null) {
			String message = String.format("Dieses Schild wird bereits vom Grundst�ck %s verwendet", find.getName());
			throw new ValidationException(message);
		}
		// boolean contained =
		// gsManager.getGrundstuecke().contains(grundstueck);
		// if (!contained) {
		// gsManager.grundstuecke.add(grundstueck);
		// }
		setDefaults(grundstueck.getRegion());
		return gsManager.grundstuecke.add(grundstueck);
	}

	private static void setDefaults(ProtectedRegion region) {
		region.setPriority(2);
		region.setFlag(DefaultFlag.BUILD, State.ALLOW);
		region.setFlag(DefaultFlag.BUILD.getRegionGroupFlag(), RegionGroup.MEMBERS);
	}

	/**
	 * Entfernt das Grundst�ck von dem passenden GSManager, falls das Element
	 * enthalten ist.<br>
	 * <br>
	 * Siehe {@link Set#remove Set.remove}
	 *
	 * @param grundstueck
	 * @return true, falls das Element enthalten war. Ansonsten false.
	 */
	public static boolean remove(Grundstueck grundstueck) {
		return getGSManager(grundstueck.getWorld()).grundstuecke.remove(grundstueck);
	}

}
