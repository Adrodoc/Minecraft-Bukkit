package de.adrodoc55.minecraft.plugins.magic_protection.protection;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;

public class ProtectionUtils {

	public static short getIdOfBlock(Block block) {
		if (block == null) {
			throw new IllegalArgumentException("block must not be null");
		}
		int x = block.getX() % 16;
		int z = block.getZ() % 16;
		if (x < 0)
			x += 16;
		if (z < 0)
			z += 16;
		int id = x + z * 16 + block.getY() * 256;
		if (id > Short.MAX_VALUE || id < 0) {
			String message = String
					.format("Die Id eines Blocks muss in einen Short passen, war aber '%d'! Möglicherweise ist die Welt höher als 128 Blöcke und die Protection Kodierung muss angepasst werden.",
							id);
			throw new InternalError(message);
		}
		short shortId = (short) id;
		return shortId;
	}

	public static Block getBlockOfId(Chunk chunk, int id) {
		if (chunk == null) {
			throw new IllegalArgumentException("chunk must not be null");
		}
		int x = id % 16;
		id = (id - x) / 16;
		int z = id % 16;
		id = (id - z) / 16;
		int y = id;
		return chunk.getBlock(x, y, z);
	}

	private static final String CHUNK_NAME_SEPARATOR = ";";

	public static String getNameOfChunk(Chunk chunk) {
		if (chunk == null) {
			throw new IllegalArgumentException("chunk must not be null");
		}
		String nameOfChunk = String.valueOf(chunk.getX())
				+ CHUNK_NAME_SEPARATOR + String.valueOf(chunk.getZ());
		return nameOfChunk;
	}

	public static Chunk getChunkOfName(World world, String chunkName) {
		if (world == null) {
			throw new IllegalArgumentException("world must not be null");
		}
		if (chunkName == null) {
			throw new IllegalArgumentException("chunkName must not be null");
		}
		try {
			String[] xz = chunkName.split(CHUNK_NAME_SEPARATOR, 2);
			int x = Integer.parseInt(xz[0]);
			int z = Integer.parseInt(xz[1]);
			Chunk chunk = world.getChunkAt(x, z);
			return chunk;
		} catch (IndexOutOfBoundsException | NumberFormatException e) {
			throw new IllegalChunkNameException(chunkName, e);
		}
	}

}
