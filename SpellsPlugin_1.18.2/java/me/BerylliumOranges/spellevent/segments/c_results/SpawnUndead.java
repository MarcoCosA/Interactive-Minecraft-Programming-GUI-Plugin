package me.BerylliumOranges.spellevent.segments.c_results;

import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Particle.DustTransition;
import org.bukkit.World;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import me.BerylliumOranges.spellevent.other.Guideline;
import me.BerylliumOranges.spellevent.processes.SegmentProcess;
import me.BerylliumOranges.spellevent.processes.SpellProcess;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;

public class SpawnUndead extends EventSegment {
	public static final String THIS_SEGMENT_TYPE = SEGMENT_TYPE_RESULT;
	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 90;
	public static final double TIME = 7;
	public static final int RARITY = 4;
	public static final int BASE_RADIUS = 8;
	public static final int SIDES = 5;
	public static final double HIGHT = 0.2;
	public static final int DAMAGE = 1000;
	public static final DustOptions DUST = new DustOptions(Color.AQUA, 0.5F);
	public static final DustOptions BIG_DUST = new DustOptions(Color.AQUA, 10F);
	public static final DustTransition AQUA_TO_WHITE = new DustTransition(Color.AQUA, Color.WHITE, 0.85F);
	public static final String SACRIFICE_TAG = ChatColor.RED + "Sacrafice";
	public static final List<Class<? extends LivingEntity>> POSSIBLE_SPAWNS = Arrays.asList(Creeper.class, Zombie.class, Skeleton.class);

