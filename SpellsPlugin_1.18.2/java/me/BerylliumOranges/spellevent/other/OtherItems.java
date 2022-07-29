package me.BerylliumOranges.spellevent.other;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.BerylliumOranges.spellevent.processes.EventItemStorage;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;

public class OtherItems {
	public static File[] triggerFiles = sortAlphabetically((new File("plugins/Basics/event")).listFiles());

	public static final int MANA_POTATO_MANA = 175;

	public static ItemStack manaPotato() {
		ItemStack item = new ItemStack(Material.POISONOUS_POTATO);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.AQUA + "Poisonous Mana Potato");
		meta.setLore(Arrays.asList(ChatColor.WHITE + "+" + MANA_POTATO_MANA + " Mana"));
		meta.setLocalizedName(meta.getDisplayName());
		meta.setCustomModelData(1);
		item.setItemMeta(meta);
		return item;
	}

	public static final int MANA_BERRIES_MANA = 75;

	public static ItemStack manaBerries() {
		ItemStack item = new ItemStack(Material.GLOW_BERRIES);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.AQUA + "Watery Mana Berries");
		meta.setLore(Arrays.asList(ChatColor.WHITE + "+" + MANA_BERRIES_MANA + " Mana"));
		meta.setLocalizedName(meta.getDisplayName());
		meta.setCustomModelData(1);
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack singleUpgrade() {
		ItemStack item = new ItemStack(Material.GOLD_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.GRAY + "Single Upgrade");
		meta.setLore(Arrays.asList(ChatColor.GOLD + "Improves " + org.bukkit.ChatColor.WHITE + "adjacent segments"));
		meta.setLocalizedName(EventSegment.SEGMENT_TYPE_UPGRADE + " [" + meta.getDisplayName() + "]");
		meta.setCustomModelData(2);
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack doubleUpgrade() {
		ItemStack item = new ItemStack(Material.GOLD_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.BLUE + "Double Upgrade");
		meta.setLore(Arrays.asList(ChatColor.GOLD + "Improves " + org.bukkit.ChatColor.WHITE + "adjacent segments"));
		meta.setLocalizedName(EventSegment.SEGMENT_TYPE_UPGRADE + " [" + meta.getDisplayName() + "]");
		meta.setCustomModelData(1);
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack quadrupleUpgrade() {
		ItemStack item = new ItemStack(Material.GOLD_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.RED + "Quadruple Upgrade");
		meta.setLore(Arrays.asList(ChatColor.GOLD + "Improves " + org.bukkit.ChatColor.WHITE + "adjacent segments"));
		meta.setLocalizedName(EventSegment.SEGMENT_TYPE_UPGRADE + " [" + meta.getDisplayName() + "]");
		meta.setCustomModelData(3);
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack octupleUpgrade() {
		ItemStack item = new ItemStack(Material.GOLD_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD + "Octuple Upgrade " + ChatColor.GOLD + "[" + ChatColor.YELLOW
				+ "Creative Only" + ChatColor.GOLD + "]");
		meta.setLore(Arrays.asList(ChatColor.GOLD + "Improves " + org.bukkit.ChatColor.WHITE + "adjacent segments"));
		meta.setLocalizedName(EventSegment.SEGMENT_TYPE_UPGRADE + " [" + meta.getDisplayName() + "]");
		meta.setCustomModelData(3);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(meta);

		item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);

		return item;
	}

	public static ItemStack triggerIndicator() {
		ItemStack item = new ItemStack(Material.ORANGE_STAINED_GLASS_PANE);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.YELLOW + "Indicator");

		meta.setLore(Arrays.asList(ChatColor.WHITE + "Segments placed here will be triggered"));
		meta.setLocalizedName(EventSegment.INDICATOR + " [" + meta.getDisplayName() + "] [0]");
		meta.setCustomModelData(0);
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack equipIndicator() {
		ItemStack item = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.BLUE + "Indicator");

		meta.setLore(Arrays.asList(ChatColor.WHITE + "Items placed here will be equipped"));
		meta.setLocalizedName(EventSegment.INDICATOR + " [" + meta.getDisplayName() + "] [1]");
		meta.setCustomModelData(0);
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack lockedIndicator() {
		ItemStack item = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.WHITE + "Locked");
		meta.setLocalizedName(EventSegment.LOCKED_INDICATOR + " [" + meta.getDisplayName() + "]");
		// meta.setCustomModelData(0);
		item.setItemMeta(meta);
		return item;
	}

	public static String getMobItemInventoryInfo(String mobName) {
		return EventItemStorage.MOB_INVENTORY_TAG + "[" + mobName + "] [MOB]";
	}

	public static String getMobItemInfo(String itemName) {
		return EventItemStorage.MOB_ITEM_TAG + "[" + itemName + "] [" + RandomStringUtils.random(4, true, true) + "]";
	}

	public static String getRealTitle(InventoryView v) {

		if (v.getTitle().startsWith(ChatColor.GRAY + "Procedure")) {
			return v.getTitle().substring((ChatColor.GRAY + "Procedure").length()).trim();

		}
		return v.getTitle();
	}

	public static boolean isProcedureInventory(InventoryView v) {
		return getRealTitle(v).startsWith("EventItem")
				|| getRealTitle(v).startsWith(EventItemStorage.MOB_INVENTORY_TAG);
	}

	public static String intToArmorstring(int i) {
		switch (i) {
		case 1:
			return "HELMET";
		case 2:
			return "CHESTPLATE";
		case 3:
			return "LEGGINGS";
		default:
			return "BOOTS";
		}
	}

	public static int getGenericArmorValue(int armorPiece, int armorMat) {
		switch (armorMat) {
		case 1:// iron
			switch (armorPiece) {
			case 1:
				return 2;
			case 2:
				return 6;
			case 3:
				return 5;
			default:
				return 2;
			}
		case 2:// diamond
		case 3:// netherite
			switch (armorPiece) {
			case 1:
				return 3;
			case 2:
				return 8;
			case 3:
				return 6;
			default:
				return 3;
			}
		default:// leather
			switch (armorPiece) {
			case 1:
				return 1;
			case 2:
				return 3;
			case 3:
				return 2;
			default:
				return 1;
			}
		}
	}

	public static EquipmentSlot intToEquipmentSlot(int i) {
		switch (i) {
		case 1:
			return EquipmentSlot.HEAD;
		case 2:
			return EquipmentSlot.CHEST;
		case 3:
			return EquipmentSlot.LEGS;
		default:
			return EquipmentSlot.FEET;
		}
	}

	public static File[] sortAlphabetically(File[] input) {
		List<File> list = new LinkedList<File>();
		Collections.sort(list, new Comparator<File>() {
			@Override
			public int compare(File arg0, File arg1) {
				return arg0.getName().compareTo(arg1.getName());
			}
		});
		return input;
	}
}
