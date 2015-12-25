package de.adrodoc55.minecraft.plugins.magic_protection.magic;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

public class MagicCraftingManager implements Listener {

	public static void setServerRecipes(Server server) {
		server.clearRecipes();
		addServerRecipes(server);
	}

	public static void addServerRecipes(Server server) {

		ShapedRecipe necroticWandRecipe = new ShapedRecipe(getNewNecroticWand());
		necroticWandRecipe.shape("  I", "FB ", "RF ");
		necroticWandRecipe.setIngredient('I', Material.IRON_INGOT);
		necroticWandRecipe.setIngredient('F', Material.FEATHER);
		necroticWandRecipe.setIngredient('B', Material.BONE);
		necroticWandRecipe.setIngredient('R', Material.ROTTEN_FLESH);
		server.addRecipe(necroticWandRecipe);

		ShapedRecipe magicWandRecipe = new ShapedRecipe(getNewMagicWand());
		magicWandRecipe.shape(" LD", "LSL", "IL ");
		magicWandRecipe.setIngredient('L', Material.LEATHER);
		magicWandRecipe.setIngredient('D', Material.DIAMOND);
		magicWandRecipe.setIngredient('S', Material.STICK);
		magicWandRecipe.setIngredient('I', Material.IRON_INGOT);
		server.addRecipe(magicWandRecipe);

		ShapedRecipe masterWandRecipe = new ShapedRecipe(getNewMasterWand());
		masterWandRecipe.shape(" GN", "GBG", "DG ");
		masterWandRecipe.setIngredient('G', Material.GOLD_INGOT);
		masterWandRecipe.setIngredient('N', Material.NETHER_STAR);
		masterWandRecipe.setIngredient('B', Material.BLAZE_ROD);
		masterWandRecipe.setIngredient('D', Material.DIAMOND);
		server.addRecipe(masterWandRecipe);

	}

	public static ItemStack getNewNecroticWand() {
		ItemStack necroticWand = new ItemStack(Material.BONE);
		ItemMeta itemMeta = necroticWand.getItemMeta();
		itemMeta.setDisplayName("Necrotic Wand");
		ArrayList<String> lore = new ArrayList<String>(2);
		lore.add("How does this Work?");
		lore.add("Magic!");
		itemMeta.setLore(lore);
		necroticWand.setItemMeta(itemMeta);
		necroticWand.addUnsafeEnchantment(new EnchantmentWrapper(17), 1);
		return necroticWand;
	}

	public static ItemStack getNewMagicWand() {
		ItemStack magicWand = new ItemStack(Material.STICK);
		ItemMeta itemMeta = magicWand.getItemMeta();
		itemMeta.setDisplayName("Magic Wand");
		ArrayList<String> lore = new ArrayList<String>(1);
		lore.add("Level up!");
		itemMeta.setLore(lore);
		magicWand.setItemMeta(itemMeta);
		magicWand.addUnsafeEnchantment(new EnchantmentWrapper(34), 3);
		return magicWand;
	}

	public static ItemStack getNewMasterWand() {
		ItemStack masterWand = new ItemStack(Material.BLAZE_ROD);
		ItemMeta itemMeta = masterWand.getItemMeta();
		itemMeta.setDisplayName("Master Wand");
		ArrayList<String> lore = new ArrayList<String>(1);
		lore.add("Infinite BURN!");
		itemMeta.setLore(lore);
		masterWand.setItemMeta(itemMeta);
		masterWand.addUnsafeEnchantment(new EnchantmentWrapper(51), 1);
		masterWand.addUnsafeEnchantment(new EnchantmentWrapper(20), 1);
		return masterWand;
	}

}
