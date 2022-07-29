package me.BerylliumOranges.spellevent.segments.g_spell_focus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.BerylliumOranges.main.SpellPluginMain;
import me.BerylliumOranges.misc.GachaItems;
import me.BerylliumOranges.spellevent.other.Guideline;
import me.BerylliumOranges.spellevent.processes.EventItemStorage;
import me.BerylliumOranges.spellevent.processes.SegmentProcess;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;

public class ScrollCopy extends EventSegment {
	public static final String THIS_SEGMENT_TYPE = SEGMENT_TYPE_RESULT;
	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 55;
	public static final int RARITY = 4;

	public static Guideline processSegment(SegmentProcess proc) {
		int[] upgrades = EventSegment.getUpgrades(proc);

		for (SegmentProcess focus : proc.getFocusedSpellsCopy()) {
			if (proc.subtractMana(MANA_COST) < 1)
				return new Guideline(proc, true, false);
			String localName = "";
			ItemStack container = focus.getContainer();
			for (Map.Entry<ItemStack, HashMap<Integer, ItemStack>> entry : focus.getMySpellProcess()
					.getLoadedSegments()) {
				container.getItemMeta().getDisplayName();
				if (entry.getKey().getItemMeta().getLocalizedName()
						.equals(container.getItemMeta().getLocalizedName())) {
					localName = GachaItems.SPELL_ITEM_TAG + GachaItems.Rarity.COMMON.text + "[null]["
							+ GachaItems.getRandom4Digits() + "]";
					HashMap<Integer, ItemStack> copy = (HashMap<Integer, ItemStack>) entry.getValue().clone();

					for (Map.Entry<Integer, ItemStack> e : copy.entrySet()) {
						ItemStack item = e.getValue();
						if (item != null && item.hasItemMeta()) {
							ItemMeta meta = item.getItemMeta();
							meta.setLocalizedName(
									EventSegment.changeEditOption(meta.getLocalizedName(), EditOption.NOTHING));
							item.setItemMeta(meta);
						}

						e.setValue(item);
					}

					EventItemStorage.writeStorage(copy, localName);
					break;
				}
			}
			if (localName.length() > 0) {
				ItemStack scroll = new ItemStack(Material.PAPER, getAmount(upgrades));
				ItemMeta meta = scroll.getItemMeta();
				String name = focus.getContainer().getItemMeta().getDisplayName();
				meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Copied Spell (" + ChatColor.RESET + name
						+ ChatColor.LIGHT_PURPLE + ")");
				meta.setLocalizedName(localName);
				scroll.setItemMeta(meta);

				if (proc.getCaster() instanceof InventoryHolder) {
					InventoryHolder holder = (InventoryHolder) proc.getCaster();
					holder.getInventory().addItem(scroll);
				}
			}
		}
		return new Guideline(proc, true, false);
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "Scroll Copy");
		String copy = " copies ";
		if (getAmount(upgrades) == 1)
			copy = " copy ";
		meta.setLore(Arrays.asList(
				ChatColor.WHITE + "Gives " + ChatColor.LIGHT_PURPLE + getAmount(upgrades) + ChatColor.WHITE + copy
						+ "of " + ChatColor.LIGHT_PURPLE + "focused",
				ChatColor.WHITE + "spells as scrolls", "", getCostText(MANA_COST) + ChatColor.WHITE + " per spell"));

		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_SPELL_FOCUS_RESULT);
		item.setItemMeta(meta);
		return item;
	}

	public static int getAmount(int[] upgrades) {
		return 1 + (int) ((upgrades[0] + upgrades[1]) / 4.0);
	}
}
