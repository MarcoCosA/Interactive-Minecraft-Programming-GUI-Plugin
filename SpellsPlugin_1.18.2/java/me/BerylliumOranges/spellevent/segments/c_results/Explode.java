package me.BerylliumOranges.spellevent.segments.c_results;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.BerylliumOranges.spellevent.other.Guideline;
import me.BerylliumOranges.spellevent.processes.SegmentProcess;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSpellModifier;
import me.BerylliumOranges.spellevent.segments.d_modifiers.Fiery;
import net.md_5.bungee.api.ChatColor;

public class Explode extends EventSegment {
	public static final String THIS_SEGMENT_TYPE = SEGMENT_TYPE_RESULT;
	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 35;
	public static final float EXPLOSION_SIZE = 0.5F;
	public static final int RARITY = 1;

	public static Guideline processSegment(SegmentProcess proc) {
		int[] upgrades = getUpgrades(proc);
		if (proc.subtractMana(MANA_COST) < 1)
			return new Guideline(proc, true, true);

		boolean found = false;
		for (EventSpellModifier mod : proc.getProjectileModifiers()) {
			if (mod instanceof Fiery) {
				found = true;
				break;
			}
		}
		proc.getSpellLocation().getWorld().createExplosion(proc.getSpellLocation(), getExplosionSize(upgrades), found);
		return new Guideline(proc, true, false);
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "Explosion");
		meta.setLore(Arrays.asList(ChatColor.WHITE + "Deals a maximum of " + ChatColor.GREEN + "" + calculateMaxDamage(getExplosionSize(upgrades))
				+ ChatColor.WHITE + " damage", ChatColor.WHITE + "(scales off of game difficulty)", "", getCostText(MANA_COST)));

		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_RESULT);
		item.setItemMeta(meta);
		return item;
	}

	public static float getExplosionSize(int upgrades[]) {
		return EXPLOSION_SIZE + (upgrades[0] + upgrades[1]) * 0.2F;
	}

	public static float calculateMaxDamage(float explosionSize) {
		return (float) (Math.floor((1 * 1 + (Bukkit.getServer().getWorlds().get(0).getDifficulty().getValue())) * 700 * explosionSize + 100) / 100.0);
	}

}
