package me.BerylliumOranges.spellevent.segments.c_results;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import me.BerylliumOranges.spellevent.other.Guideline;
import me.BerylliumOranges.spellevent.processes.SegmentProcess;
import me.BerylliumOranges.spellevent.processes.SpellProcess;
import me.BerylliumOranges.spellevent.projectiles.SpellProjectile;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;

public class Mirror extends EventSegment {
	public static final String THIS_SEGMENT_TYPE = SEGMENT_TYPE_RESULT;
	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 25;
	public static final double ORIGINAL_TIME = 5.5;
	public static final double SIZE = 3.5;
	public static final double SQUARE_ROOT_SIZE = Math.sqrt(SIZE);
	public static final int RARITY = 1;

	public static ArrayList<Mirror> mirrors = new ArrayList<Mirror>();

//	public static List<Vector> nodes = Arrays.asList(new Vector(-1, -1, -1), new Vector(-1, -1, 1),
//			new Vector(-1, 1, -1), new Vector(-1, 1, 1), new Vector(1, -1, -1), new Vector(1, -1, 1),
//			new Vector(1, 1, -1), new Vector(1, 1, 1));

	public static Guideline processSegment(SegmentProcess proc) {
		int[] upgrades = getUpgrades(proc);
		if (proc.subtractMana(MANA_COST) < 1)
			return new Guideline(proc, true, true);

		mirrors.add(new Mirror(proc, proc.getSpellLocation(), getSize(upgrades), getTime(upgrades)));

		return new Guideline(proc, true, true);
	}

	public static void rotateNodes(List<Vector> v, double angleX, double angleY) {
		double sinX = Math.sin(angleX);
		double cosX = Math.cos(angleX);

		double sinY = Math.sin(angleY);
		double cosY = Math.cos(angleY);

		for (Vector node : v) {
			double x = node.getX();
			double y = node.getY();
			double z = node.getZ();

			node.setX(x * cosX - z * sinX);
			node.setZ(z * cosX + x * sinX);

			z = node.getZ();

			node.setY(y * cosY - z * sinY);
			node.setZ(z * cosY + y * sinY);
		}
	}

	public static boolean isMovingTowards(Vector testPoint, Vector objectPosition, Vector objectVelocity) {
		Vector toPoint = testPoint.clone().subtract(objectPosition); // a vector going from your obect to the point
		return toPoint.dot(objectVelocity) > 0;
//	    return dot(toPoint, objectVelocity) > 0;
	}

	private static Vector intersectPoint(Vector rayVector, Vector rayPoint, Vector planeNormal, Vector planePoint) {
		Vector diff = rayPoint.clone().subtract(planePoint);
		double prod1 = diff.clone().dot(planeNormal);
		double prod2 = rayVector.clone().dot(planeNormal);
		double prod3 = prod1 / prod2;
		return rayPoint.clone().subtract(rayVector.clone().multiply(prod3));
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "Mirror");

		ArrayList<String> lore = new ArrayList<String>(
				Arrays.asList(ChatColor.WHITE + "Reflects " + ChatColor.GREEN + "spell projectiles.",
						ChatColor.WHITE + "Lasts for " + ChatColor.GREEN + Math.floor(getTime(upgrades) * 100) / 100.0
								+ ChatColor.WHITE + " seconds"));

