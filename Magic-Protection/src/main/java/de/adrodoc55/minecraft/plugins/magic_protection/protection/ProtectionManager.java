package de.adrodoc55.minecraft.plugins.magic_protection.protection;

import static de.adrodoc55.minecraft.plugins.magic_protection.MagicProtectionPlugin.logger;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import de.adrodoc55.minecraft.plugins.common.PluginException;
import de.adrodoc55.minecraft.plugins.common.utils.MaterialUtils;
import de.adrodoc55.minecraft.plugins.magic_protection.MagicProtectionPlugin;

public class ProtectionManager {

  // static {
  // Material[] replaceable = { Material.AIR, Material.FIRE, Material.WATER,
  // Material.STATIONARY_WATER, Material.LAVA,
  // Material.STATIONARY_LAVA, Material.LONG_GRASS,
  // Material.DEAD_BUSH, Material.VINE, Material.SNOW,
  // Material.WATER_LILY };
  // Material[] gravity = { Material.SAND, Material.GRAVEL, Material.ANVIL,
  // Material.DRAGON_EGG };
  // for (Material m : gravity)
  // STANDING.add(m);
  // Material[] multiBlock = { Material.BED };
  // Material[] spontain = { Material.SAPLING, Material.TNT };
  // Material[] standing = { Material.WOODEN_DOOR, Material.BIRCH_DOOR,
  // Material.SPRUCE_DOOR, Material.JUNGLE_DOOR,
  // Material.ACACIA_DOOR, Material.DARK_OAK_DOOR,
  // Material.DOUBLE_PLANT, Material.YELLOW_FLOWER,
  // Material.RED_ROSE, Material.BROWN_MUSHROOM,
  // Material.RED_MUSHROOM, Material.CACTUS, Material.CARPET,
  // Material.SIGN_POST, Material.FLOWER_POT,
  // Material.STANDING_BANNER, Material.STONE_PLATE,
  // Material.WOOD_PLATE, Material.GOLD_PLATE, Material.IRON_PLATE,
  // Material.REDSTONE_WIRE, /*
  // * Material.repeater ,
  // */
  // Material.REDSTONE_COMPARATOR_OFF,
  // Material.REDSTONE_COMPARATOR_ON, Material.POWERED_RAIL,
  // Material.DETECTOR_RAIL, Material.RAILS,
  // Material.ACTIVATOR_RAIL, Material.SUGAR_CANE_BLOCK,
  // Material.CROPS, Material.PUMPKIN_STEM, Material.MELON_STEM,
  // Material.POTATO, Material.CARROT, Material.CAKE_BLOCK };
  // for (Material m : standing)
  // STANDING.add(m);
  // Material[] torches = { Material.TORCH, Material.LEVER,
  // Material.REDSTONE_TORCH_OFF, Material.REDSTONE_TORCH_ON,
  // Material.WOOD_BUTTON, Material.STONE_BUTTON };
  // for (Material m : torches)
  // TORCHES_AND_BUTTONS.add(m);
  // Material[] trapdoors = { Material.TRAP_DOOR, Material.IRON_TRAPDOOR };
  // for (Material m : trapdoors)
  // TRAPDOORS.add(m);
  // Material[] hanging = { Material.LADDER, Material.WALL_SIGN,
  // Material.BANNER, Material.TRIPWIRE_HOOK };
  // for (Material m : hanging)
  // HANGING.add(m);
  // Material[] cocoa = { Material.COCOA };
  // for (Material m : cocoa)
  // COCOA.add(m);
  // // Material[] inventory = { Material.CHEST, Material.FURNACE,
  // // Material.JUKEBOX, Material.TRAPPED_CHEST, Material.DISPENSER,
  // // Material.HOPPER, Material.DROPPER, Material.BEACON,
  // // Material.BREWING_STAND };
  // for (Material m : replaceable)
  // NOT_PROTECTABLE.add(m);
  // for (Material m : gravity)
  // NOT_PROTECTABLE.add(m);
  // for (Material m : spontain)
  // NOT_PROTECTABLE.add(m);
  // for (Material m : multiBlock)
  // NOT_PROTECTABLE.add(m);
  // NOT_PROTECTABLE.addAll(COCOA);
  // NOT_PROTECTABLE.addAll(TORCHES_AND_BUTTONS);
  // NOT_PROTECTABLE.addAll(HANGING);
  // NOT_PROTECTABLE.addAll(STANDING);
  // NOT_PROTECTABLE.addAll(TRAPDOORS);
  // }

