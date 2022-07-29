package me.BerylliumOranges.spellevent.segments.c_results;

import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
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
import net.md_5.bungee.api.ChatColor;

public class Sacrifice extends EventSegment {
	public static final String THIS_SEGMENT_TYPE = SEGMENT_TYPE_RESULT;
	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 90;
	public static final double TIME = 30;
	public static final int RARITY = 4;
	public static final int BASE_RADIUS = 7;
	public static final int SIDES = 5;
	public static final double HIGHT = 0.2;
	public static final int DAMAGE = 666;
	public static final int MINIMUM_HEALTH = 100;
	public static final DustOptions DUST = new DustOptions(Color.RED, 0.8F);
	public static final DustOptions BIG_DUST = new DustOptions(Color.RED, 10F);
	public static final String SACRIFICE_TAG = ChatColor.RED + "Sacrafice";

	public static Guideline processSegment(SegmentProcess proc) {
		int[] upgrades = getUpgrades(proc);
		float radius = BASE_RADIUS + getUpgrades(upgrades);
		double degreeIncrement = (float) 3 + (radius / 10.0);
		double increment = (float) Math.toRadians(degreeIncrement);

		if (proc.subtractMana(MANA_COST) < 1)
			return new Guideline(proc, true, true);
		ArrayList<LivingEntity> targets = (ArrayList<LivingEntity>) proc.getTargetsCopy().clone();
		BukkitTask task = new BukkitRunnable() {
			int count = 0;

			public void run() {
				for (LivingEntity target : targets) {
					if (target instanceof Player)
						target.setVelocity(target.getVelocity().multiply(0));
					if (count < TIME * 20 * 0.2 || count % 2 == 0) {
						float angle = (float) Math.toRadians(Math.min(count * 5, 360));
						for (float x = 0; x < angle; x += increment * increment * 7.0) {
							target.getWorld().spawnParticle(Particle.REDSTONE, target.getLocation().getX() + Math.cos(x) * radius * 1.1,
									target.getLocation().getY() + HIGHT, target.getLocation().getZ() + Math.sin(x) * radius * 1.1, 0, 0, 0.1, 0, 0,
									DUST, true);
						}
					}
					if (count > TIME * 20 * 0.2 && count % 2 == 0) {
						drawPentagram(target.getWorld(), target.getLocation().getX() - radius, target.getLocation().getY() + HIGHT,
								target.getLocation().getZ() - radius / 3.0, count, radius, degreeIncrement);
					}
				}

				count++;
				if (count > TIME * 20) {
					this.cancel();
					for (LivingEntity target : targets) {
						for (Entity ent : SpellPluginMain.getNearbyEntities(target.getLocation(), radius)) {
							// if (ent instanceof LivingEntity &&
							// ((LivingEntity) ent).getMaxHealth() <
							// MINIMUM_HEALTH) {
							LivingEntity liv = (LivingEntity) ent;
							target.getWorld().spawnParticle(Particle.REDSTONE, liv.getLocation().getX(), liv.getLocation().getY(),
									liv.getLocation().getZ(), 30, 0, 30, 0, 0, BIG_DUST, true);
							liv.setNoDamageTicks(0);
							liv.addScoreboardTag(SACRIFICE_TAG);
							liv.damage(DAMAGE);

							Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(SpellProcess.plugin, new Runnable() {
								public void run() {
									liv.getScoreboardTags().remove(SACRIFICE_TAG);
								}
							});
							// }
						}
					}
				}
			}
		}.runTaskTimer(SpellProcess.plugin, 0, 0);
		proc.getSpellSlot().getTasks().add(task);
		return new Guideline(proc, true, false);

	}

	private static void drawPentagram(World w, double x, double y, double z, int progress, double radius, double space) {
		double angle = 0;

		Path2D p = new Path2D.Float();
		p.moveTo(x, z);

		for (int i = 0; i < 5; i++) {
			double x2 = x + (Math.cos(angle) * radius * 2);
			double z2 = z + (Math.sin(-angle) * radius * 2);
			p.lineTo(x2, z2);

			drawLine(new Location(w, x, y, z), new Location(w, x2, y, z2), Math.toRadians(space * space), progress - TIME * 20 * 0.2);

			x = x2;
			z = z2;
			angle -= Math.toRadians(144);
			// If you want little flames...
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
		double distance = point1.distance(point2) * Math.min((progress) / ((TIME * 20.0) * 0.7), 1);
		Vector p1 = point1.toVector();
		Vector p2 = point2.toVector();
		Vector vector = p2.clone().subtract(p1).normalize().multiply(space);
		double length = 0;
		for (; length < distance; p1.add(vector)) {
			world.spawnParticle(Particle.REDSTONE, p1.getX(), p1.getY(), p1.getZ(), 0, 0, 0, 0, 0, DUST, false);
			length += space;
		}
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "Sacrafice");
		meta.setLore(Arrays.asList(ChatColor.WHITE + "Deals " + ChatColor.RED + DAMAGE + ChatColor.WHITE + " damage to entities with",
				ChatColor.WHITE + "less than " + ChatColor.RED + MINIMUM_HEALTH + "" + ChatColor.WHITE + " health and within " + ChatColor.RED
						+ (BASE_RADIUS + getUpgrades(upgrades)),
				ChatColor.WHITE + "blocks after " + ChatColor.RED + TIME + ChatColor.WHITE + " seconds", ChatColor.DARK_GRAY + "Can cause lag",
				getCostText(MANA_COST)));

		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_RESULT);
		item.setItemMeta(meta);
		return item;
	}

	public static int getUpgrades(int upgrades[]) {
		return (int) (upgrades[0] + upgrades[1]);
	}

}
