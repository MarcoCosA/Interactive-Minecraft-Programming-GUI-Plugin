package me.BerylliumOranges.spellevent.segments.b_triggers;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import me.BerylliumOranges.spellevent.other.Guideline;
import me.BerylliumOranges.spellevent.processes.SegmentProcess;
import me.BerylliumOranges.spellevent.processes.SpellActions;
import me.BerylliumOranges.spellevent.projectiles.SpellProjectile;
import me.BerylliumOranges.spellevent.segments.abstracts.EventTriggerSegment;
import me.BerylliumOranges.spellevent.segments.d_modifiers.ApplyGravity;
import me.BerylliumOranges.spellevent.segments.f_unfair.TargetNearestEnemy;

public final class Artillery extends EventTriggerSegment {
	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 10;
	public static final int RADIUS = 121;
	public static final int RARITY = 4;
	public static final boolean IS_PROJECTILE = true;

	public static Guideline processSegment(SegmentProcess proc) {
		LivingEntity liv = TargetNearestEnemy.getNearestEnemy(proc.getCaster(), getRange(getItemUpgrades(proc)));

		if (proc.subtractMana(MANA_COST) < 1 || liv == null)
			return new Guideline(proc, true, true);

		Vector d = liv.getLocation().toVector().subtract(proc.getSpellLocation().toVector()).normalize();

		d.add(new Vector(0, liv.getLocation().distance(proc.getSpellLocation()) * 0.0178, 0));

		proc.getProjectileModifiers().add(new ApplyGravity(proc));

		if (proc.getCaster() instanceof Mob) {
			Mob m = (Mob) proc.getCaster();
			m.teleport(proc.getCaster().getLocation().clone().setDirection(d.normalize()));
		}

		new SpellProjectile(proc, proc.getSpellLocation(), d);

		return new Guideline(proc, false, true);
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "Artillery");
		ArrayList<String> lore = new ArrayList<String>(Arrays.asList(ChatColor.WHITE + "Launches a projectile at an enemy",
				ChatColor.WHITE + "within " + ChatColor.YELLOW + getRange(upgrades) + ChatColor.WHITE + " blocks.", "",
				getCostPerBlock(SpellProjectile.BASE_COST), getCostText(MANA_COST) + " " + ChatColor.YELLOW + SpellActions.getArrowModelFromInt(dir)));
		// lore.addAll();
		meta.setLore(lore);
		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_TRIGGER);
		item.setItemMeta(meta);
		return item;
	}

	public static int getRange(int[] upgrades) {
		return RADIUS + (upgrades[0] + upgrades[1]) * 10;
	}
}
