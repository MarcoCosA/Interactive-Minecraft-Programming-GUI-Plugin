package me.BerylliumOranges.spellevent.segments.c_results;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import me.BerylliumOranges.spellevent.other.Guideline;
import me.BerylliumOranges.spellevent.processes.SegmentProcess;
import me.BerylliumOranges.spellevent.processes.SpellProcess;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;
import net.md_5.bungee.api.ChatColor;

public class Knockback extends EventSegment {
	public static final String THIS_SEGMENT_TYPE = SEGMENT_TYPE_RESULT;
	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 12;
	public static final double KNOCKBACK_STRENGTH = 1;
	public static final int RARITY = 0;

	public static Guideline processSegment(SegmentProcess proc) {
		int[] upgrades = getUpgrades(proc);
		if (proc.subtractMana(MANA_COST) < 1)
			return new Guideline(proc, true, true);
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(SpellProcess.plugin, new Runnable() {
			public void run() {
				for (LivingEntity target : proc.getTargetsCopy()) {
					// Vector unitVector =
					// proc.getSpellLocation().getDirection().subtract(target.getLocation().toVector())
					// .normalize();
					if (target.equals(proc.getCaster())) {
						target.setVelocity(target.getVelocity().clone().add(proc.getSpellLocation().getDirection()).clone()
								.multiply(getKnockbackStrength(upgrades)));
					} else
						target.setVelocity(target.getVelocity().clone().add(proc.getSpellLocation().getDirection()).clone()
								.multiply(new Vector(1, 0.5, 1)).multiply(getKnockbackStrength(upgrades)));
					// Vector unitVector =
					// proc.getSpellLocation().getDirection().clone().normalize();
					// Bukkit.broadcastMessage("unit vector: " + unitVector);
					// target.setVelocity(target.getVelocity().add(unitVector.multiply(-getKnockbackStrength(upgrades))));

				}
			}
		});
		return new Guideline(proc, true, false);
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "Knockback");
		meta.setLore(Arrays.asList(ChatColor.WHITE + "Are knocked " + ChatColor.GREEN + "away " + ChatColor.WHITE + "from you.",
				ChatColor.WHITE + "Knockback " + ChatColor.GREEN + "strength: " + Math.floor(getKnockbackStrength(upgrades) * 100.0) / 100.0, "",
				getCostText(MANA_COST)));

		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_RESULT);
		item.setItemMeta(meta);
		return item;
	}

	public static double getKnockbackStrength(int upgrades[]) {
		return KNOCKBACK_STRENGTH + (upgrades[0] + upgrades[1]) / 6.0;
	}

}