		lore.addAll(Arrays.asList("", getCostText(MANA_COST)));
		meta.setLore(lore);

		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_RESULT);
		item.setItemMeta(meta);
		return item;
	}

	public static double getTime(int upgrades[]) {
		return ORIGINAL_TIME + (upgrades[0] + upgrades[1]) / 2.0;
	}

	public static double getSize(int upgrades[]) {
		return SIZE + (upgrades[0] + upgrades[1]) / 2.0;
	}

	public SegmentProcess proc = null;

	protected Location spellLocation;
	protected Vector min;
	protected Vector max;
	protected Vector middle;

	protected int count = 0;
	protected double duration = 0;
	protected List<Vector> vectors;

	public Mirror(@Nullable SegmentProcess input, Location spellLocation, double size, double duration) {
		this.duration = duration;
		Vector vec1 = new Vector(size, size, size);
		Vector vec2 = new Vector(size, -size, size);
		Vector vec4 = new Vector(-size, -size, size);
		Vector vec3 = new Vector(-size, size, size);
		vectors = Arrays.asList(vec1, vec2, vec4, vec3);
		proc = input;
		Vector normalXZ = new Vector(proc.getSpellLocation().getDirection().getX(), 0,
				proc.getSpellLocation().getDirection().getZ()).normalize();
		Vector normalized = proc.getSpellLocation().getDirection().clone().normalize();
		rotateNodes(vectors, 0,
				Math.atan(-normalized.getY() / (Math.abs(normalized.getX()) + Math.abs(normalized.getZ()))));
		double angleXZ = Math.atan(normalXZ.getZ() / normalXZ.getX());
		if ((normalXZ.getX() < 0)) {
			angleXZ += Math.PI;
		}
		rotateNodes(vectors, angleXZ + Math.toRadians(-90), 0);
		this.spellLocation = spellLocation;

		min = spellLocation.toVector().add(new Vector(Math.min(vec1.getX(), vec4.getX()) - 0.005,
				Math.min(vec1.getY(), vec4.getY()) - 0.005, Math.min(vec1.getZ(), vec4.getZ()) - 0.005));
		max = spellLocation.toVector().add(new Vector(Math.max(vec1.getX(), vec4.getX()) + 0.005,
				Math.max(vec1.getY(), vec4.getY()) + 0.005, Math.max(vec1.getZ(), vec4.getZ()) + 0.005));

		middle = max.clone().multiply(0.5).add(min.clone().multiply(0.5));

		run();
	}

	public Mirror getInstance() {
		return this;
	}

	public void run() {
		BukkitTask task = new BukkitRunnable() {
			public void run() {
				count++;
				for (int i = 0; i < vectors.size(); i++) {
					Vector vector = vectors.get(i);

					Location loc = spellLocation.clone().add(vector);
					spellLocation.getWorld().spawnParticle(Particle.END_ROD, loc, 0);

					int next = i > vectors.size() - 2 ? 0 : i + 1;
					int last = i == 0 ? vectors.size() - 1 : i - 1;

					Vector dirToNext = vector.clone().subtract(vectors.get(next)).multiply(-0.1);
					Vector dirToLast = vector.clone().subtract(vectors.get(last)).multiply(-0.1);
					if (count % 1 == 0)
						for (Player p : Bukkit.getOnlinePlayers()) {
							p.spawnParticle(Particle.SQUID_INK, loc, 0, dirToNext.getX(), dirToNext.getY(),
									dirToNext.getZ());
							p.spawnParticle(Particle.SQUID_INK, loc, 0, dirToLast.getX(), dirToLast.getY(),
									dirToLast.getZ());
						}
				}
				if (count > duration * 20) {
					mirrors.remove(getInstance());
					this.cancel();
					return;
				}
			}
		}.runTaskTimer(SpellProcess.plugin, 0, 0);
		if (proc != null) {
			proc.getSpellSlot().getTasks().add(task);
		}

	}

	public boolean checkIfProjectileReflect(SpellProjectile proj) {
		if (proc != null && proj.getProcess().equals(proc)) {
			return false;
		}
		Vector intersection = intersectPoint(proj.getVelocity().clone().add(proj.getAcceleration()),
				proj.getLocation().toVector(), spellLocation.getDirection(), middle);
//
//		spellLocation.getWorld().spawnParticle(Particle.CLOUD, intersection.getX(), intersection.getY(),
//				intersection.getZ(), 0);
//
//		double velLength = proj.getVelocity().length();
//		if (intersection.isInAABB(min, max)) {
//			Bukkit.broadcastMessage(ChatColor.GOLD + "intersects "
//					+ intersection.distance(proj.getLocation().toVector()) + ", length " + proj.getVelocity().clone()
//							.normalize().multiply(SpellProjectile.DISTANCE_BETWEEN_CHECKS).length());
////
//			Bukkit.broadcastMessage("direction " + intersection.clone()
//					.subtract(proj.getLocation().clone().add(proj.getVelocity()).toVector()).length());
//
//			Bukkit.broadcastMessage("is moving towards "
//					+ isMovingTowards(intersection, proj.getLocation().toVector(), proj.getVelocity()));
//
//		}

		Vector next = proj.getVelocity().clone().normalize().multiply(SpellProjectile.DISTANCE_BETWEEN_CHECKS);
		if (intersection.isInAABB(min, max)
				&& intersection.distanceSquared(proj.getLocation().toVector()) < next.lengthSquared() + 0.001
				&& isMovingTowards(intersection, proj.getLocation().clone().toVector(), next)) {
			Vector reflection = proj.getVelocity().clone().subtract(spellLocation.getDirection().clone()
					.multiply(proj.getVelocity().clone().dot(spellLocation.getDirection()) * 2));
			Location loc = proj.getLocation().clone();
//			loc.setX(intersection.getX());
//			loc.setY(intersection.getY());
//			loc.setZ(intersection.getZ());
//		loc.getWorld().spawnParticle(Particle.CLOUD, loc, 0);
			Location interLoc = new Location(proj.getLocation().getWorld(), intersection.getX(), intersection.getY(),
					intersection.getZ());
			interLoc.setDirection(reflection);
			proj.setReflected(true);
			proj.setVelocity(reflection);
			proj.getProcess().getSpellLocation().setDirection(reflection);
			proj.getProcess().ticksNotToReflect += 10 + proj.getVelocity().length() * 5;
//			proj.getLocation()
//					.add(proj.getVelocity().clone().normalize().multiply(SpellProjectile.DISTANCE_BETWEEN_CHECKS));

			return true;
		}
		return false;
	}

	public static Vector CalculateReflection(Vector velocity, Vector direction) {
		return velocity.clone().subtract(direction.clone().multiply(velocity.dot(direction) * 2));
	}

}
