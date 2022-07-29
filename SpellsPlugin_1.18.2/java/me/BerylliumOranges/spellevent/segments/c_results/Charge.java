package me.BerylliumOranges.spellevent.segments.c_results;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
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

public class Charge extends EventSegment {
	public static final String THIS_SEGMENT_TYPE = SEGMENT_TYPE_RESULT;
	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 20;
	public static final double ORIGINAL_EXPLOSION = 1;
	public static final double ORIGINAL_TIME = 1.5;
	public static final int RARITY = 1;

	public static Guideline processSegment(SegmentProcess proc) {
		int[] upgrades = getUpgrades(proc);
		if (proc.subtractMana(MANA_COST) < 1)
			return new Guideline(proc, true, true);
		if (proc.getTargetsCopy().size() > 0) {
			LivingEntity victim = proc.getTargetsCopy().get(0);

			ArrayList<Entity> passengers = new ArrayList<Entity>();
			if (victim.getPassengers().size() > 0)
				passengers.addAll(victim.getPassengers());

			for (Entity ent : passengers) {
				victim.removePassenger(ent);
			}
			Vector dir = victim.getLocation().getDirection().multiply(1)
					.add(new Vector(0, victim.getVelocity().getY(), 0));
			victim.setAI(false);
			BukkitTask task = new BukkitRunnable() {
				public void run() {
					if (!victim.getType().equals(EntityType.PLAYER)) {
						victim.teleport(victim.getLocation().clone().add(dir));
					} else {
						victim.setVelocity(dir.clone().add(victim.getLocation().getDirection().multiply(0.85).setY(0)
								.add(new Vector(0, victim.getVelocity().getY(), 0))).normalize());
					}

					Location eyeAndInfrontLoc = victim.getLocation().add(victim.getLocation().getDirection().setY(0))
							.add(0, 1, 0);
					if (SpellPluginMain.generateRand(1, 0) == 1)
						victim.getWorld().spawnParticle(Particle.SMOKE_LARGE, eyeAndInfrontLoc.getX(),
								eyeAndInfrontLoc.getY(), eyeAndInfrontLoc.getZ(), 0, 0, 0, 0, 0);

					boolean found = false;
					for (Entity ent : eyeAndInfrontLoc.getWorld().getNearbyEntities(eyeAndInfrontLoc, 0.5, 1.2, 0.5)) {
						if (!ent.equals(victim) && ent instanceof LivingEntity && !passengers.contains(ent)) {
							found = true;
							break;

						}
					}
					if (found || eyeAndInfrontLoc.getBlock().getType().isSolid()) {
						chargeHit(victim, upgrades, passengers);
						this.cancel();
						findAndCallNext(proc);
						for (Entity ent : passengers)
							victim.addPassenger(ent);
					} else if (victim.isDead()) {
						this.cancel();

					}
				}
			}.runTaskTimer(SpellProcess.plugin, 5, 1);
			proc.getSpellSlot().getTasks().add(task);
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(SpellProcess.plugin, new Runnable() {
				public void run() {
					if (!task.isCancelled()) {
						chargeHit(victim, upgrades, passengers);
						for (Entity ent : passengers)
							victim.addPassenger(ent);
						task.cancel();
					}
				}
			}, (long) (getTime(upgrades) * 20));
		}
		return new Guideline(proc, true, false);
	}

	public static void chargeHit(LivingEntity victim, int[] upgrades, ArrayList<Entity> passengers) {
		victim.setInvulnerable(true);
		ArrayList<Entity> notInvul = new ArrayList<Entity>();
		for (Entity ent : passengers) {
			if (!ent.isInvulnerable()) {
				notInvul.add(ent);
				ent.setInvulnerable(true);
			}
		}
		for (Entity ent : victim.getNearbyEntities(3, 3, 3)) {
			if (ent instanceof LivingEntity) {
				LivingEntity liv = (LivingEntity) ent;
				liv.setNoDamageTicks(0);
			}
		}
		victim.getWorld().createExplosion(victim.getEyeLocation(), getExplosionSize(upgrades));
		for (Entity ent : victim.getNearbyEntities(3, 3, 3)) {
			if (ent instanceof LivingEntity) {
				LivingEntity liv = (LivingEntity) ent;
				liv.setNoDamageTicks(0);
			}
		}
		for (Entity ent : notInvul) {
			ent.setInvulnerable(false);
		}

		victim.setInvulnerable(false);
		victim.setAI(true);

	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "Charge");
		meta.setLore(Arrays.asList(
				ChatColor.WHITE + "The victim " + ChatColor.GREEN + "charges " + ChatColor.WHITE + "forward ",
				ChatColor.WHITE + "for " + ChatColor.GREEN + getTime(upgrades) + ChatColor.WHITE + " seconds and "
						+ ChatColor.GREEN + "explodes",
				ChatColor.WHITE + "Explosion Size: " + ChatColor.GREEN + getExplosionSize(upgrades), "",
				getCostText(MANA_COST)));

		meta.setLocalizedName(THIS_SEGMENT_TYPE + " [" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_RESULT);
		item.setItemMeta(meta);
		return item;
	}

	public static float getExplosionSize(int upgrades[]) {
		return (float) (Math.min(ORIGINAL_EXPLOSION + upgrades[0] * 0.2, 1.8) + upgrades[1] * 0.2F);
	}

	public static double getTime(int upgrades[]) {
		return Math.min(ORIGINAL_TIME + upgrades[0] * 0.25 + upgrades[1] * 0.25, 2.0);
	}

}
