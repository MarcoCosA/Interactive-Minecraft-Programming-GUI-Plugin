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

public class ApplyGravity extends EventSpellModifier {

	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double VELOCITY_MODIFIER = 1.8;
	public static final double MANA_COST = 1;

	public static final int RARITY = 2;

	public static Guideline processSegment(SegmentProcess proc) {
		proc.incrementGeneration();
		if (proc.subtractMana(MANA_COST) < 1)
			return new Guideline(proc, true, true);
		for (int i = 0; i < proc.getPresentSegments()[proc.getY()][proc.getX()].getAmount(); i++)
			proc.getProjectileModifiers().add(new ApplyGravity(proc));

		return findAndCallNext(proc);
	}

	@Override
	public boolean applyProjectileModifier(SpellProjectile proj, boolean finalCall) {
		proj.setAcceleration(proj.getAcceleration().add(new Vector(0, -0.02, 0)));
		return true;
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "Apply Gravity");
		ArrayList<String> lore = new ArrayList<String>(Arrays.asList(ChatColor.WHITE + "Your next projectile",
				ChatColor.WHITE + "accelerates downwards"/* + ChatColor.AQUA + "gravity" */,
				ChatColor.DARK_GRAY + "Stacks", getCostText(MANA_COST) + " " + SpellActions.getArrowModelFromInt(dir)));
		meta.setLore(lore);
		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_MODIFIER);
		item.setItemMeta(meta);
		return item;
	}

	public ApplyGravity(SegmentProcess proc) {
		super(proc);
	}
}
