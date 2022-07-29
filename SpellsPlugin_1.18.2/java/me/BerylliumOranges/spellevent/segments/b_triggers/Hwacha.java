package me.BerylliumOranges.spellevent.segments.b_triggers;

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
import me.BerylliumOranges.spellevent.segments.abstracts.EventTriggerSegment;

public final class Hwacha extends EventTriggerSegment {
	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 10;

	public static final int RARITY = 1;
	public static final int PROJECTILE_COUNT = 5;
	public static final boolean IS_PROJECTILE = true;
	public static final double PROJ_COST = SpellProjectile.BASE_COST / PROJECTILE_COUNT;

	public static Guideline processSegment(SegmentProcess proc) {
		proc.addTimesCloned(PROJECTILE_COUNT);
		if (proc.subtractMana(MANA_COST) < 1)
			return new Guideline(proc, true, true);
		proc.getSpellLocation().setDirection(
				proc.getSpellLocation().getDirection().add(new Vector(0, 0.2 * Math.random(), 0)).normalize());
		Vector normalXZ = new Vector(proc.getSpellLocation().getDirection().getX(), 0,
				proc.getSpellLocation().getDirection().getZ()).normalize().multiply(0.55);
		try {
			{
				SegmentProcess clone = (SegmentProcess) proc.clone();
				SpellProjectile proj = new SpellProjectile(clone, clone.getSpellLocation(),
						clone.getSpellLocation().getDirection());
				proj.setCost(PROJ_COST);
			}
			{
				Vector moveRight = normalXZ.clone().rotateAroundY(Math.toRadians(90));
				{
					moveRight.add(moveRight);
					SegmentProcess clone = (SegmentProcess) proc.clone();
					clone.getSpellLocation().add(moveRight);
					SpellProjectile proj = new SpellProjectile(clone, clone.getSpellLocation(),
							clone.getSpellLocation().getDirection());
					proj.setCost(PROJ_COST);
				}
				{
					moveRight.add(moveRight);
					SegmentProcess clone = (SegmentProcess) proc.clone();
					clone.getSpellLocation().add(moveRight);
					SpellProjectile proj = new SpellProjectile(clone, clone.getSpellLocation(),
							clone.getSpellLocation().getDirection());
					proj.setCost(PROJ_COST);
				}
			}
			{
				Vector moveLeft = normalXZ.clone().rotateAroundY(Math.toRadians(-90));
				{
					moveLeft.add(moveLeft);
					SegmentProcess clone = (SegmentProcess) proc.clone();
					clone.getSpellLocation().add(moveLeft);
					SpellProjectile proj = new SpellProjectile(clone, clone.getSpellLocation(),
							clone.getSpellLocation().getDirection());
					proj.setCost(PROJ_COST);
				}
				{
					moveLeft.add(moveLeft);
					SegmentProcess clone = (SegmentProcess) proc.clone();
					clone.getSpellLocation().add(moveLeft);
					SpellProjectile proj = new SpellProjectile(clone, clone.getSpellLocation(),
							clone.getSpellLocation().getDirection());
					proj.setCost(PROJ_COST);
				}
			}
		} catch (Exception er) {
			er.printStackTrace();
		}

		return new Guideline(proc, true, true);
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "Hwacha");
		ArrayList<String> lore = new ArrayList<String>(Arrays.asList(
				ChatColor.WHITE + "Launches " + ChatColor.YELLOW + PROJECTILE_COUNT + ChatColor.WHITE + " projectiles ",
				"", getCostText(MANA_COST) + " " + ChatColor.YELLOW + SpellActions.getArrowModelFromInt(dir)));
		lore.addAll(getMultiplierLore(MANA_COST_MULTIPLIER));
		meta.setLore(lore);
		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_TRIGGER);
		item.setItemMeta(meta);
		return item;
	}
}
