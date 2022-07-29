package me.BerylliumOranges.spellevent.segments.c_results;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
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

public class Mute extends EventSegment {
	public static final String THIS_SEGMENT_TYPE = SEGMENT_TYPE_RESULT;
	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 45;
	public static final double TIME = 3;
	public static final int RARITY = 3;

	public static Guideline processSegment(SegmentProcess proc) {
		int[] upgrades = getUpgrades(proc);
		if (proc.subtractMana(MANA_COST) < 1)
			return new Guideline(proc, true, true);

		ArrayList<EntityStats> targets = EntityStats.getEntityStats(proc.getTargetsCopy());
		for (EntityStats target : targets) {
			BukkitTask task = new BukkitRunnable() {
				public void run() {
					if (!target.isMuted()) {
						mute(target, true);
						BukkitTask task = new BukkitRunnable() {

							public void run() {
								this.cancel();
							}

							@Override
							public void cancel() {
								super.cancel();
								mute(target, false);
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

	public static final double RED = 75 / 255D;
	public static final double GREEN = 0 / 255D;
	public static final double BLUE = 130 / 255D;

	public static void mute(EntityStats stat, boolean mute) {
		stat.setMuted(mute);
		Location loc = stat.getEntity().getEyeLocation();
		if (mute) {
			loc.getWorld().playSound(loc, Sound.BLOCK_FIRE_EXTINGUISH, 0.2F, 2F);
			try {
				for (int i = 0; i < 7; i++)
					stat.getEntity().getWorld().spawnParticle(Particle.SPELL_MOB, loc.getX() + Math.random() - 0.5,
							loc.getY() + Math.random() - 0.3, loc.getZ() + (Math.random() - 0.5) * 2, 0, RED, GREEN,
							BLUE, 1, null, true);
			} catch (Exception er) {
				er.printStackTrace();
			}
		} else {
			loc.getWorld().playSound(loc, Sound.ITEM_FLINTANDSTEEL_USE, 0.85F, 2F);
		}
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "Mute");
		meta.setLore(Arrays.asList(ChatColor.YELLOW + "Targets " + ChatColor.WHITE + "cannot cast spells",
				ChatColor.WHITE + "for " + ChatColor.GREEN + getDuration(upgrades) + ChatColor.WHITE + " seconds",
				ChatColor.DARK_GRAY + "Doesn't cancel in progress spells", getCostText(MANA_COST)));

		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_RESULT);
		item.setItemMeta(meta);
		return item;
	}

	public static double getDuration(int upgrades[]) {
		return TIME + (upgrades[0] + upgrades[1]) / 3.0;
	}

}
