package me.BerylliumOranges.spellevent.segments.c_results;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.BerylliumOranges.spellevent.other.Guideline;
import me.BerylliumOranges.spellevent.processes.SegmentProcess;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;

public class Lightning extends EventSegment {
	public static final String THIS_SEGMENT_TYPE = SEGMENT_TYPE_RESULT;
	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 90;
	public static final int RARITY = 0;

	public static Guideline processSegment(SegmentProcess proc) {
		if (proc.subtractMana(MANA_COST) < 1)
			return new Guideline(proc, true, true);
		proc.getSpellLocation().getWorld().strikeLightning(proc.getSpellLocation());
		if (proc.getSpellLocation().clone().add(0, 1, 0).getBlock().getType().isAir()) {
			proc.getSpellLocation().clone().add(0, 1, 0).getBlock().setType(Material.FIRE);
		}
		return new Guideline(proc, true, false);
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "Strike Lightning");
		meta.setLore(Arrays.asList(ChatColor.WHITE + "Unaffected by upgrades", "", getCostText(MANA_COST)));

		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_RESULT);
		item.setItemMeta(meta);
		return item;
	}
}
