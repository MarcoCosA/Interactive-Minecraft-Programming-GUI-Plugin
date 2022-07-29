package me.BerylliumOranges.spellevent.segments.d_modifiers;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.BerylliumOranges.spellevent.other.Guideline;
import me.BerylliumOranges.spellevent.processes.SegmentProcess;
import me.BerylliumOranges.spellevent.processes.SpellActions;
import me.BerylliumOranges.spellevent.projectiles.SpellProjectile;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSpellModifier;

public class PoorOdds extends EventSpellModifier {

	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 20;
	public static final int RARITY = 4;

	public static Guideline processSegment(SegmentProcess proc) {
		if (Math.random() < 0.05) {
			proc.setCrit(proc.getCrit() + EventSegment.CRIT_MULTIPLIER * 5);
			String name = proc.getContainer().getItemMeta().getDisplayName();
			if (name == null || name.equals("")) {
				name = ITEM.getItemMeta().getDisplayName();
			}
			alertCaster(proc.getCaster(), name, ChatColor.BOLD + "Jackpot", false);
		}

		return findAndCallNext(proc);
	}

	@Override
	public boolean applyProjectileModifier(SpellProjectile proj, boolean finalCall) {
		return true;
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "Poor Odds");
		meta.setLore(Arrays.asList(
				ChatColor.WHITE + "Spell has a " + ChatColor.AQUA + "5% " + ChatColor.WHITE + "chance to",
				ChatColor.WHITE + "be upgraded " + ChatColor.AQUA + (EventSegment.CRIT_MULTIPLIER * 5) + ChatColor.WHITE
						+ " times",
				"",
				EventSegment.getCostText(MANA_COST) + " " + ChatColor.WHITE + SpellActions.getArrowModelFromInt(dir)));
		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_MODIFIER);
		item.setItemMeta(meta);
		return item;
	}

	public PoorOdds(SegmentProcess proc) {
		super(proc);
	}
}
