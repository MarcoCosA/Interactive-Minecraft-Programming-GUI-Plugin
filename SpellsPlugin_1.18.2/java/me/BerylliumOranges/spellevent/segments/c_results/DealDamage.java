package me.BerylliumOranges.spellevent.segments.c_results;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.BerylliumOranges.spellevent.other.Guideline;
import me.BerylliumOranges.spellevent.processes.SegmentProcess;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;
import net.md_5.bungee.api.ChatColor;

public class DealDamage extends EventSegment {
	public static final String THIS_SEGMENT_TYPE = SEGMENT_TYPE_RESULT;
	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 10;
	public static final double ORIGINAL_DAMAGE = 4.0;
	public static final int RARITY = 1;
	public static final String DAMAGE_SCOREBOARD_TAG = ChatColor.RED + "Damaged";

	public static Guideline processSegment(SegmentProcess proc) {
		int[] upgrades = getUpgrades(proc);
		if (proc.subtractMana(MANA_COST) < 1)
			return new Guideline(proc, true, true);
		for (LivingEntity target : proc.getTargetsCopy()) {
			try {
				target.getScoreboardTags().add(DAMAGE_SCOREBOARD_TAG);
				target.damage(getDamage(upgrades), proc.getCaster());
				target.getScoreboardTags().remove(DAMAGE_SCOREBOARD_TAG);
			} catch (Exception er) {
				er.printStackTrace();
			}
			target.setNoDamageTicks(Math.min(11, target.getNoDamageTicks() + 1));
		}
		return new Guideline(proc, false, false);
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "Damage Segment");
		meta.setLore(Arrays.asList(
				ChatColor.WHITE + "Deal " + ChatColor.GOLD + getDamage(upgrades) + ChatColor.WHITE + " damage to "
						+ ChatColor.YELLOW + "targets",
				ChatColor.DARK_GRAY + "Targets are briefly invulnerable", getCostText(MANA_COST)));

		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_RESULT);
		item.setItemMeta(meta);
		return item;
	}

	public static double getDamage(int upgrades[]) {
		return ORIGINAL_DAMAGE + (upgrades[0] + upgrades[1]) * 1.5;
	}

}
