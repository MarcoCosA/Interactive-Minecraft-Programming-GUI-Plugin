package me.BerylliumOranges.spellevent.segments.e_logic;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.BerylliumOranges.spellevent.other.Guideline;
import me.BerylliumOranges.spellevent.processes.SegmentProcess;
import me.BerylliumOranges.spellevent.processes.SpellActions;
import me.BerylliumOranges.spellevent.processes.SpellProcess;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;
import net.md_5.bungee.api.ChatColor;

public class PreciseDelay extends EventSegment {
	public static final String THIS_SEGMENT_TYPE = SEGMENT_TYPE_LOGIC;
	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 1;
	public static final double DELAY_LENGTH = 0.05;
	public static final int RARITY = 0;

	public static Guideline processSegment(SegmentProcess proc) {
		int[] upgrades = getItemUpgrades(proc);
		if (proc.subtractMana(MANA_COST) < 1)
			return new Guideline(proc, true, true);
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(SpellProcess.plugin, new Runnable() {
			public void run() {
				findAndCallNext(proc);
			}
		}, (long) (getDuration(upgrades) * 20));
		return new Guideline(proc, false, false);
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "Precise Delay");
		meta.setLore(
				Arrays.asList(ChatColor.WHITE + "Delays following segments ",
						ChatColor.WHITE + "by " + ChatColor.GRAY + Math.floor(getDuration(upgrades) * 100.0) / 100.0 + ChatColor.WHITE + " seconds "
								+ ChatColor.GRAY + SpellActions.getArrowModelFromInt(dir),
						ChatColor.DARK_GRAY + "Does not crit", getCostText(MANA_COST)));
		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_LOGIC);
		item.setItemMeta(meta);
		return item;
	}

	public static double getDuration(int upgrades[]) {
		return DELAY_LENGTH + (upgrades[0] + upgrades[1]) / 20.0;
	}
}
