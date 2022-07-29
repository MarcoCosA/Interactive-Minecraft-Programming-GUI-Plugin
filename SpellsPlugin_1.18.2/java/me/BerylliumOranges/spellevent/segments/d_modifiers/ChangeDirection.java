package me.BerylliumOranges.spellevent.segments.d_modifiers;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
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

public class ChangeDirection extends EventSpellModifier {

	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double VELOCITY_MODIFIER = 1.8;
	public static final double MANA_COST = 1;
	public static final int RARITY = 2;

	public static final double SPEED = 0.02;
	public Vector normal = null;
	public int dir = 0;

	public static Guideline processSegment(SegmentProcess proc) {
		proc.incrementGeneration();

//		for (EventSpellModifier mod : proc.getProjectileModifiers()) {
//			if (mod instanceof ApplyAcceleration)
//				return findAndCallNext(proc);
//		}

		if (proc.subtractMana(MANA_COST) < 1)
			return new Guideline(proc, true, true);

		for (int i = 0; i < proc.getPresentSegments()[proc.getY()][proc.getX()].getAmount(); i++) {
			ChangeDirection ac = new ChangeDirection(proc);
			ac.setDir(proc.getDir());
			proc.getProjectileModifiers().add(ac);
		}
		return findAndCallNext(proc);
	}

	@Override
	public boolean applyProjectileModifier(SpellProjectile proj, boolean finalCall) {
		Vector vel = new Vector(0, -SPEED, 0);
//		if (normal == null)
		normal = new Vector(proj.getVelocity().getX(), 0, proj.getVelocity().getZ()).normalize();

		if (dir == 0) {
			vel = normal.clone().rotateAroundY(-90).multiply(SPEED);
		} else if (dir == 2) {
			vel = normal.clone().rotateAroundY(90).multiply(SPEED);
		} else if (dir == 3) {
			vel = new Vector(0, SPEED, 0);
		}
		proj.setVelocity(proj.getVelocity().add(vel).normalize());

//		proj.setAcceleration(proj.getAcceleration().add(vel));
		return false;
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "Change Direction");
		ArrayList<String> lore = new ArrayList<String>(Arrays.asList(ChatColor.WHITE + "Your next projectile",
				ChatColor.WHITE + "moves in the " + ChatColor.AQUA + "direction",
				ChatColor.AQUA + "of this segment" /* + ChatColor.AQUA + "gravity" */, ChatColor.DARK_GRAY + "Stacks",
				getCostText(MANA_COST) + " " + SpellActions.getArrowModelFromInt(dir)));
		meta.setLore(lore);
		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_MODIFIER);
		item.setItemMeta(meta);
		return item;
	}

	public ChangeDirection(SegmentProcess proc) {
		super(proc);
	}

	public int getDir() {
		return dir;
	}

	public void setDir(int dir) {
		this.dir = dir;
	}
}