  private static Map<UUID, ProtectionManager> INSTANCES = new HashMap<UUID, ProtectionManager>();

  /**
   * Gibt eine unmodifiable Collection aller aktiven GSManager zurück.
   *
   * @return eine Liste aller aktiven GSManager
   */
  public static Collection<ProtectionManager> getActiveInstances() {
    return Collections.unmodifiableCollection(INSTANCES.values());
  }

  public static ProtectionManager getProtectionManager(World world) {
    if (world == null) {
      throw new IllegalArgumentException("world must not be null");
    }
    UUID uuid = world.getUID();
    ProtectionManager instance = ProtectionManager.INSTANCES.get(uuid);
    if (instance != null) {
      return instance;
    }
    return new ProtectionManager(world);
  }

  public static boolean isBlockTypeProtectable(Material type) {
    return !(MaterialUtils.hasGravity(type) || MaterialUtils.isReplaceable(type)
        || MaterialUtils.isSpontain(type) || MaterialUtils.isMultiblock(type)
        || MaterialUtils.isDependened(type));
  }

  /**
   * Gibt den Protector der Dependency des angegebenen Blocks zurück.
   *
   * Gibt true zurück, wenn der Protector gesetzt wurde, ansonsten false.<br>
   * <br>
   *
   * @param block
   * @param protector
   * @return Den Erfolg
   */
  public static OfflinePlayer getBlockProtector(Block block) {
    if (block == null) {
      throw new IllegalArgumentException("block must not be null");
    }
    block = MaterialUtils.getBlockDependencyRecursively(block);
    ProtectionManager pm = ProtectionManager.getProtectionManager(block.getWorld());
    // Kein LoadChunkEvent für SpawnChunks
    pm.loadChunkProtection(block.getChunk());
    if (!isBlockTypeProtectable(block.getType()))
      return null;
    BlockProtectorMetadata blockProtectorMetadata = pm.getBlockProtectorMetadata(block);
    if (blockProtectorMetadata == null) {
      return null;
    }
    return blockProtectorMetadata.value();

  }

  /**
   * Setzt den Protector der Dependency des angegebenen Blocks, falls:<br>
   * <ul>
   * <li>Der Type der Dependency protected werden kann
   * <li>Die Dependency noch nicht protected wurde
   * </ul>
   *
   * Gibt true zurück, wenn der Protector gesetzt wurde, ansonsten false.<br>
   * <br>
   *
   * @param block
   * @param protector
   * @return Den Erfolg
   */
  public static boolean setBlockProtector(Block block, OfflinePlayer protector) {
    if (block == null) {
      throw new IllegalArgumentException("block must not be null");
    }
    if (protector == null) {
      throw new IllegalArgumentException("protector must not be null");
    }
    block = MaterialUtils.getBlockDependencyRecursively(block);
    ProtectionManager pm = ProtectionManager.getProtectionManager(block.getWorld());
    // Kein LoadChunkEvent für SpawnChunks
    pm.loadChunkProtection(block.getChunk());
    if (!isBlockTypeProtectable(block.getType()))
      return false;
    if (getBlockProtector(block) != null)
      return false;
    // saveChunkProtection(block, protector);
    pm.setBlockMetaData(block, protector);

    // Player player = protector.getPlayer();
    // if (player != null) {
    // MinecraftUtils.sendMessage(player, ChatColor.DARK_PURPLE
    // + "Dieser Block wird nun von dir geschützt");
    // }
    return true;
  }

  /**
   * Löscht den Protector der Dependency des angegebenen Blocks.
   *
   * @param block
   */
  public static void removeBlockProtection(Block block) {
    if (block == null) {
      throw new IllegalArgumentException("block must not be null");
    }
    block = MaterialUtils.getBlockDependencyRecursively(block);
    ProtectionManager pm = ProtectionManager.getProtectionManager(block.getWorld());
    Chunk chunk = block.getChunk();
    // Kein LoadChunkEvent für SpawnChunks
    pm.loadChunkProtection(chunk);
    pm.removeBlockMetaData(block);
  }

