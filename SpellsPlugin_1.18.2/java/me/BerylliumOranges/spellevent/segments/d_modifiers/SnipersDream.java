package me.BerylliumOranges.spellevent.segments.d_modifiers;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import me.BerylliumOranges.spellevent.other.Guideline;
import me.BerylliumOranges.spellevent.processes.SegmentProcess;
import me.BerylliumOranges.spellevent.processes.SpellActions;
import me.BerylliumOranges.spellevent.projectiles.SpellProjectile;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSpellModifier;

public final class SnipersDream extends EventSpellModifier {
	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final int MAX_UPGRADES = 12;

	// Higher = Weaker
	public static final double DISTANCE_MODIFIER = 40;
	public static final double MANA_COST = 25;
	public static final int RARITY = 3;

	protected double distance = -5;
	protected int originalCrit;

	public static Guideline processSegment(SegmentProcess proc) {
		proc.incrementGeneration();
		if (proc.subtractMana(MANA_COST) < 1)
			return new Guideline(proc, true, true);

		proc.getProjectileModifiers().add(new SnipersDream(proc));
		return findAndCallNext(proc);
	}

	public SnipersDream(SegmentProcess proc) {
		super(proc);
		originalCrit = proc.getCrit() - proc.getTimesUpgradedBySnipersDream();
	}

	@Override
	public boolean applyProjectileModifier(SpellProjectile proj, boolean finalCall) {
		Vector normal = proj.getVelocity().clone().normalize();
		distance += Math.sqrt(Math.pow(normal.getX(), 2) + Math.pow(normal.getZ(), 2));
		int crit = Math.min(
				Math.max((int) (distance / DISTANCE_MODIFIER), 0) + proj.getProcess().getTimesUpgradedBySnipersDream(),
				MAX_UPGRADES);
		proj.getProcess().setCrit(crit + originalCrit);
		if (finalCall) {
			proj.getProcess().setTimesUpgradedBySnipersDream(crit);
			alertCaster(proj.getProcess().getCaster(), ITEM, "Upgraded " + crit + "x", false);
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "Sniper's Dream");
		ArrayList<String> lore = new ArrayList<String>(
				Arrays.asList(ChatColor.WHITE + "Your next projectile becomes stronger",
						ChatColor.WHITE + "the " + ChatColor.AQUA + "further " + ChatColor.WHITE
								+ "it travels horizontally",
						ChatColor.DARK_GRAY + "Maximum " + MAX_UPGRADES + " upgrades",
						getCostText(MANA_COST) + " " + SpellActions.getArrowModelFromInt(dir)));
		meta.setLore(lore);
		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_MODIFIER);
		item.setItemMeta(meta);
		return item;
	}

//	@Override
//	public Object clone() throws CloneNotSupportedException {
//		SnipersDream clone = new SnipersDream(null);
//		return this;
//	}
}
