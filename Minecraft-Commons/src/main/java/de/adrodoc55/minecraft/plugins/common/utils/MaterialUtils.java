package de.adrodoc55.minecraft.plugins.common.utils;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import de.adrodoc55.minecraft.plugins.common.PluginException;

public class MaterialUtils {

	public static boolean hasGravity(Material material) {
		switch (material) {
		case ANVIL:
		case DRAGON_EGG:
		case GRAVEL:
		case SAND:
			return true;
		default:
			return false;
		}
	}

	public static boolean isReplaceable(Material material) {
		switch (material) {
		case AIR:
		case DEAD_BUSH:
		case FIRE:
		case LAVA:
		case LONG_GRASS:
		case SNOW:
		case STATIONARY_LAVA:
		case STATIONARY_WATER:
		case VINE:
		case WATER:
		case WATER_LILY:
			return true;
		default:
			return false;
		}
	}

	public static boolean isSpontain(Material material) {
		switch (material) {
		case SAPLING:
		case TNT:
			return true;
		default:
			return false;
		}
	}

	public static boolean hasInventory(Material material) {
		switch (material) {
		case BEACON:
		case BREWING_STAND:
		case CHEST:
		case DISPENSER:
		case DROPPER:
		case FURNACE:
		case HOPPER:
		case JUKEBOX:
		case TRAPPED_CHEST:
			return true;
		default:
			return false;
		}
	}

	public static boolean isMultiblock(Material material) {
		if (isDoor(material)) {
			return true;
		}
		switch (material) {
		case BED:
			return true;
		default:
			return false;
		}
	}

	public static boolean isDoor(Material material) {
		switch (material) {
		case ACACIA_DOOR:
		case BIRCH_DOOR:
		case DARK_OAK_DOOR:
		case IRON_DOOR_BLOCK:
		case JUNGLE_DOOR:
		case SPRUCE_DOOR:
		case WOODEN_DOOR:
			return true;
		default:
			return false;
		}
	}

	public static boolean isTrapDoor(Material material) {
		switch (material) {
		case IRON_TRAPDOOR:
		case TRAP_DOOR:
			return true;
		default:
			return false;
		}
	}

	public static boolean isFenceGate(Material material) {
		switch (material) {
		case ACACIA_FENCE_GATE:
		case BIRCH_FENCE_GATE:
		case DARK_OAK_FENCE_GATE:
		case FENCE_GATE:
		case JUNGLE_FENCE_GATE:
		case SPRUCE_FENCE_GATE:
			return true;
		default:
			return false;
		}
	}

	public static boolean isRail(Material material) {
		switch (material) {
		case ACTIVATOR_RAIL:
		case DETECTOR_RAIL:
		case POWERED_RAIL:
		case RAILS:
			return true;
		default:
			return false;
		}
	}

	public static boolean isStanding(Material material) {
		if (isDoor(material)) {
			return true;
		}
		if (isRail(material)) {
			return true;
		}
		switch (material) {
		case BROWN_MUSHROOM:
		case CACTUS:
		case CAKE_BLOCK:
		case CARPET:
		case CARROT:
		case CROPS:
		case DEAD_BUSH:
		case DIODE_BLOCK_OFF:
		case DIODE_BLOCK_ON:
		case DOUBLE_PLANT:
		case FIRE:
		case FLOWER_POT:
		case GOLD_PLATE:
		case IRON_PLATE:
		case LONG_GRASS:
		case MELON_STEM:
		case POTATO:
		case PUMPKIN_STEM:
		case RED_MUSHROOM:
		case RED_ROSE:
		case REDSTONE_COMPARATOR_OFF:
		case REDSTONE_COMPARATOR_ON:
		case REDSTONE_WIRE:
		case SIGN_POST:
		case SNOW:
		case STANDING_BANNER:
		case STONE_PLATE:
		case SUGAR_CANE_BLOCK:
		case WATER_LILY:
		case WOOD_PLATE:
		case YELLOW_FLOWER:
			return true;
		default:
			return false;
		}
	}

	public static boolean isFourDirectional(Material material) {
		switch (material) {
		case BANNER:
		case LADDER:
		case TRIPWIRE_HOOK:
		case VINE:
		case WALL_SIGN:
			return true;
		default:
			return false;
		}
	}

	public static boolean isSixDirectional(Material material) {
		switch (material) {
		case LEVER:
		case REDSTONE_TORCH_OFF:
		case REDSTONE_TORCH_ON:
		case STONE_BUTTON:
		case TORCH:
		case WOOD_BUTTON:
			return true;
		default:
			return false;
		}
	}

	public static boolean isEightDirectional(Material material) {
		return isTrapDoor(material);
	}

	public static boolean isTwelveDirectional(Material material) {
		switch (material) {
		case COCOA:
			return true;
		default:
			return false;
		}
	}

	public static boolean isDependened(Material material) {
		return isStanding(material) || isFourDirectional(material)
				|| isSixDirectional(material) || isEightDirectional(material)
				|| isTwelveDirectional(material);
	}

	public static boolean isLockable(Material material) {
		return hasInventory(material) || isDoor(material)
				|| isTrapDoor(material);
	}

	public static Block getBlockDependencyRecursively(Block block) {
		Block dependency = getBlockDependency(block);
		if (dependency.equals(block)) {
			return dependency;
		} else {
			return getBlockDependencyRecursively(dependency);
		}
	}

	private static Block getBlockDependency(Block block) {
		Material type = block.getType();
		@SuppressWarnings("deprecation")
		byte data = block.getData();
		if (MaterialUtils.isStanding(type)) {
			return block.getRelative(BlockFace.DOWN);
		} else if (MaterialUtils.isFourDirectional(type)) {
			switch (data) {
			case 2:
				return block.getRelative(BlockFace.SOUTH);
			case 3:
				return block.getRelative(BlockFace.NORTH);
			case 4:
				return block.getRelative(BlockFace.EAST);
			case 5:
				return block.getRelative(BlockFace.WEST);
			}
		} else if (MaterialUtils.isSixDirectional(type)) {
			switch (data) {
			case 0:
				return block.getRelative(BlockFace.UP);
			case 1:
				return block.getRelative(BlockFace.WEST);
			case 2:
				return block.getRelative(BlockFace.EAST);
			case 3:
				return block.getRelative(BlockFace.NORTH);
			case 4:
				return block.getRelative(BlockFace.SOUTH);
			case 5:
				return block.getRelative(BlockFace.DOWN);
			}
		} else if (MaterialUtils.isEightDirectional(type)) {
			switch (data % 4) {
			case 0:
				return block.getRelative(BlockFace.SOUTH);
			case 1:
				return block.getRelative(BlockFace.NORTH);
			case 2:
				return block.getRelative(BlockFace.EAST);
			case 3:
				return block.getRelative(BlockFace.WEST);
			}
		} else if (MaterialUtils.isTwelveDirectional(type)) {
			switch (data % 4) {
			case 0:
				return block.getRelative(BlockFace.SOUTH);
			case 1:
				return block.getRelative(BlockFace.WEST);
			case 2:
				return block.getRelative(BlockFace.NORTH);
			case 3:
				return block.getRelative(BlockFace.EAST);
			}
		} else {
			return block;
		}
		String message = String.format(
				"Invalide Block-Data eines hängenden Blocks: Block = '%s'",
				block);
		throw new PluginException(508, message);
	}

}
