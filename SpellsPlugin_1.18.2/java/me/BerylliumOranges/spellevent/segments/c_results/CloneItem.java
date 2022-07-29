package me.BerylliumOranges.spellevent.segments.c_results;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import me.BerylliumOranges.spellevent.other.Guideline;
import me.BerylliumOranges.spellevent.processes.SegmentProcess;
import me.BerylliumOranges.spellevent.processes.SpellProcess;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;

public class CloneItem extends EventSegment {
	public static final String THIS_SEGMENT_TYPE = SEGMENT_TYPE_RESULT;
	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 500;
	public static final double ITEM_MANA_COST = 30;
	public static final int ORIGINAL_AMPLIFICATION = 0;
	public static final int RARITY = 4;

	public static final boolean CONSUME_POT = false;

	public static Guideline processSegment(SegmentProcess proc) {
		int[] upgrades = getUpgrades(proc);
		if (proc.subtractMana(MANA_COST, false) < 1 || proc.addSegmentCooldown(CloneItem.class, (int) ITEM_MANA_COST)
				|| proc.subtractMana(MANA_COST) < 1)
			return new Guideline(proc, true, true);
		if (proc.getY() < proc.getTimesProced().length) {
			ItemStack item = proc.getPresentSegments()[proc.getY() + 1][proc.getX()];
			if (item != null && item.hasItemMeta() && !(item instanceof Damageable)) {
				ItemStack itemClone = item.clone();
				itemClone.setAmount(1);
				int amp = getAmplification(upgrades);
				for (int i = 0; i < amp; i++) {
					proc.getSpellLocation().getWorld().dropItem(proc.getSpellLocation(), itemClone);

					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(SpellProcess.plugin, new Runnable() {
						public void run() {
							proc.getSpellLocation().getWorld().playSound(proc.getSpellLocation(),
									Sound.BLOCK_NOTE_BLOCK_BELL, 1F, 1.0F);
						}
					}, i * 5);
				}
			}
		}
		return new Guideline(proc, true, false);
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "Clone Item");
		String text = "copies";
		if (getAmplification(upgrades) == 1)
			text = "copy";
		ArrayList<String> lore = new ArrayList<String>(
				Arrays.asList(ChatColor.WHITE + "Drops " + ChatColor.RED + getAmplification(upgrades) + " " + text + " "
						+ ChatColor.WHITE + "of the item", ChatColor.WHITE + "under this segment."));
		lore.addAll(
				Arrays.asList(ChatColor.DARK_GRAY + "", getCooldownLore((int) ITEM_MANA_COST), getCostText(MANA_COST)));
		meta.setLore(lore);
		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_RESULT);
		item.setItemMeta(meta);
		return item;
	}

	public static int getAmplification(int upgrades[]) {
		return ORIGINAL_AMPLIFICATION + (upgrades[0] + upgrades[1]) / (EventSegment.CRIT_MULTIPLIER * 5);
	}
}
