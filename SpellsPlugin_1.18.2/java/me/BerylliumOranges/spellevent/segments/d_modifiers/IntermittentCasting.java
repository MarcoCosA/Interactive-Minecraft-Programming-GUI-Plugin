package me.BerylliumOranges.spellevent.segments.d_modifiers;

import java.util.ArrayList;
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

public final class IntermittentCasting extends EventSpellModifier {
	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 30;
	public static final int RARITY = 2;
	public static final int TIMES_TO_CAST_DEFAULT = 2;
	protected int timesCast = 1;

	public static Guideline processSegment(SegmentProcess proc) {

		for (EventSpellModifier mod : proc.getProjectileModifiers()) {
			if (mod instanceof IntermittentCasting)
				return findAndCallNext(proc);
		}

		if (proc.subtractMana(MANA_COST) < 1)
			return new Guideline(proc, true, true);

		proc.getProjectileModifiers().add(new IntermittentCasting(proc));
		return findAndCallNext(proc);
	}

	public IntermittentCasting(SegmentProcess proc) {
		super(proc);
		upgrades = getItemUpgrades(proc);
	}

	@Override
	public boolean applyProjectileModifier(SpellProjectile proj, boolean finalCall) {
		double intTimesToCast = getTimesToCast(upgrades) + 1;
		if (timesCast < intTimesToCast
				&& proj.getTicksLived() / (double) proj.getLifespan() * intTimesToCast > timesCast) {
			try {
				SegmentProcess clone = (SegmentProcess) proj.getProcess().clone();
				clone.setSpellLocation(proj.getLocation().clone());
				EventSegment.findAndCallNext(clone);
				timesCast++;
			} catch (Exception er) {
				er.printStackTrace();
			}
		}
		for (EventSpellModifier mod : proj.getProcess().getProjectileModifiers()) {
			if (mod instanceof IntermittentCasting && mod != this) {
				return false;
			}
		}

		return false;
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "Intermittent Casting");
		ArrayList<String> lore = new ArrayList<String>(Arrays.asList(ChatColor.WHITE + "Your next projectile will cast",
				ChatColor.WHITE + "your spell " + ChatColor.AQUA + getTimesToCast(upgrades) + ChatColor.WHITE
						+ " additional times",
				ChatColor.DARK_GRAY + "Does not stack or crit",
				getCostText(MANA_COST) + " " + SpellActions.getArrowModelFromInt(dir)));
		meta.setLore(lore);
		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_MODIFIER);
		item.setItemMeta(meta);
		return item;
	}

	public static int getTimesToCast(int upgrades[]) {
		return TIMES_TO_CAST_DEFAULT + (upgrades[0] + upgrades[1]) / 3;
	}
}