	public static Guideline processSegment(SegmentProcess proc) {
		int[] upgrades = getUpgrades(proc);
		float radius = BASE_RADIUS + getUpgrades(upgrades);
		double degreeIncrement = (float) 3 + (radius / 10.0);
		double increment = (float) Math.toRadians(degreeIncrement);
		// Vector startingPoint = new Vector(1, 0,
		// 1).normalize().multiply(radius);

		if (proc.subtractMana(MANA_COST) < 1)
			return new Guideline(proc, true, true);
		ArrayList<LivingEntity> targets = (ArrayList<LivingEntity>) proc.getTargetsCopy().clone();
		BukkitTask task = new BukkitRunnable() {
			int count = 0;
			ArrayList<Location> locs = new ArrayList<Location>();

			public void run() {
				// LivingEntity target
				Location location = proc.getSpellLocation();
				// target.setVelocity(target.getVelocity().multiply(0));
				if (count % 2 == 0) {
					for (int i = 0; i < Math.min(count * 0.1, 20); i++) {
						double a = Math.random() * 2 * Math.PI;
						double r = 1 * Math.sqrt(Math.random()) * radius;

						double x = r * Math.cos(a);
						double z = r * Math.sin(a);
						// target.getWorld().spawnParticle(Particle.SOUL,
						// target.getLocation().getX() + x,
						// target.getLocation().getY() + HIGHT + count * 0.01,
						// target.getLocation().getZ() + y,
						// 1, 0, 1, 0, 0, null, true);

						if (i == 0 && count % 20 == 0)
							locs.add(getNearestAirBlock(
									new Location(location.getWorld(), location.getX() + x, location.getY() - 1, location.getZ() + z)));
					}
				}
				for (int i = locs.size() - 1; i >= 0; i--) {
					Location loc = locs.get(i);
					loc.getWorld().spawnParticle(Particle.REDSTONE, loc.getX(), loc.getY() + 1, loc.getZ(), 1, 0.1, 0.5, 0.1, 1, BIG_DUST);
					if (Math.random() > 0.99) {
						loc.getWorld().spawn(loc, POSSIBLE_SPAWNS.get((int) (Math.random() * POSSIBLE_SPAWNS.size())));
						locs.remove(i);
					}
				}
				if (count < TIME * 20 * 0.2 || count % 2 == 0) {
					float angle = (float) Math.toRadians(Math.min(count * 25, 360));
					for (float x = 0; x < angle; x += increment * increment * 7.0) {
						location.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, location.getX() + Math.cos(x) * radius * 1.1,
								location.getY() + HIGHT, location.getZ() + Math.sin(x) * radius * 1.1, 0, 0, 0.1, 0, 0, AQUA_TO_WHITE, true);

					}
				}
				if (count % 5 == 0) {
					// int rotations = 0;
					// double pentagramAngle = (double) Math.toRadians((720) /
					// SIDES);
					// double pentagramAngle2 = (double) Math.toRadians(108);
					// Vector v = STARTING_POINT.clone().multiply(INCREMENT);
					//// v.rotateAroundY(pentagramAngle / -8.0);
					//// Vector loc = new Vector(0, 0, 0);
					// Vector loc = new Vector(-RADIUS * Math.cos(pentagramAngle
					// / (3.0 / 8.0)) + 0.05, 0,
					// -RADIUS * Math.sin(pentagramAngle / (3.0 / 8.0)) - 0.05);
					// Vector actualStart1 = loc.clone();
					// Vector actualStart2 = new Vector(-RADIUS *
					// Math.cos(pentagramAngle / (3.0 / 8.0)), 0,
					// -RADIUS * Math.sin(pentagramAngle / (3.0 / 8.0)));
					// for (double j = 0; j < (count - 40) / 10.0; j +=
					// INCREMENT) {
					//
					// if ((int) ((j) / (1.15 * Math.sin(pentagramAngle2 / 2.0)
					// * RADIUS)) > rotations) {
					// rotations = (int) ((j) / (1.15 * Math.sin(pentagramAngle2
					// / 2.0) * RADIUS));
					//
					// Bukkit.broadcastMessage(
					// "The length of one side is " +
					// actualStart1.clone().subtract(loc).length());
					// actualStart1 = loc.clone();
					// if (rotations > SIDES - 1)
					// break;
					// v.rotateAroundY(pentagramAngle);
					// }
					// loc.add(v);
					// target.getWorld().spawnParticle(Particle.REDSTONE,
					// target.getLocation().getX() + loc.getX(),
					// target.getLocation().getY() + loc.getY() + 0.2,
					// target.getLocation().getZ() + loc.getZ(), 0, 0, 0, 0, 0,
					// DUST, true);
					//
					// }
					// drawPentagram(target.getWorld(),
					// target.getLocation().getX() - radius,
					// target.getLocation().getY() + HIGHT,
					// target.getLocation().getZ() - radius / 3.0, count,
					// radius, degreeIncrement);
					// for (float k = 0; k < angle; k += 0.4F * Math.PI) {
					// // k is the angle, multiple of 36 degrees
					// for (float j = 0; j < RADIUS; j += INCREMENT * 4) {
					// target.getWorld().spawnParticle(Particle.REDSTONE,
					// target.getLocation().getX() + Math.cos(k) * j -
					// (Math.cos(k) * RADIUS),
					// target.getLocation().getY() + 0.2,
					// target.getLocation().getZ() + Math.sin(k) * j -
					// (Math.sin(k) * RADIUS),
					// 0, 0, 0, 0, 0, DUST);
					// }
					// }
					//

				}

				count++;
				if (count > (TIME * 20) + getUpgrades(upgrades) * 20) {
					this.cancel();

					for (Location loc : locs) {
						loc.getWorld().spawnParticle(Particle.REDSTONE, loc.getX(), loc.getY() + 1, loc.getZ(), 1, 0.1, 0.5, 0.1, 1, BIG_DUST);
						loc.getWorld().spawn(loc, POSSIBLE_SPAWNS.get((int) (Math.random() * POSSIBLE_SPAWNS.size())));
					}
					// target.getWorld().spawnParticle(Particle.REDSTONE,
					// liv.getLocation().getX(),
					// liv.getLocation().getY(), liv.getLocation().getZ(), 30,
					// 0, 30, 0, 0, BIG_DUST,
					// true);
					// liv.setNoDamageTicks(0);
					// liv.addScoreboardTag(SACRIFICE_TAG);
					// liv.damage(DAMAGE);

					// Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(SpellProcess.plugin,
					// new Runnable() {
					// public void run() {
					// liv.getScoreboardTags().remove(SACRIFICE_TAG);
					// }
					// });
				}
			}
		}.runTaskTimer(SpellProcess.plugin, 0, 0);
		proc.getSpellSlot().getTasks().add(task);
		return new Guideline(proc, true, false);

	}

	public static Location getNearestAirBlock(Location location) {
		// Location location = player.getLocation().clone(); // clone the
		// location so we can modify it without issues
		for (int y = location.getBlockY(); y > 0; y--) { // get current Y
															// coordinate, go
															// down until we hit
															// bedrock (0)
			if (!location.add(0, 1, 0).getBlock().getType().isSolid() && !location.clone().add(0, 1, 0).getBlock().getType().isSolid())
				return location.getBlock().getLocation().add(0.5, 0, 0.5);
		}
		return location;
	}

	private static void drawPentagram(World w, double x, double y, double z, int progress, double radius, double space) {
		double angle = 0;

		Path2D p = new Path2D.Float();
		p.moveTo(x, z);

		for (int i = 0; i < 9; i++) {
			double x2 = x + (Math.cos(angle) * radius * 2);
			double z2 = z + (Math.sin(-angle) * radius * 2);
			p.lineTo(x2, z2);

			drawLine(new Location(w, x, y, z), new Location(w, x2, y, z2), Math.toRadians(space * space), progress - TIME * 20 * 0.1);

			x = x2;
			z = z2;
			angle -= Math.toRadians(160);

			// if (progress / (TIME * 20.0) > 0.6 && progress / (TIME * 20.0) <
			// 0.9) {
			// w.spawnParticle(Particle.SMALL_FLAME, x, y + 0.5, z, 0, 0, 0.1,
			// 0, 1, null, true);
			// }

		}
		p.closePath();

	}

	public static void drawLine(Location point1, Location point2, double space, double progress) {
		World world = point1.getWorld();
		double distance = point1.distance(point2) * Math.min((progress) / ((TIME * 20.0) * 0.6), 1);
		Vector p1 = point1.toVector();
		Vector p2 = point2.toVector();
		Vector vector = p2.clone().subtract(p1).normalize().multiply(space);
		double length = 0;
		for (; length < distance; p1.add(vector)) {
			world.spawnParticle(Particle.SOUL, p1.getX(), p1.getY(), p1.getZ(), 0, 0, 0, 0, 0, null, false);
			length += space;
		}
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "Spawn Undead");
		meta.setLore(Arrays.asList(ChatColor.WHITE + "Deals " + ChatColor.RED + DAMAGE + ChatColor.WHITE + " damage to all entities ",
				ChatColor.WHITE + "within " + ChatColor.RED + (BASE_RADIUS + getUpgrades(upgrades)) + ChatColor.WHITE + " blocks after "
						+ ChatColor.RED + TIME + ChatColor.WHITE + " seconds",
				ChatColor.DARK_GRAY + "Can cause lag", getCostText(MANA_COST)));

		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_RESULT);
		item.setItemMeta(meta);
		return item;
	}

	public static int getUpgrades(int upgrades[]) {
		return (int) (upgrades[0] + upgrades[1]);
	}

}
