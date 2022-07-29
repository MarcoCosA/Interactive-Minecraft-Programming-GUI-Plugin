package me.BerylliumOranges.spellevent.segments.d_modifiers;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import me.BerylliumOranges.main.SpellPluginMain;
import me.BerylliumOranges.spellevent.other.Guideline;
import me.BerylliumOranges.spellevent.processes.SegmentProcess;
import me.BerylliumOranges.spellevent.processes.SpellActions;
import me.BerylliumOranges.spellevent.processes.SpellProcess;
import me.BerylliumOranges.spellevent.projectiles.SpellProjectile;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSpellModifier;

public class Liquifacting extends EventSpellModifier {

	public static final ArrayList<Material> BLOCK_TYPES = new ArrayList<Material>(Arrays.asList(Material.OBSIDIAN, Material.FIRE, Material.COBWEB));
	public static final ArrayList<Material> BLOCK_TYPES_TO_DELETE = new ArrayList<Material>(Arrays.asList(Material.COBWEB, Material.OBSIDIAN));

	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST_MULTIPLIER = 1;
	public static final double MANA_COST = 10;
	public static final int SIZE = 2;
	public static final int RARITY = 4;
	public static final double COST_PER_BLOCK = SpellProjectile.BASE_COST * 2;

	public static boolean registered = false;

	public static Guideline processSegment(SegmentProcess proc) {
		if (proc.getY() < proc.getTimesProced().length) {
			Liquifacting path = new Liquifacting(proc);
			proc.incrementGeneration();

			if (proc.subtractMana(MANA_COST) < 1)
				return new Guideline(proc, true, true);
			proc.getProjectileModifiers().add(path);
		}
		return findAndCallNext(proc);
	}

	@Override
	public boolean applyProjectileModifier(SpellProjectile proj, boolean finalCall) {
		if (proj.getTicksLived() == 0)
			proj.setCost(proj.getCost() + COST_PER_BLOCK);

		if (proj.getProcess().getY() < proj.getProcess().getTimesProced().length) {

			Vector normalXZ = new Vector(proj.getLocation().getDirection().getX(), 0, proj.getLocation().getDirection().getZ()).normalize();
			double vel = getVel(upgrades);
			double rot = Math.atan(normalXZ.getZ() / normalXZ.getX());
			double size = getSize(upgrades);
			for (double x = 0.5; x < size; x++) {
				double insideSize = x;
				for (double i = 0; i < insideSize; i += 0.25) {
					double angle = (Math.PI * 2) * (i / insideSize);
					double tempX = Math.cos(angle);

					Vector vec = new Vector((-tempX * Math.sin(rot)) * insideSize, Math.sin(angle) * insideSize,
							(tempX * Math.cos(rot)) * insideSize);
					// if ((normalXZ.getX() < 0)) {
					// rot += Math.PI;
					// }
					// vec.rotateAroundX(rot);

					Location loc = proj.getLocation().clone().add(vec);
					if (!loc.getBlock().getType().isAir()) {
						FallingBlock fall = loc.getWorld().spawnFallingBlock(loc.getBlock().getLocation().add(0.5, 0, 0.5),
								loc.getBlock().getBlockData());
						loc.getBlock().setType(Material.AIR);
						fall.setVelocity(new Vector(0, 0.5 + vel, 0));
						// loc.getWorld().spawnParticle(Particle.LANDING_HONEY,
						// loc,
						// 0);
					}
				}
			}

			try {
				for (double x = 0; x < 360.0; x += 5) {

				}
			} catch (Exception er) {
				er.printStackTrace();
			}
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "Liquifacting Projectile");
		ArrayList<String> lore = new ArrayList<String>(Arrays.asList(ChatColor.WHITE + "Your next projectile disrupts", ChatColor.WHITE + "blocks",
				ChatColor.DARK_GRAY + "Does not crit", getCostText(MANA_COST) + " " + ChatColor.GRAY + SpellActions.getArrowModelFromInt(dir)));
		lore.addAll(getMultiplierLore(MANA_COST_MULTIPLIER));
		meta.setLore(lore);
		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_MODIFIER);
		item.setItemMeta(meta);
		return item;
	}

	public Liquifacting(SegmentProcess proc) {
		super(proc);
		upgrades = EventSegment.getItemUpgrades(proc);
	}

	public static double getSize(int upgrades[]) {

		return (int) (SIZE + (upgrades[0] + upgrades[1]) / 8.0);
	}

	public static double getVel(int upgrades[]) {

		return (int) (upgrades[0] + upgrades[1]) / 100.0;
	}
}
