package me.BerylliumOranges.spellevent.segments.e_logic;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.BerylliumOranges.spellevent.other.Guideline;
import me.BerylliumOranges.spellevent.processes.SegmentProcess;
import me.BerylliumOranges.spellevent.processes.SpellActions;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;

public final class SimultaneousCast extends EventSegment {
	public static final String THIS_SEGMENT_TYPE = SEGMENT_TYPE_LOGIC;
	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 50;

	public static final int RARITY = 2;

	public static Guideline processSegment(SegmentProcess proc) {
		if (proc.subtractMana(MANA_COST) < 1)
			return new Guideline(proc, true, true);
		SegmentProcess clone;
		try {
			clone = (SegmentProcess) proc.clone();
			clone.setDir(1);
			findAndCallNext(clone);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return findAndCallNext(proc);
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "Simultaneous Cast");
		ArrayList<String> lore = new ArrayList<String>(
				Arrays.asList(ChatColor.WHITE + "Casts a spell starting with the",
						ChatColor.WHITE + "segments " + ChatColor.GRAY + "under " + ChatColor.WHITE + "this one", "",
						getCostText(MANA_COST) + " " + ChatColor.GRAY + SpellActions.getArrowModelFromInt(dir)));
		meta.setLore(lore);
		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_LOGIC);
		item.setItemMeta(meta);
		return item;
	}
}
