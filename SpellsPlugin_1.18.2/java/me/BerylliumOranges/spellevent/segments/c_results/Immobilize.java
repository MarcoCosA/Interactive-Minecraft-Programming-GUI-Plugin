package me.BerylliumOranges.spellevent.segments.c_results;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import me.BerylliumOranges.main.SpellPluginMain;
import me.BerylliumOranges.spellevent.entity_information.EntityStats;
import me.BerylliumOranges.spellevent.other.Guideline;
import me.BerylliumOranges.spellevent.processes.SegmentProcess;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;
import net.md_5.bungee.api.ChatColor;

public class Immobilize extends EventSegment {
	public static final String THIS_SEGMENT_TYPE = SEGMENT_TYPE_RESULT;
	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 25;
	public static final double IMMOBALIZE_TIME = 1.5;
	public static final int RARITY = 2;

	public static Guideline processSegment(SegmentProcess proc) {
		int[] upgrades = getUpgrades(proc);
		if (proc.subtractMana(MANA_COST) < 1)
			return new Guideline(proc, true, true);

		ArrayList<EntityStats> targets = EntityStats.getEntityStats(proc.getTargetsCopy());
		for (EntityStats target : targets) {
			BukkitTask task = new BukkitRunnable() {
				public void run() {
					if (!target.isImmobilized()) {
						immobilize(target, true);
						BukkitTask task = new BukkitRunnable() {

							public void run() {
								this.cancel();
							}

							@Override
							public void cancel() {
								super.cancel();
								immobilize(target, false);
							}
						}.runTaskLater(SpellPluginMain.getInstance(), (long) getDuration(upgrades) * 20L);
						proc.getSpellSlot().getTasks().add(task);
						this.cancel();
					}
				}
			}.runTaskTimer(SpellPluginMain.getInstance(), 0, 0);
			proc.getSpellSlot().getTasks().add(task);

		}
		return new Guideline(proc, true, false);
	}

	public static void immobilize(EntityStats stat, boolean input) {
		stat.setImmobilized(input);
		if (input) {
			stat.getEntity().getLocation().getWorld().playSound(stat.getEntity().getLocation(), Sound.BLOCK_CHAIN_PLACE, 1.0F, 0.8F);
		} else {
			stat.getEntity().getLocation().getWorld().playSound(stat.getEntity().getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.3F, 2F);
		}
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "Immobilize");
		meta.setLore(Arrays.asList(ChatColor.WHITE + "Lasts for " + ChatColor.GREEN + getDuration(upgrades) + ChatColor.WHITE + " seconds", "",
				getCostText(MANA_COST)));

		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_RESULT);
		item.setItemMeta(meta);
		return item;
	}

	public static double getDuration(int upgrades[]) {
		return Math.floor((IMMOBALIZE_TIME + (upgrades[0] + upgrades[1]) / 6.0) * 100.0) / 100.0;
	}

}
