package de.adrodoc55.minecraft.plugins.terrania.gs;

import static de.adrodoc55.minecraft.plugins.terrania.gs.TerraniaGsPlugin.logger;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.bukkit.World;
import org.bukkit.block.Sign;

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

    private static volatile Map<UUID, GsManager> INSTANCES = new HashMap<UUID, GsManager>();

    /**
     * Gibt eine unmodifiable Collection aller aktiven GSManager zurück.
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
        File gsDir = new File(TerraniaGsPlugin.instance().getDataFolder(),
                "grundstuecke");
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
            // TODO: jedem Grundstück ein eigenes File geben
            // TODO: setDirty für Grundstücke
            logger().info("Speichere Grundstücke der Welt " + world.getName());
            JAXBContext jaxbContext = JAXBContext.newInstance(XmlGsRoot.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            // TODO: format output rausnehmen
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            XmlGsRoot root = new XmlGsRoot(this);
            jaxbMarshaller.marshal(root, getGsFile(world));
        } catch (JAXBException ex) {
            String errorMessage = String
                    .format("JAXBException beim Speichern des GSManagers für Welt '%s' mit UUID '%s'",
                            world.getName(), world.getUID());
            throw new PluginException(512, errorMessage, ex);
        }
    }

    private static GsManager load(World world) {
        String worldName = world.getName();
        UUID worldUuid = world.getUID();
        String message = String.format("Lade Grundstücke für Welt '%s'",
                worldName);
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
            String errorMessage = String
                    .format("JAXBException beim Laden des GSManagers für Welt '%s' mit UUID '%s'",
                            worldName, worldUuid);
            throw new PluginException(513, errorMessage, ex);
        }
    }

    private static ScheduledExecutorService scheduler;

    private static ScheduledExecutorService getScheduler() {
        if (scheduler == null) {
            scheduler = Executors
                    .newSingleThreadScheduledExecutor(new ThreadFactory() {
                        private ThreadFactory defaultThreadFactory = Executors
                                .defaultThreadFactory();

                        @Override
                        public Thread newThread(Runnable r) {
                            Thread t = defaultThreadFactory.newThread(r);
                            t.setDaemon(true);
                            return t;
                        }
                    });
        }
        return scheduler;
    }

    private static volatile boolean setup;

    public static void setupUpdateScheduler() {
        if (setup) {
            return;
        }
        synchronized (GsManager.class) {
            if (setup) {
                return;
            }
            setup = true;
        }
        // Java 8:
        ScheduledExecutorService scheduler = getScheduler();
        Runnable command = () -> {
            logger().info("Update alle Grundstücke");
            for (GsManager gsm : GsManager.getActiveInstances()) {
                gsm.updateAlleGrundstuecke();
            }
            logger().info("Update alle Grundstücke FINISHED");
        };
        // @formatter:off
        LocalDateTime atStartOfTomorrow = LocalDate.now().plusDays(1).atStartOfDay().plusMinutes(1);
        long initialDelay = LocalDateTime.now().until(atStartOfTomorrow, ChronoUnit.MINUTES);
        long period = TimeUnit.DAYS.toMinutes(1);
        scheduler.scheduleAtFixedRate(command, initialDelay, period, TimeUnit.MINUTES);
        // @formatter:on

        // Java 7:
        // Timer timer = new Timer(true);
        // LocalDateTime atStartOfTomorrow =
        // LocalDate.now().plusDays(1).toLocalDateTime(new LocalTime(0, 0));
        // Date firstTime = atStartOfTomorrow.toDate(TimeZone.getDefault());
        // int period = 1000 * 60 * 60 * 24;
        // timer.schedule(new TimerTask() {
        // @Override
        // public void run() {
        // // Jeden Tag um 00:00 werden alle abgelaufenen Grundstücke
        // // freigegeben.
        // logger().info("Update alle Grundstücke");
        // updateAlleGrundstuecke();
        // logger().info("Update alle Grundstücke FINISHED");
        // }
        // }, firstTime, period);
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
        updateAlleGrundstuecke();
        setupUpdateScheduler();
    }

    public synchronized void updateAlleGrundstuecke() {
        for (Grundstueck grundstueck : getGrundstuecke()) {
            grundstueck.updateSignConent();
        }
    }

    /**
     * Findet das Grundstück, falls es existiert und gibt es zurück. Ansonsten
     * wird null zurückgegeben.
     *
     * @param sign
     *            - das Sign des Grundstückes nach dem gesucht werden soll.
     * @return das Grundstück oder null.
     */
    public Grundstueck find(final Sign sign) {
        Grundstueck grundstueck = CollectionUtils.find(getGrundstuecke(),
                new Closure<Grundstueck, Boolean>() {
                    @Override
                    public Boolean call(Grundstueck grundstueck) {
                        return grundstueck.getSign().equals(sign);
                    }
                });
        return grundstueck;
    }

    /**
     * Findet das Grundstück, falls es existiert und gibt es zurück. Ansonsten
     * wird null zurückgegeben.
     *
     * @param gsName
     *            - der Name des Grundstückes nach dem gesucht werden soll.
     * @return das Grundstück oder null.
     */
    public Grundstueck find(final String gsName) {
        Grundstueck grundstueck = CollectionUtils.find(getGrundstuecke(),
                new Closure<Grundstueck, Boolean>() {
                    @Override
                    public Boolean call(Grundstueck grundstueck) {
                        return grundstueck.getName().equals(gsName);
                    }
                });
        return grundstueck;
    }

    /**
     * Gibt eine unmodifiable Liste der Grundstücke zurück.
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
                String message = String.format(
                        "Lösche Grundstück %s in Welt %s. Grund: %s",
                        gs.getName(), gs.getWorld().getName(), ex.getMessage());
                logger().warn(message);
                gs.invalidate();
                iterator.remove();
            }
        }
    }

    public World getWorld() {
        return world;
    }

    /**
     * Fügt das Grundstück dem passenden GSManager hinzu, falls das Element noch
     * nicht enthalten ist.<br>
     * <br>
     * Siehe {@link Set#add Set.add}
     *
     * @param grundstueck
     * @throws ValidationException
     *             wenn das Grundstück invalide ist.
     * @return true, falls das Element hinzugefügt wurde. Ansonsten false.
     */
    public static boolean add(Grundstueck grundstueck)
            throws ValidationException {
        grundstueck.validate();
        GsManager gsm = getGSManager(grundstueck.getWorld());
        Grundstueck find = gsm.find(grundstueck.getSign());
        if (find != null) {
            String message = String.format(
                    "Dieses Schild wird bereits vom Grundstück %s verwendet",
                    find.getName());
            throw new ValidationException(message);
        }
        boolean success = gsm.grundstuecke.add(grundstueck);
        if(success) {
            setDefaults(grundstueck.getRegion());
            grundstueck.updateSignConent();
        }
        return success;
    }

    private static void setDefaults(ProtectedRegion region) {
        region.setPriority(20);
        region.setFlag(DefaultFlag.BUILD, State.ALLOW);
        region.setFlag(DefaultFlag.BUILD.getRegionGroupFlag(),
                RegionGroup.MEMBERS);
    }

    /**
     * Entfernt das Grundstück von dem passenden GSManager, falls das Element
     * enthalten ist. <br>
     * Das Grundstueck wird hierdurch invalide. Siehe
     * {@link Grundstueck#validate()}
     *
     * @param grundstueck
     * @return true, falls das Element enthalten war. Ansonsten false.
     * @see Set#remove(Object o)
     */
    public static boolean remove(Grundstueck grundstueck) {
        GsManager gsm = getGSManager(grundstueck.getWorld());
        grundstueck.invalidate();
        return gsm.grundstuecke.remove(grundstueck);
    }

}
