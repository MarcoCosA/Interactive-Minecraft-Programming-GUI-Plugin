package me.BerylliumOranges.spellevent.segments.d_modifiers;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustTransition;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.inventory.InventoryHolder;
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

public class Fiery extends EventSpellModifier {

	public static final ArrayList<Material> BLOCK_TYPES = new ArrayList<Material>(Arrays.asList(Material.OBSIDIAN, Material.FIRE, Material.COBWEB));
	public static final ArrayList<Material> BLOCK_TYPES_TO_DELETE = new ArrayList<Material>(Arrays.asList(Material.COBWEB, Material.OBSIDIAN));

	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST_MULTIPLIER = 1;
	public static final double MANA_COST = 10;
	public static final int SIZE = 1;
	public static final int RARITY = 4;
	public static final double COST_PER_BLOCK = SpellProjectile.BASE_COST * 10;

	public static boolean registered = false;

	public static Guideline processSegment(SegmentProcess proc) {
		if (proc.getY() < proc.getTimesProced().length) {
			Fiery path = new Fiery(proc);
			proc.incrementGeneration();
			if (proc.subtractMana(MANA_COST) < 1)
				return new Guideline(proc, true, true);
			proc.getProjectileModifiers().add(path);
		}
		return findAndCallNext(proc);
	}

	ArrayList<Block> blocks = new ArrayList<Block>();

	@Override
	public boolean applyProjectileModifier(SpellProjectile proj, boolean finalCall) {
		if (proj.getTicksLived() == 0) {
			proj.setCost(proj.getCost() + COST_PER_BLOCK);
			proj.setDustColor(new DustTransition(Color.fromRGB(222, 92, 50), Color.BLACK, SpellProjectile.calculateDustSize(proj.getHitbox())));
		}

		if (proj.getTicksLived() % 2 == 0) {
			double size = getSize(upgrades);

			for (int z = -1; z <= 1; z += 2) {
				Vector vec1 = proj.getVelocity().clone().normalize().rotateAroundY(z * 45).multiply(5.0);
				// Bukkit.broadcastMessage("vec is " + vec1);
				Location loc = proj.getLocation().clone().add(0, 1, 0);
				for (int i = 0; i < size; i++) {

					for (int j = 0; j < 5; j++) {
						if (loc.clone().add(0, -1, 0).getBlock().getType().isAir()) {
							loc.add(0, -1, 0);
						} else
							break;
					}
					if (loc.getBlock().getType().isAir()) {
						loc.getBlock().setType(Material.FIRE);
					}
					loc.add(vec1);
				}
			}
		}
		// for (double x = 0; x < size; x++) {
		// double i = 0;
		// do {
		// double angle = (Math.PI * 2) * (i / x);
		// Vector vec;
		// if (i < 0)
		// vec = new Vector(0, 0, 0);
		// else
		// vec = new Vector(Math.cos(angle) * x, 0, Math.sin(angle) * x);
		// Location loc = proj.getLocation().getBlock().getLocation().add(0.5,
		// 1, 0.5).clone().add(vec);
		// // loc.getWorld().spawnParticle(Particle.LANDING_HONEY, loc, 0);
		//
		// for (int j = 0; j < 4; j++) {
		// if (loc.clone().add(0, -1,
		// 0).getBlock().getType().equals(Material.FIRE)
		// || loc.clone().add(0, -1, 0).getBlock().getType().isAir()) {
		// loc.add(0, -1, 0);
		// } else
		// break;
		// }
		// if (x < size - 1.5) {
		// if (loc.getBlock().getType().equals(Material.FIRE)) {
		// loc.getBlock().setType(Material.AIR);
		// blocks.add(loc.getBlock());
		// }
		// } else if (loc.getBlock().getType().isAir() &&
		// !blocks.contains(loc.getBlock())) {
		// loc.getBlock().setType(Material.FIRE);
		// }
		// i += 0.1;
		// } while (i < x);
		//
		// }
		return false;

	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "Fiery Projectile");
		ArrayList<String> lore = new ArrayList<String>(Arrays.asList(ChatColor.WHITE + "Your next projectile is on", ChatColor.AQUA + "fire", "",
				getCostPerBlock(COST_PER_BLOCK), getCostText(MANA_COST) + " " + ChatColor.GRAY + SpellActions.getArrowModelFromInt(dir)));
		lore.addAll(getMultiplierLore(MANA_COST_MULTIPLIER));
		meta.setLore(lore);
		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_MODIFIER);
		item.setItemMeta(meta);
		return item;
	}

	public Fiery(SegmentProcess proc) {
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