  private static File getWorldsDir() {
    File worldsDir = new File(MagicProtectionPlugin.instance().getDataFolder(), "worlds");
    worldsDir.mkdirs();
    return worldsDir;
  }

  private static File getWorldDir(World world) {
    File worldDir = new File(getWorldsDir(), world.getName());
    worldDir.mkdirs();
    return worldDir;
  }

  private static File getChunkFile(Chunk chunk) {
    return new File(getWorldDir(chunk.getWorld()),
        ProtectionUtils.getNameOfChunk(chunk) + ".protection");
  }

  private final World world;

  private ProtectionManager(World world) {
    this.world = world;
    UUID uuid = world.getUID();
    String message = String.format(
        "Neuer ProtectionManager für Welt '%s' mit UUID '%s' initialisiert", world.getName(), uuid);
    logger().info(message);
    ProtectionManager.INSTANCES.put(uuid, this);
  }

  private final Map<String, List<Block>> loadedChunks = new HashMap<String, List<Block>>();

  public static void saveAll() {
    for (ProtectionManager protectionManager : getActiveInstances()) {
      protectionManager.save();
    }
  }

  public void save() {
    for (String nameOfChunk : loadedChunks.keySet()) {
      Chunk chunk = ProtectionUtils.getChunkOfName(world, nameOfChunk);
      saveChunkProtection(chunk);
    }
  }

  public void unload() {
    save();
    loadedChunks.clear();
  }

  public void unloadChunk(Chunk chunk) {
    if (chunk == null) {
      throw new IllegalArgumentException("chunk must not be null");
    }
    saveChunkProtection(chunk);
    String nameOfChunk = ProtectionUtils.getNameOfChunk(chunk);
    loadedChunks.remove(nameOfChunk);
  }

  /**
   * Lädt die Chunk Protection, falls diese noch nicht geladen wurde.
   *
   * @param chunk
   */
  public void loadChunkProtection(Chunk chunk) {
    if (chunk == null) {
      throw new IllegalArgumentException("chunk must not be null");
    }
    String nameOfChunk = ProtectionUtils.getNameOfChunk(chunk);
    List<Block> blocks = loadedChunks.get(nameOfChunk);
    if (blocks != null) {
      return;
    }
    loadedChunks.put(nameOfChunk, new ArrayList<Block>());
    File chunkFile = getChunkFile(chunk);
    // try {
    // chunkFile.createNewFile();
    // } catch (IOException ex) {
    // String message = String.format(
    // "IOException beim Erzeugen der Datei für den Chunk '%s'",
    // nameOfChunk);
    // throw new PluginException(511, message, ex);
    // }
    if (chunkFile.exists()) {
      try {
        byte[] bytes = Files.readAllBytes(chunkFile.toPath());
        ByteBuffer bb = ByteBuffer.allocate(bytes.length);
        bb.put(bytes);
        bb.rewind();
        while (bb.remaining() >= 18) {
          // Ein Short hat 2 Bytes, ein Long 8 -> 18
          short idOfBlock = bb.getShort();
          Block block = ProtectionUtils.getBlockOfId(chunk, idOfBlock);
          long mostSigBits = bb.getLong();
          long leastSigBits = bb.getLong();
          UUID id = new UUID(mostSigBits, leastSigBits);
          OfflinePlayer protector = Bukkit.getOfflinePlayer(id);
          if (protector == null) {
            String format =
                "Spieler mit der UUID '%s' konnte nicht gefunden werden. Lösche Protection vom Block %s";
            String message = String.format(format, id, block);
            logger().warn(message);
          } else {
            setBlockMetaData(block, protector);
          }
        }
      } catch (IOException e) {
        String message = String.format("IOException beim Laden der Chunkprotection für Chunk: '%s'",
            chunkFile.getName());
        throw new PluginException(501, message, e);
      }
    }
    setDirty(chunk, false);
  }

