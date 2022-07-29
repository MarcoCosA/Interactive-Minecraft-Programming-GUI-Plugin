package me.BerylliumOranges.spellevent.segments.f_unfair;

import java.util.Arrays;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.BerylliumOranges.spellevent.other.Guideline;
import me.BerylliumOranges.spellevent.processes.SegmentProcess;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;
import me.BerylliumOranges.spellevent.segments.abstracts.EventTriggerSegment;

public class RandomTrigger extends EventSegment {
	public static final String THIS_SEGMENT_TYPE = SEGMENT_TYPE_TRIGGER;
	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 1;
	public static final int RARITY =5;

	public static Guideline processSegment(SegmentProcess proc) {
		if (proc.subtractMana(MANA_COST) < 1)
			return new Guideline(proc, true, true);
		Class<EventTriggerSegment> clazz = EventTriggerSegment.ALL_TRIGGER_SEGMENTS
				.get((int) (new Random(proc.getSeed()).nextDouble() * EventTriggerSegment.ALL_TRIGGER_SEGMENTS.size()));
		try {
			proc.setLastCast(ChatColor.YELLOW + "[" + ChatColor.GOLD
					+ ChatColor
							.stripColor(((ItemStack) clazz.getField("ITEM").get(null)).getItemMeta().getDisplayName())
					+ ChatColor.YELLOW + "]");
			((Guideline) clazz.getMethod("processSegment", SegmentProcess.class).invoke(null, proc)).getProcess();

		} catch (Exception er) {
			er.printStackTrace();
		}
		return new Guideline(proc, false, false);
	}
	

	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.RED + "Random Trigger");
		meta.setLore(Arrays.asList(ChatColor.WHITE + "Casts a random " + ChatColor.YELLOW + "trigger segment"));
		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_UNFAIR);
		item.setItemMeta(meta);
		return item;
	}
}
