package me.BerylliumOranges.spellevent.segments.b_triggers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Sound;
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
import me.BerylliumOranges.spellevent.processes.SpellActions;
import me.BerylliumOranges.spellevent.processes.SpellProcess;
import me.BerylliumOranges.spellevent.projectiles.SpellProjectile;
import me.BerylliumOranges.spellevent.segments.abstracts.EventTriggerSegment;

public final class BlackHole extends EventTriggerSegment {
	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 300;

	public static final int RARITY = 3;
	public static final int TIME = 7;
	public static int numberOfBlackHoles = 0;

	public static Guideline processSegment(SegmentProcess proc) {
		if (proc.subtractMana(MANA_COST) < 1)
			return new Guideline(proc, true, true);
		Location l = proc.getSpellLocation().add(0, 1.5, 0);
		double effectRadius = 15;
		numberOfBlackHoles++;
		BukkitTask task = new BukkitRunnable() {
			int counter = 0;

			public void run() {
				if (counter % 4 == 0) {
					l.getWorld().playSound(l, Sound.AMBIENT_CRIMSON_FOREST_MOOD, 2F, -1.5F);
				}
				counter++;
				for (SpellProjectile spell : SpellProjectile.allProjectiles) {
					double distance = spell.getLocation().distance(l);
					if (distance < effectRadius + 3) {
						Vector direction = spell.getLocation().clone().subtract(l).toVector().normalize().multiply(-1 / distance);
						Vector vel = spell.getVelocity().clone().add(direction);
						spell.setVelocity(vel);
					}
				}

				for (Entity ent : SpellPluginMain.getNearbyEntities(l, effectRadius)) {
					if ((ent instanceof Player)
							&& (((Player) ent).getGameMode().equals(GameMode.SPECTATOR) || ((Player) ent).getGameMode().equals(GameMode.CREATIVE)))
						continue;
					double distance = Math.max(8, Math.pow(ent.getLocation().distance(l), 1.5));

					Vector direction = ent.getLocation().clone().subtract(l).toVector().normalize().multiply(-2 / distance);
					Vector vel = ent.getVelocity().clone().add(direction);
					ent.setVelocity(vel);
					if (ent instanceof LivingEntity) {
						LivingEntity liv = (LivingEntity) ent;
						liv.setFallDistance(0);
					}
				}
				for (int i = 0; i < 20 - Math.min(numberOfBlackHoles, 10); i++) {
					double range = 15;
					double hR = 15 / 2.0;
					Location loc = l.clone()
							.add((new Vector((Math.random() * range) - hR, (Math.random() * range) - hR, (Math.random() * range) - hR)).normalize());

					loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 1, 0F, 0F, 0F, 0.001, new DustOptions(Color.BLACK, 3), true);
				}
			}
		}.runTaskTimer(SpellProcess.plugin, 1, 1);
		proc.getSpellSlot().getTasks().add(task);
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(SpellProcess.plugin, new Runnable() {
			public void run() {
				proc.getTargetsCopy().clear();
				for (Entity ent : SpellPluginMain.getNearbyEntities(l, effectRadius)) {
					if (ent instanceof LivingEntity) {
						proc.getTargetsCopy().add((LivingEntity) ent);
					}
				}
				numberOfBlackHoles--;
				task.cancel();
				findAndCallNext(proc);
			}
		}, (long) TIME * 20);
		return new Guideline(proc, false, true);
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "Black Hole");
		ArrayList<String> lore = new ArrayList<String>(Arrays.asList(ChatColor.YELLOW + "Targets " + ChatColor.WHITE + "nearby entities", "",
				getCostText(MANA_COST) + " " + ChatColor.YELLOW + SpellActions.getArrowModelFromInt(dir)));
		lore.addAll(getMultiplierLore(MANA_COST_MULTIPLIER));
		meta.setLore(lore);
		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_TRIGGER);
		item.setItemMeta(meta);
		return item;
	}
}
