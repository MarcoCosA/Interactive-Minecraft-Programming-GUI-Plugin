package me.BerylliumOranges.spellevent.segments.f_unfair;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.BerylliumOranges.spellevent.other.Guideline;
import me.BerylliumOranges.spellevent.processes.SegmentProcess;
import me.BerylliumOranges.spellevent.processes.SpellActions;
import me.BerylliumOranges.spellevent.projectiles.SpellProjectile;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSpellModifier;

public class PlayerSeeker extends EventSpellModifier {

	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 1;
	public static final int SIZE = 16;
	public static final int RARITY = 4;

	public static Guideline processSegment(SegmentProcess proc) {
		if (proc.subtractMana(MANA_COST) < 1)
			return new Guideline(proc, true, true);
		proc.incrementGeneration();
		proc.getProjectileModifiers().add(new PlayerSeeker(proc));
		return findAndCallNext(proc);
	}

	@Override
	public boolean applyProjectileModifier(SpellProjectile proj, boolean finalCall) {
		if (proj.getTicksLived() % 6 == 0)
			return false;

		Entity nearest = null;
		double distance = Double.MAX_VALUE;
		double currentDistance = 0;
		int size = getSize(upgrades);
		for (Entity ent : proj.getProcess().getSpellLocation().getWorld().getNearbyEntities(proj.getLocation(), size,
				size, size)) {
			if (!ent.isDead() && ent instanceof Player && !ent.equals(proj.getProcess().getCaster())) {
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
		proj.setVelocity(nearest.getLocation().clone().add(0, nearest.getHeight() / 1.5, 0).toVector()
				.subtract(proj.getLocation().toVector()).normalize().multiply(length));
		return false;
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "Player Seeker");
		ArrayList<String> lore = new ArrayList<String>(Arrays.asList(ChatColor.WHITE + "Points your spell towards the",
				ChatColor.WHITE + "nearest player within " + ChatColor.GRAY + getSize(upgrades) + " blocks", "",
				getCostText(MANA_COST) + " " + ChatColor.GRAY + SpellActions.getArrowModelFromInt(dir)));
		meta.setLore(lore);
		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_UNFAIR);
		item.setItemMeta(meta);
		return item;
	}

	public PlayerSeeker(SegmentProcess proc) {
		super(proc);
	}

	public static int getSize(int upgrades[]) {

		return SIZE + (upgrades[0] + upgrades[1]) * 4;
	}
}
