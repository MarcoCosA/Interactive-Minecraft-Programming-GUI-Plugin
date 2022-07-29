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
import me.BerylliumOranges.spellevent.segments.abstracts.EventSpellModifier;
import me.BerylliumOranges.spellevent.segments.b_triggers.DomainExpansion;

public class PerfectDomain extends EventSpellModifier {

	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 10;
	public static final int RARITY = 2;
	public static final int BONUS_RADIUS = 2;

	public static Guideline processSegment(SegmentProcess proc) {
		if (proc.subtractMana(MANA_COST) < 1)
			return new Guideline(proc, true, true);
		for (int i = 0; i < proc.getPresentSegments()[proc.getY()][proc.getX()].getAmount(); i++)
			proc.getProjectileModifiers().add(new PerfectDomain(proc));
		return findAndCallNext(proc);
	}

	@Override
	public boolean applyProjectileModifier(SpellProjectile proj, boolean finalCall) {
		return false;
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "Perfect Domain");
		meta.setLore(Arrays.asList(
				ChatColor.YELLOW + ChatColor.stripColor(DomainExpansion.ITEM.getItemMeta().getDisplayName()) + " "
						+ ChatColor.WHITE + "casts much",
				ChatColor.AQUA + "faster " + ChatColor.WHITE + "and is " + ChatColor.GRAY + BONUS_RADIUS
						+ ChatColor.WHITE + " blocks " + ChatColor.AQUA + "larger",
				ChatColor.DARK_GRAY + "Stacks", getCostText(MANA_COST) + " " + SpellActions.getArrowModelFromInt(dir)));
		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_MODIFIER);
		item.setItemMeta(meta);
		return item;
	}

	public PerfectDomain(SegmentProcess proc) {
		super(proc);
	}
}
