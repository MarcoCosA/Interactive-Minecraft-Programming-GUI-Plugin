package me.BerylliumOranges.spellevent.segments.e_logic;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import me.BerylliumOranges.spellevent.other.Guideline;
import me.BerylliumOranges.spellevent.processes.SegmentProcess;
import me.BerylliumOranges.spellevent.processes.SpellActions;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;
import me.BerylliumOranges.spellevent.segments.d_modifiers.SlowProjectile;
import net.md_5.bungee.api.ChatColor;

public class Rotate extends EventSegment {
	public static final String THIS_SEGMENT_TYPE = SEGMENT_TYPE_LOGIC;
	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 1;
	public static final int RARITY = 0;

	public static Guideline processSegment(SegmentProcess proc) {
		int[] upgrades = getUpgrades(proc);
		if (proc.subtractMana(MANA_COST) < 1)
			return new Guideline(proc, true, true);
//		Vector v = proc.getSpellLocation().getDirection();
//		Vector normalXZ = new Vector(proc.getSpellLocation().getDirection().getX(), 0,
//				proc.getSpellLocation().getDirection().getZ()).normalize();
//		Vector normalized = proc.getSpellLocation().getDirection().clone().normalize();
//		rotateVector(v, 0, Math.atan(-normalized.getY() / (Math.abs(normalized.getX()) + Math.abs(normalized.getZ()))));
//		double angleXZ = Math.atan(normalXZ.getZ() / normalXZ.getX());
//		if ((normalXZ.getX() < 0)) {
//			angleXZ += Math.PI;
//		}
//		rotateVector(v, angleXZ + Math.toRadians(-45 * proc.getPresentSegments()[proc.getY()][proc.getX()].getAmount()),
//				0);
		Vector normalXZ = new Vector(proc.getSpellLocation().getDirection().getX(), 0,
				proc.getSpellLocation().getDirection().getZ());

		proc.getSpellLocation().setDirection(proc.getSpellLocation().getDirection()
				.rotateAroundY(Math.toRadians(-15 * proc.getPresentSegments()[proc.getY()][proc.getX()].getAmount())));
//		proc.getSpellLocation().setDirection(proc.getSpellLocation().getDirection()
//				.rotateAroundZ(Math.atan(proc.getSpellLocation().getDirection().getY() / normalXZ.length())));
		return findAndCallNext(proc);
	}

	public static void rotateVector(Vector v, double angleX, double angleY) {
		double sinX = Math.sin(angleX);
		double cosX = Math.cos(angleX);

		double sinY = Math.sin(angleY);
		double cosY = Math.cos(angleY);
		double x = v.getX();
		double y = v.getY();
		double z = v.getZ();

		v.setX(x * cosX - z * sinX);
		v.setZ(z * cosX + x * sinX);

		z = v.getZ();

		v.setY(y * cosY - z * sinY);
		v.setZ(z * cosY + y * sinY);
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "Rotate Right 15\u00B0");
		meta.setLore(Arrays.asList(ChatColor.WHITE + "Rotates your spell 15 degrees", ChatColor.WHITE + "to the right",
				ChatColor.DARK_GRAY + "Stacks", getCostText(MANA_COST) + " " + SpellActions.getArrowModelFromInt(dir)));
		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_LOGIC);
		item.setItemMeta(meta);
		return item;
	}

}
