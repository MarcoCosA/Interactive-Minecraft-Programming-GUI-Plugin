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

public class InterceptorProjectile extends EventSpellModifier {

	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 15;
	public static final int SIZE = 12;
	public static final int RARITY = 2;
	public static final double COST_PER_BLOCK = Math.floor(SpellProjectile.BASE_COST * 15.0) / 10.0;

	public static Guideline processSegment(SegmentProcess proc) {
		proc.incrementGeneration();
		if (proc.subtractMana(MANA_COST) < 1)
			return new Guideline(proc, true, true);
		proc.getProjectileModifiers().add(new InterceptorProjectile(proc));
		return findAndCallNext(proc);
	}

	@Override
	public boolean applyProjectileModifier(SpellProjectile proj, boolean finalCall) {
		if (proj.getTicksLived() == 0)
			proj.setCost(proj.getCost() + COST_PER_BLOCK);
		if (proj.getTicksLived() % 6 == 0)
			return false;

		SpellProjectile nearest = null;
		double distance = Double.MAX_VALUE;
		double currentDistance = 0;
		int size = getSize(upgrades);
		for (SpellProjectile p : SpellProjectile.allProjectiles) {
			if (isEnemy(proj.getProcess().getCaster(), p.getProcess().getCaster()) && !hasModifier(p.getProcess(), InterceptorProjectile.class)
					&& p.getLocation().distanceSquared(proj.getLocation()) < size * size) {
				if (!p.getProcess().getSpellSlot().isStopped() && p.isAlive()) {
					currentDistance = p.getLocation().distanceSquared(proj.getLocation());
					if (currentDistance < distance) {
						distance = currentDistance;
						nearest = p;
					}
				}
			}
		}
		if (nearest == null)
			return false;

		if (nearest.getLocation().distanceSquared(proj.getLocation()) < proj.getHitbox() * proj.getHitbox()) {
			proj.setTicksLived(proj.getLifespan() + 1);
			proj.getProcess().setFocusedSpell(nearest.getProcess());
		}

		double length = proj.getVelocity().length();
		proj.setVelocity(nearest.getLocation().clone().toVector().subtract(proj.getLocation().toVector()).normalize());
		proj.getProcess().getSpellLocation().setDirection(proj.getVelocity());
		proj.setVelocity(proj.getVelocity().multiply(length));
		return false;
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "Interceptor Projectile");
		ArrayList<String> lore = new ArrayList<String>(Arrays.asList(ChatColor.WHITE + "Your next projectile follows the",
				ChatColor.WHITE + "nearest enemy projectile within ", ChatColor.GRAY + "" + getSize(upgrades) + ChatColor.WHITE + " blocks and"
						+ ChatColor.LIGHT_PURPLE + " focuses " + ChatColor.WHITE + "its spell",
				"", getCostPerBlock(COST_PER_BLOCK), getCostText(MANA_COST) + " " + SpellActions.getArrowModelFromInt(dir)));
		meta.setLore(lore);
		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_MODIFIER);
		item.setItemMeta(meta);
		return item;
	}

	public InterceptorProjectile(SegmentProcess proc) {
		super(proc);
	}

	public static int getSize(int upgrades[]) {
		return SIZE + (upgrades[0] + upgrades[1]) * 4;
	}
}
