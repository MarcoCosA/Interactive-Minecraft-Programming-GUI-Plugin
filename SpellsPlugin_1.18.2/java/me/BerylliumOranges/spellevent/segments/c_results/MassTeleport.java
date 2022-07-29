package me.BerylliumOranges.spellevent.segments.c_results;

import java.awt.geom.Path2D;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustTransition;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import me.BerylliumOranges.main.SpellPluginMain;
import me.BerylliumOranges.spellevent.other.Guideline;
import me.BerylliumOranges.spellevent.processes.SegmentProcess;
import me.BerylliumOranges.spellevent.processes.SpellProcess;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;

public class MassTeleport extends EventSegment {
	public static final String THIS_SEGMENT_TYPE = SEGMENT_TYPE_RESULT;
	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 90;
	public static final double TIME = 4;
	public static final int RARITY = -1;
	public static final int BASE_RADIUS = 3;
	public static final int SIDES = 3;
	public static final double HIGHT = 0.2;
	public static final DustTransition DUST_TRANSITION_FROM = new DustTransition(Color.AQUA, Color.BLUE, 1.1F);
	public static final DustTransition DUST_TRANSITION_TO = new DustTransition(Color.AQUA, Color.BLUE, 1.1F);
	public static final DustTransition DUST_TRANSITION_TO_BIG = new DustTransition(Color.AQUA, Color.BLUE, 1.95F);

	public static Guideline processSegment(SegmentProcess proc) {
		int[] upgrades = getUpgrades(proc);
		float radius = getRadius(upgrades);
		double degreeIncrement = (float) 4 + (radius / 9.0);
		double increment = (float) Math.toRadians(degreeIncrement);
		int time = getTime(upgrades) * 20;
		// Vector startingPoint = new Vector(1, 0, 1).normalize().multiply(radius);

		Location from = proc.getCaster().getLocation();
		Location to = proc.getSpellLocation();

		if (proc.subtractMana(MANA_COST) < 1)
			return new Guideline(proc, true, true);
		BukkitTask task = new BukkitRunnable() {
			int count = 0;

			public void run() {
				if (count < time * 0.2 || count % 2 == 0) {
					float angle = (float) Math.toRadians(Math.min((count / (double) time) * 360, 360));
					for (float x = 0; x < angle; x += increment * increment * 7.0) {

						from.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION,
								from.getX() + Math.cos(x) * radius * 1.2, from.getY() + HIGHT,
								from.getZ() + Math.sin(x) * radius * 1.2, 0, 0, 0.1, 0, 0, DUST_TRANSITION_FROM, true);

						proc.getSpellLocation().getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION,
								to.getX() + Math.cos(x) * radius * 1.2, to.getY() + HIGHT,
								to.getZ() + Math.sin(x) * radius * 1.2, 0, 0, 0.1, 0, 0, DUST_TRANSITION_TO, true);

					}
				}
				if (count % 2 == 0) {
					drawPentagram(from.getWorld(), from.getX() - radius, from.getY() + HIGHT,
							from.getZ() - radius / 2.0 - 0.25, count, radius,
							degreeIncrement * (0.9 + (Math.random() * 0.2)), DUST_TRANSITION_FROM);
					drawPentagram(to.getWorld(), to.getX() - radius, to.getY() + HIGHT, to.getZ() - radius / 2.0 - 0.25,
							count, radius, degreeIncrement * (0.9 + (Math.random() * 0.2)), DUST_TRANSITION_TO);
				}
				count++;
				for (Entity ent : SpellPluginMain.getNearbyEntities(from, radius)) {
					Location loc = to.clone().add(ent.getLocation().subtract(from).toVector());
					if (ent instanceof LivingEntity)
						loc.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, loc.getX(), loc.getY() + 0.5,
								loc.getZ(), 1, 0.1, 0.5, 0.1, 1, DUST_TRANSITION_TO_BIG);
				}
				if (count > time) {
					this.cancel();
					for (Entity ent : SpellPluginMain.getNearbyEntities(from, radius)) {
						ent.teleport(to.clone().add(ent.getLocation().subtract(from).toVector()));
					}
				}
			}
		}.runTaskTimer(SpellProcess.plugin, 0, 0);
		proc.getSpellSlot().getTasks().add(task);
		return new Guideline(proc, true, false);

	}

	public static Location getNearestAirBlock(Location location) {
//		Location location = player.getLocation().clone(); // clone the location so we can modify it without issues
		for (int y = location.getBlockY(); y > 0; y--) { // get current Y coordinate, go down until we hit bedrock (0)
			if (!location.add(0, 1, 0).getBlock().getType().isSolid()
					&& !location.clone().add(0, 1, 0).getBlock().getType().isSolid())
				return location.getBlock().getLocation().add(0.5, 0, 0.5);
		}
		return location;
	}

	private static void drawPentagram(World w, double x, double y, double z, int progress, double radius, double space,
			DustTransition dust) {
		double angle = 0;

		Path2D p = new Path2D.Float();
		p.moveTo(x, z);

		for (int i = 0; i < SIDES; i++) {
			double x2 = x + (Math.cos(angle) * radius * 2);
			double z2 = z + (Math.sin(-angle) * radius * 2);
			p.lineTo(x2, z2);

			drawLine(new Location(w, x, y, z), new Location(w, x2, y, z2), Math.toRadians(space * space), progress,
					dust);

			x = x2;
			z = z2;
			angle -= Math.toRadians(120);

//			if (progress / (TIME * 20.0) > 0.6 && progress / (TIME * 20.0) < 0.9) {
//				w.spawnParticle(Particle.DUST_COLOR_TRANSITION, x, y + 0.5, z, 0, 0, 0.1, 0, 1, DUST_TRANSITION, true);
//			}

		}
		p.closePath();

	}

	public static void drawLine(Location point1, Location point2, double space, double progress, DustTransition dust) {
		World world = point1.getWorld();
		double distance = point1.distance(point2);
		Vector p1 = point1.toVector();
		Vector p2 = point2.toVector();
		Vector vector = p2.clone().subtract(p1).normalize().multiply(space);
		double length = 0;
		for (; length < distance; p1.add(vector)) {
			world.spawnParticle(Particle.DUST_COLOR_TRANSITION, p1.getX(), p1.getY(), p1.getZ(), 0, 0, 0, 0, 0, dust,
					false);
			length += space;
		}
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "Mass Teleport");
		meta.setLore(Arrays.asList(ChatColor.GREEN + "" + getRadius(upgrades) + ChatColor.WHITE + " block radius",
				ChatColor.DARK_GRAY + "Can cause lag", getCostText(MANA_COST)));

		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_RESULT);
		item.setItemMeta(meta);
		return item;
	}

	public static int getRadius(int upgrades[]) {
		return (int) ((upgrades[0] + upgrades[1]) / 2.0F + BASE_RADIUS);
	}

	public static int getTime(int upgrades[]) {
		return (int) ((upgrades[0] + upgrades[1]) / 4.0F + TIME);
	}

}
