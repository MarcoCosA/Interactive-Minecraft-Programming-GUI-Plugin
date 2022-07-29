package me.BerylliumOranges.spellevent.segments.b_triggers;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
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
import me.BerylliumOranges.spellevent.processes.SpellActions;
import me.BerylliumOranges.spellevent.processes.SpellProcess;
import me.BerylliumOranges.spellevent.segments.abstracts.EventTriggerSegment;
import me.BerylliumOranges.spellevent.segments.d_modifiers.PerfectDomain;

public final class DomainExpansion extends EventTriggerSegment {
	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 22;
	public static final int RADIUS = 8;
	public static final int RARITY = 3;
	public static final int TIME = 4;

	public static Guideline processSegment(SegmentProcess proc) {
		if (proc.subtractMana(MANA_COST) < 1)
			return new Guideline(proc, true, true);

		ArrayList<LivingEntity> targets = new ArrayList<LivingEntity>(proc.getTargetsCopy());
		final boolean casterWasTargeted = targets.contains(proc.getCaster());
		if (targets.isEmpty()) {
			targets.add(proc.getCaster());
		}

		int temp = 0;
		for (int i = proc.getProjectileModifiers().size() - 1; i >= 0; i--) {
			if (proc.getProjectileModifiers().get(i) instanceof PerfectDomain) {
				proc.getProjectileModifiers().remove(i);
				temp++;
			}
		}
		final int instances = temp;

		for (LivingEntity target : targets) {
			final int max = TIME * 20;
			final int radius = RADIUS + instances * PerfectDomain.BONUS_RADIUS;

			if (instances > 0) {
				runDomainExpansion(max, radius, max, target, casterWasTargeted, proc, null);
			} else {
				BukkitTask task = new BukkitRunnable() {
					int counter = (int) (instances > 0 ? max - (0.1 * max) : 0);

					public void run() {
						runDomainExpansion(max, radius, counter, target, casterWasTargeted, proc, this);
						counter++;
					}
				}.runTaskTimer(SpellProcess.plugin, 0, 0);
				proc.getSpellSlot().getTasks().add(task);
			}
		}
		return new Guideline(proc, false, true);
	}

	public static void runDomainExpansion(int max, int radius, int counter, LivingEntity target,
			boolean casterWasTargeted, SegmentProcess proc, BukkitRunnable task) {
		int intensity = (int) (counter / (TIME * 20.0) * 255);
		intensity = Math.min(intensity + 55, 255);
//		Color col = null;
//		int op = SpellProjectile.getColorOption(proc.getCaster(), target);
//		if (op == 0)
//			col = Color.fromRGB(intensity, intensity, 0);
//		else if (op == 1)
//			col = Color.fromRGB(0, intensity, 0);
//		else
//			col = Color.fromRGB(intensity, 0, 0);

		if (counter >= max) {

			for (Entity ent : SpellPluginMain.getNearbyEntities(target.getLocation(), radius)) {
				if (ent instanceof LivingEntity && (!ent.equals(proc.getCaster()) || casterWasTargeted)) {
					try {
						SegmentProcess clone = (SegmentProcess) proc.clone();
						clone.setSpellLocation(ent.getLocation());
						Vector v = ((LivingEntity) ent).getLocation().subtract(target.getLocation()).toVector();
						clone.getSpellLocation().setDirection(v);
						clone.setTarget((LivingEntity) ent);
						findAndCallNext(clone);
					} catch (Exception er) {
					}
				}
			}

//			for (int i = 0; i < 50; i++) {
			double range = 15;
			double hR = 15 / 2.0;
			double a = Math.random() * 2 * Math.PI;
//			double r = 1 * Math.sqrt(Math.random()) * RADIUS;
			Location loc = target.getLocation().add(0, 0.2, 0);
			int increment = Math.max(4, 14 - radius);
			for (int j = 0; j < 360; j += increment) {
				loc.getWorld().spawnParticle(Particle.REDSTONE,
						loc.clone().add(Math.cos(Math.toRadians(j)) * radius, 0, Math.sin(Math.toRadians(j)) * radius),
						0, 0, 0, 0, 5, new DustOptions(Color.fromRGB(intensity, intensity, 0), 2), true);
			}

			double x = radius * Math.cos(a);
			double z = radius * Math.sin(a);

//				Location loc = target.getLocation();
			Vector dir = target.getLocation().add(0, target.getHeight() / 2.0, 0).clone().subtract(loc).toVector();
//				loc.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, loc, 0, dir.getX() + x, dir.getY(),
//						dir.getZ() + z, 0.05 + (Math.random() * 0.05), null, true);

//				loc.getWorld().spawnParticle(Particle.REDSTONE, loc.clone().add(x, 0, z), 0, 0, 0, 0, 5,
//						new DustOptions(Color.fromRGB(intensity, intensity, 0), 1), true);
//			}
			if (task != null)
				task.cancel();
			return;
		}

		if (counter % 2 == 0) {
			Location loc = target.getLocation().add(0, target.getHeight() / 2.0, 0);
//			for (int i = 0; i < 20; i++) {
//			double r = 1 * Math.sqrt(Math.random()) * RADIUS;
			loc.add(new Vector(Math.sqrt(Math.random()) * (Math.random() > 0.5 ? 1 : -1),
					Math.sqrt(Math.random()) * (Math.random() > 0.5 ? 1 : -1),
					Math.sqrt(Math.random()) * (Math.random() > 0.5 ? 1 : -1)).multiply(0.5));
			loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 0, 0, 0, 0, 5,
					new DustOptions(Color.fromRGB(intensity, intensity, 0), 3), true);
//			}
		}
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "Domain Expansion");
		ArrayList<String> lore = new ArrayList<String>(Arrays.asList(
				ChatColor.YELLOW + "Targets " + ChatColor.WHITE + "all entities within ",
				"" + ChatColor.YELLOW + RADIUS + ChatColor.WHITE + " blocks " + ChatColor.WHITE + "after "
						+ ChatColor.YELLOW + TIME + ChatColor.WHITE + " seconds",
				ChatColor.DARK_GRAY + "Only targets the caster if they", ChatColor.DARK_GRAY + "were already a target",
				getCostText(MANA_COST) + " " + ChatColor.YELLOW + SpellActions.getArrowModelFromInt(dir)));
		lore.addAll(getMultiplierLore(MANA_COST_MULTIPLIER));
		meta.setLore(lore);
		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_TRIGGER);
		item.setItemMeta(meta);
		return item;
	}
}
