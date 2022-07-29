package me.BerylliumOranges.spellevent.segments.d_modifiers;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import me.BerylliumOranges.spellevent.other.Guideline;
import me.BerylliumOranges.spellevent.processes.SegmentProcess;
import me.BerylliumOranges.spellevent.processes.SpellActions;
import me.BerylliumOranges.spellevent.projectiles.SpellProjectile;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSpellModifier;

public class Missile extends EventSpellModifier {

	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 15;
	public static final int SIZE = 12;
	public static final int RARITY = 3;
	public static final double COST_PER_BLOCK = SpellProjectile.BASE_COST * 2;

	public static Guideline processSegment(SegmentProcess proc) {
		proc.incrementGeneration();
		if (proc.subtractMana(MANA_COST) < 1)
			return new Guideline(proc, true, true);
		proc.getProjectileModifiers().add(new Missile(proc));
		return findAndCallNext(proc);
	}

	@Override
	public boolean applyProjectileModifier(SpellProjectile proj, boolean finalCall) {
		if (proj.getTicksLived() == 0)
			proj.setCost(proj.getCost() + COST_PER_BLOCK);
		if (proj.getTicksLived() % 6 == 0)
			return false;
		// proj.setLifespan((int) (proj.getLifespan() / 0.7));
		// proj.setVelocity(proj.getVelocity().multiply(0.7));
		Entity nearest = null;
		double distance = Double.MAX_VALUE;
		double currentDistance = 0;
		int size = getSize(upgrades);
		for (Entity ent : proj.getProcess().getSpellLocation().getWorld().getNearbyEntities(proj.getLocation(), size, size, size)) {
			if (ent instanceof LivingEntity)
				if (!ent.isDead() && isEnemy(proj.getProcess().getCaster(), (LivingEntity) ent)) {
					currentDistance = ent.getLocation().distanceSquared(proj.getProcess().getSpellLocation());
					if (currentDistance < distance) {
						distance = currentDistance;
						nearest = ent;
					}
				}
		}
		if (nearest == null)
			return false;
		double length = proj.getVelocity().length();
		proj.setVelocity(
				nearest.getLocation().clone().add(0, nearest.getHeight() / 1.5, 0).toVector().subtract(proj.getLocation().toVector()).normalize());
		proj.getProcess().getSpellLocation().setDirection(proj.getVelocity());
		proj.setVelocity(proj.getVelocity().multiply(length));
		return false;
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "Missile");
		ArrayList<String> lore = new ArrayList<String>(Arrays.asList(ChatColor.WHITE + "Points your spell towards the",
				ChatColor.WHITE + "nearest living entity within " + ChatColor.GRAY + getSize(upgrades) + " blocks", "",
				getCostPerBlock(COST_PER_BLOCK), getCostText(MANA_COST) + " " + SpellActions.getArrowModelFromInt(dir)));
		meta.setLore(lore);
		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_MODIFIER);
		item.setItemMeta(meta);
		return item;
	}

	public Missile(SegmentProcess proc) {
		super(proc);
	}

	public static int getSize(int upgrades[]) {

		return SIZE + (upgrades[0] + upgrades[1]) * 4;
	}
}
