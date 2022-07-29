package me.BerylliumOranges.spellevent.segments.c_results;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import me.BerylliumOranges.main.SpellPluginMain;
import me.BerylliumOranges.spellevent.other.Guideline;
import me.BerylliumOranges.spellevent.processes.SegmentProcess;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;

public class SeismicWave extends EventSegment {
	public static final String THIS_SEGMENT_TYPE = SEGMENT_TYPE_RESULT;
	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 50;
	public static final int RARITY = 4;
	public static final int SIZE = 4;
	public static final int DAMAGE = 16;

	public static Guideline processSegment(SegmentProcess proc) {
		int[] upgrades = EventSegment.getUpgrades(proc);
		if (proc.subtractMana(MANA_COST) < 1)
			return new Guideline(proc, true, true);
		double size = getSize(upgrades);
		if (proc.getCaster() instanceof IronGolem)
			size *= 1.8;
		ArrayList<Block> allBlocks = new ArrayList<>();
		ArrayList<Block> currentBlocks = new ArrayList<>();
		ArrayList<ArrayList<Block>> blocksToChange = new ArrayList<ArrayList<Block>>();
		for (double x = 0; x < size; x++) {
			double i = 0;
			do {
				double angle = (Math.PI * 2) * (i / x);
				Vector vec;
				if (i < 0)
					vec = new Vector(0, 0, 0);
				else
					vec = new Vector(Math.cos(angle) * x, 0, Math.sin(angle) * x);
				Location loc = proc.getSpellLocation().getBlock().getLocation().add(0.5, 1, 0.5).clone().add(vec);
				// loc.getWorld().spawnParticle(Particle.LANDING_HONEY, loc, 0);

				for (int j = 0; j < 4; j++) {
					if (loc.getBlock().getType().isAir() || !loc.getBlock().getType().isSolid()
							|| loc.getBlock().getState() instanceof InventoryHolder) {
						loc.add(0, -1, 0);
					} else
						break;
				}

				if (!loc.getBlock().getType().isAir() && !(loc.getBlock().getState() instanceof InventoryHolder)) {
					if (!allBlocks.contains(loc.getBlock())) {
						allBlocks.add(loc.getBlock());
						currentBlocks.add(loc.getBlock());
					}
				}
				i += 0.1;
			} while (i < x);
			blocksToChange.add((ArrayList<Block>) currentBlocks.clone());
			currentBlocks.clear();
		}

		ArrayList<LivingEntity> hurt = new ArrayList<LivingEntity>();
		for (int x = 0; x < blocksToChange.size(); x++) {
			final int insideSize = x;
			BukkitTask task = new BukkitRunnable() {
				public void run() {
					for (Block b : blocksToChange.get(insideSize)) {
						if (b.getType().isAir())
							continue;
						FallingBlock fall = b.getWorld().spawnFallingBlock(b.getLocation().add(0.5, 0, 0.5), b.getBlockData());
						b.setType(Material.AIR);
						fall.setVelocity(new Vector(0, 0.2 + insideSize / (20.0), 0));

						for (Entity ent : SpellPluginMain.getNearbyEntities(fall.getLocation(), 1.7)) {
							if (ent instanceof LivingEntity && !ent.equals(proc.getCaster())) {
								LivingEntity liv = (LivingEntity) ent;
								if (!hurt.contains(liv)) {
									if (((proc.getCaster() instanceof IronGolem)) && (liv instanceof Villager || liv instanceof IronGolem)) {
									} else {
										double damage = getDamage(upgrades);
										if (proc.getCaster() instanceof IronGolem)
											damage *= 1.8;
										liv.damage(damage);
										hurt.add(liv);
									}
								}
							}
						}
					}
				}
			}.runTaskLater(SpellPluginMain.getInstance(), (long) (x * 2));
			proc.getSpellSlot().getTasks().add(task);
		}
		if (proc.getSpellLocation().distanceSquared(proc.getCaster().getLocation()) < 5)
			proc.getCaster().setVelocity(proc.getCaster().getVelocity().add(new Vector(0, 0.4, 0)));

		return new Guideline(proc, true, false);
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "Seismic Wave");
		meta.setLore(Arrays.asList(ChatColor.WHITE + "Deals " + ChatColor.GREEN + getDamage(upgrades) + ChatColor.WHITE + " damage with a",
				ChatColor.WHITE + "radius of " + ChatColor.GREEN + (int) getSize(upgrades) + ChatColor.WHITE + " blocks", "", getCostText(MANA_COST)));

		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_RESULT);
		item.setItemMeta(meta);
		return item;
	}

	public static double getDamage(int upgrades[]) {
		return DAMAGE + (upgrades[0] + upgrades[1]) * 2.0;
	}

	public static double getSize(int upgrades[]) {

		return (int) (SIZE + (upgrades[0] + upgrades[1]) / 1.5);
	}
}
