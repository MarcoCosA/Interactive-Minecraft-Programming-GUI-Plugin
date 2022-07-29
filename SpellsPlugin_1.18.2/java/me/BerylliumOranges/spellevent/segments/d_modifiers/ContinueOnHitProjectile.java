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
import me.BerylliumOranges.spellevent.segments.abstracts.EventSpellModifier;

public class ContinueOnHitProjectile extends EventSpellModifier {

	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 1;
	public static final double COST_PER_BLOCK = Math.floor(SpellProjectile.BASE_COST * 10) / 10.0;

	public static final int RARITY = 4;

	public static Guideline processSegment(SegmentProcess proc) {

		proc.incrementGeneration();
		if (proc.subtractMana(MANA_COST) < 1)
			return new Guideline(proc, true, true);
		proc.getProjectileModifiers().add(new ContinueOnHitProjectile(proc));
		return findAndCallNext(proc);
	}

	@Override
	public boolean applyProjectileModifier(SpellProjectile proj, boolean finalCall) {
		proj.setCost(proj.getCost() + COST_PER_BLOCK);
		proj.setCancelOnTarget(false);
		return true;
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "Continue Projectile after Hit");
		ArrayList<String> lore = new ArrayList<String>(Arrays.asList(ChatColor.WHITE + "Your next projectile does not ",
				ChatColor.AQUA + "interact" + ChatColor.WHITE + " with blocks", ChatColor.DARK_GRAY + "Does not stack",
				getCostPerBlock(COST_PER_BLOCK), getCostText(MANA_COST) + " " + SpellActions.getArrowModelFromInt(dir)));
		meta.setLore(lore);
		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_MODIFIER);
		item.setItemMeta(meta);
		return item;
	}

	public ContinueOnHitProjectile(SegmentProcess proc) {
		super(proc);
	}
}
