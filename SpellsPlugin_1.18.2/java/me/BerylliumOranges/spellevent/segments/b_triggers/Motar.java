package me.BerylliumOranges.spellevent.segments.b_triggers;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import me.BerylliumOranges.spellevent.other.Guideline;
import me.BerylliumOranges.spellevent.processes.SegmentProcess;
import me.BerylliumOranges.spellevent.processes.SpellActions;
import me.BerylliumOranges.spellevent.processes.SpellProcess;
import me.BerylliumOranges.spellevent.projectiles.SpellProjectile;
import me.BerylliumOranges.spellevent.segments.abstracts.EventTriggerSegment;
import me.BerylliumOranges.spellevent.segments.d_modifiers.ImpactProjectile;

public final class Motar extends EventTriggerSegment {
	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 10;

	public static final int RARITY = 5;
	public static final boolean IS_PROJECTILE = true;

	public static Guideline processSegment(SegmentProcess proc) {

		if (proc.subtractMana(MANA_COST) < 1)
			return new Guideline(proc, true, true);

		Location center = proc.getSpellLocation().clone().add(0, 0.3, 0);

		SpellProjectile proj = new SpellProjectile(proc, proc.getSpellLocation(),
				proc.getSpellLocation().getDirection());
		proj.setCancelOnTarget(false);
		proj.setLocation(proc.getSpellLocation().add(0, 100, 0));
		proj.setLifespan(proj.getLifespan() * 2);
		proj.setVelocity(new Vector(0, -1, 0).multiply(proj.getVelocity().length()));
		proj.setHitbox(ImpactProjectile.HITBOX + 0.5);

		BukkitTask task = new BukkitRunnable() {
			int time = 0;
			private static final int MAX_TIME = 30;

			public void run() {
				time++;
				if (time > MAX_TIME)
					time = 0;
				if (proj.isAlive()) {
					if (time % 4 == 0) {
						Location temp = null;
						for (int j = 0; j < 4; j++) {
							temp = center.clone()
									.add(getParticlesDirection(j).multiply((MAX_TIME - time + 4) / (MAX_TIME * 0.5)));
//							temp.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, temp.getX(), temp.getY(),
//									temp.getZ(), 0, 0, 0, 0, 0, SpellProjectile.getDustParticle(proc.getCrit(), i, SpellProjectile.BASE_HITBOX), true);
//							SpellProjectile.getColorOption(null, null);
							SpellProjectile.displayProjectileParticle(proc, temp, SpellProjectile.BASE_HITBOX * 1.25);
						}
					}
				} else {
					this.cancel();
				}
			}
		}.runTaskTimer(SpellProcess.plugin, 0, 0);
		proc.getSpellSlot().getTasks().add(task);

		return new Guideline(proc, false, true);
	}

	private static Vector getParticlesDirection(int i) {
		switch (i) {
		case 1:
			return new Vector(1, 0, 0);
		case 2:
			return new Vector(-1, 0, 0);
		case 3:
			return new Vector(0, 0, 1);
		default:
			return new Vector(0, 0, -1);
		}
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "Mortar");
		ArrayList<String> lore = new ArrayList<String>(Arrays.asList(ChatColor.WHITE + "Upgrades the spell",
				ChatColor.WHITE + "straight down", "", getCostPerBlock(SpellProjectile.BASE_COST),
				getCostText(MANA_COST) + " " + ChatColor.YELLOW + SpellActions.getArrowModelFromInt(dir)));
//		lore.addAll();
		meta.setLore(lore);
		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_TRIGGER);
		item.setItemMeta(meta);
		return item;
	}
}