  /**
   * Überschreibt die Chunk Protection für den Chunk nur dann, wenn die Chunk Protection bereits
   * geladen war und als dirty markiert ist.<br>
   * Ansonsten gibt es schließlich nichts zu speichern.
   *
   * @param chunk
   */
  public void saveChunkProtection(Chunk chunk) {
    if (chunk == null) {
      throw new IllegalArgumentException("chunk must not be null");
    }
    if (!isDirty(chunk)) {
      return;
    }
    setDirty(chunk, false);
    String nameOfChunk = ProtectionUtils.getNameOfChunk(chunk);
    List<Block> blocks = loadedChunks.get(nameOfChunk);
    if (blocks == null) {
      // Chunk war noch nicht geladen
      return;
    }
    String format = "Saving Protection of Chunk %s in World %s";
    String message = String.format(format, nameOfChunk, world.getName());
    logger().info(message);
    File chunkFile = getChunkFile(chunk);
    if (blocks.isEmpty()) {
      boolean success = chunkFile.delete();
      if (success) {
        return;
      }
    }
    try {
      ByteBuffer bb = ByteBuffer.allocate(blocks.size() * 18);
      // Ein Short hat 2 Bytes, ein Long 8 -> 18
      for (Block block : blocks) {
        short idOfBlock = ProtectionUtils.getIdOfBlock(block);
        OfflinePlayer protector = getBlockProtectorMetadata(block).value();
        UUID id = protector.getUniqueId();
        bb.putShort(idOfBlock);
        bb.putLong(id.getMostSignificantBits());
        bb.putLong(id.getLeastSignificantBits());
      }
      Files.write(chunkFile.toPath(), bb.array());
    } catch (IOException ex) {
      String exMessage =
          String.format("IOException beim Speichern der Protection des Chunks '%s'", nameOfChunk);
      throw new PluginException(510, exMessage, ex);
    }
  }

  private static final String PROTECTOR = "protector";

  private BlockProtectorMetadata getBlockProtectorMetadata(Block block) {
    List<MetadataValue> metadatas = block.getMetadata(PROTECTOR);
    for (MetadataValue metadata : metadatas) {
      if (JavaPlugin.getPlugin(MagicProtectionPlugin.class).equals(metadata.getOwningPlugin())) {
        if (metadata instanceof BlockProtectorMetadata) {
          return (BlockProtectorMetadata) metadata;
        }
      }
    }
    return null;
  }

  private void setBlockMetaData(Block block, OfflinePlayer protector) {
    block.setMetadata(PROTECTOR,
        new BlockProtectorMetadata(JavaPlugin.getPlugin(MagicProtectionPlugin.class), protector));
    String chunkName = ProtectionUtils.getNameOfChunk(block.getChunk());
    List<Block> blocks = loadedChunks.get(chunkName);
    if (blocks == null) {
      blocks = new ArrayList<Block>();
      blocks.add(block);
      loadedChunks.put(chunkName, blocks);
    } else {
      blocks.add(block);
      loadedChunks.put(chunkName, blocks);
    }
    setDirty(block.getChunk(), true);
  }

  private void removeBlockMetaData(Block block) {
    block.removeMetadata(PROTECTOR, JavaPlugin.getPlugin(MagicProtectionPlugin.class));
    String chunkName = ProtectionUtils.getNameOfChunk(block.getChunk());
    List<Block> blocks = loadedChunks.get(chunkName);
    if (blocks == null) {
      blocks = new ArrayList<Block>();
      loadedChunks.put(chunkName, blocks);
    } else {
      logger().fine("Removing Protection for: " + block);
      blocks.remove(block);
      loadedChunks.put(chunkName, blocks);
    }
    setDirty(block.getChunk(), true);
  }

  private final Set<String> dirtyChunks = new HashSet<String>();

  private static boolean setDirty(Chunk chunk, boolean dirty) {
    ProtectionManager pm = getProtectionManager(chunk.getWorld());
    String nameOfChunk = ProtectionUtils.getNameOfChunk(chunk);
    boolean contained = pm.dirtyChunks.contains(nameOfChunk);
    if (dirty) {
      pm.dirtyChunks.add(nameOfChunk);
      return !contained;
    } else {
      pm.dirtyChunks.remove(nameOfChunk);
      return contained;
    }
  }

  private static boolean isDirty(Chunk chunk) {
    ProtectionManager pm = getProtectionManager(chunk.getWorld());
    String nameOfChunk = ProtectionUtils.getNameOfChunk(chunk);
    return pm.dirtyChunks.contains(nameOfChunk);
  }

}
