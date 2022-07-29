package me.BerylliumOranges.spellevent.segments.c_results;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import me.BerylliumOranges.main.SpellPluginMain;
import me.BerylliumOranges.spellevent.other.Guideline;
import me.BerylliumOranges.spellevent.processes.SegmentProcess;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;
import net.md_5.bungee.api.ChatColor;

public class Flashbang extends EventSegment {
	public static final String THIS_SEGMENT_TYPE = SEGMENT_TYPE_RESULT;
	public static final String FLASHBANG_TAG = ChatColor.RED + "Flashbang";
	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 25;
	public static final int BLIND_TIME = 80;
	public static final int RARITY = 4;

	public static Guideline processSegment(SegmentProcess proc) {
		int[] upgrades = getUpgrades(proc);
		if (proc.subtractMana(MANA_COST) < 1)
			return new Guideline(proc, true, true);

		Location loc = proc.getSpellLocation().clone().add(0, 0.5, 0);
		Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
		FireworkMeta fwm = fw.getFireworkMeta();

		fwm.setPower(4);
		fwm.addEffect(FireworkEffect.builder().withColor(Color.WHITE).with(Type.BALL_LARGE).build());
		fw.addScoreboardTag(FLASHBANG_TAG);
		fw.setFireworkMeta(fwm);
//		fw.setSilent(true);
		fw.setBounce(true);
		fw.setInvulnerable(true);
		final int duration = getDuration(upgrades);
		BukkitTask task = new BukkitRunnable() {

			int count = 0;

			public void run() {
				fw.setVelocity(new Vector(0, 0.05, 0));
				if (count == 15)
					fw.detonate();
				if (count > 23) {
					ArrayList<Player> targets = new ArrayList<Player>();
					for (Entity target : SpellPluginMain.getNearbyEntities(proc.getSpellLocation(), 40)) {

						if (target instanceof Player) {
							Player p = (Player) target;
							if (p.hasLineOfSight(fw) && isInFront(fw, p)) {
								targets.add(p);
								p.damage(getDamage(upgrades));
							}
						}
						fw.setSilent(false);

						new BukkitRunnable() {
							int num = 0;

							public void run() {
								if (num < 17) {
									for (Player p : targets) {
										for (int x = 0; x < 360; x += 36) {
											Location loc = p.getEyeLocation().clone().add(Math.cos(Math.toRadians(x)),
													0, Math.sin(Math.toRadians(x)));
											loc.getWorld().spawnParticle(Particle.FLASH, loc, 5);
										}
									}
								}

								if (num == 20) {
									for (Player p : targets) {
										p.addPotionEffect(
												new PotionEffect(PotionEffectType.BLINDNESS, duration + 20, 10, true));
									}
								}
								if (num > 20) {
									for (Player p : targets) {
										p.stopAllSounds();
									}
								}
								if (num > duration) {
									this.cancel();
								}

								num++;
							}
						}.runTaskTimer(SpellPluginMain.getInstance(), 0, 0);

					}
					this.cancel();
				}
				count++;
			}
		}.runTaskTimer(SpellPluginMain.getInstance(), 0, 0);
		proc.getSpellSlot().getTasks().add(task);
		return new Guideline(proc, true, false);
	}

	private static boolean isInFront(Entity entity1, LivingEntity entity2) {
		Double yaw = 2 * Math.PI - Math.PI * entity2.getLocation().getYaw() / 180;
		Vector v = entity1.getLocation().toVector().subtract(entity2.getLocation().toVector());
		Vector r = new Vector(Math.sin(yaw), 0, Math.cos(yaw));
		float theta = r.angle(v);
		if (Math.PI / 2 < theta && theta < 3 * Math.PI / 2) {
			return false;
		}
		return true;
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "Flashbang");
		meta.setLore(Arrays.asList(
				ChatColor.WHITE + "Lasts for " + ChatColor.GREEN + getDuration(upgrades) / 20 + ChatColor.WHITE
						+ " seconds",
				ChatColor.WHITE + "Deals " + ChatColor.GREEN + "" + getDamage(upgrades) + ChatColor.WHITE + " damage",
				"", getCostText(MANA_COST)));

		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_RESULT);
		item.setItemMeta(meta);
		return item;
	}

	public static int getDuration(int upgrades[]) {
		return BLIND_TIME + (upgrades[0] + upgrades[1]) * 10;
	}

	public static double getDamage(int upgrades[]) {
		return 2 + (upgrades[0] + upgrades[1]) / 2.0;
	}
}
