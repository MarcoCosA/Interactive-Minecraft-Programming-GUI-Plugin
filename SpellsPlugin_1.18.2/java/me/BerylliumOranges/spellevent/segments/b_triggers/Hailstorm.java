package me.BerylliumOranges.spellevent.segments.b_triggers;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
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

public final class Hailstorm extends EventTriggerSegment {
	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 10;
	public static final int RARITY = 3;
	public static final int PROJECTILE_COUNT = 70;
	public static final int BONUS_PROJECTILE_COUNT = 70;
	public static final int MAX_HEIGHT = 150 - 16;
	public static final int COST_REDUCTION = 60;
	public static final boolean IS_PROJECTILE = true;

	public static Guideline processSegment(SegmentProcess proc) {
		int num = PROJECTILE_COUNT;
		if (proc.getSpellLocation().getY() > MAX_HEIGHT) {
			num += BONUS_PROJECTILE_COUNT;
		}
		proc.addTimesCloned(num);

		if (proc.subtractMana(MANA_COST) < 1)
			return new Guideline(proc, true, true);

		if (num > PROJECTILE_COUNT)
			proc.getCaster().getWorld().playSound(proc.getCaster().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
					0.3F, 0);

		for (int i = 0; i < num; i++) {
			Location loc = proc.getSpellLocation().clone().add(0, 16, 0);

			BukkitTask task = new BukkitRunnable() {
				public void run() {
					double a = Math.random() * 2 * Math.PI;
					double r = 1 * Math.sqrt(Math.random());

					double x = r * Math.cos(a);
					double y = r * Math.sin(a);
					Vector dir = new Vector(0, 0, 0);
					if (proc.getSpellLocation().getY() > MAX_HEIGHT)
						dir.add(new Vector(x, -6, y).normalize());
					else
						dir.add(new Vector(x, -1, y));

					try {
						SegmentProcess clone = (SegmentProcess) proc.clone();
						loc.setDirection(dir.clone());
						SpellProjectile proj = new SpellProjectile(clone, loc.clone(), dir.clone());
						proj.setCost(0);
						proc.setManaMultiplier(1 - (COST_REDUCTION / 100.0));
						proj.getVelocity().multiply((Math.random() * 0.2) + 1);
						proj.setLifespan((int) (proj.getLifespan() * 1.5));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}.runTaskLater(SpellProcess.plugin, (long) (i + (10 * Math.random())));
			proc.getSpellSlot().getTasks().add(task);
		}
		return new Guideline(proc, true, true);
	}

	// Oh you found me. Hello I'm the famous inverse square root function, but in
	// java! I'm also not used...
	public static double invSqrt(double x) {
		double xhalf = 0.5d * x;
		long i = Double.doubleToLongBits(x);
		i = 0x5fe6ec85e7de30daL - (i >> 1);
		x = Double.longBitsToDouble(i);
		x *= (1.5d - xhalf * x * x);
		return x;
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "Hailstorm");
		ArrayList<String> lore = new ArrayList<String>(Arrays.asList(
				ChatColor.WHITE + "Launches " + ChatColor.YELLOW + PROJECTILE_COUNT + ChatColor.GOLD
						+ " Projectiles ",
				ChatColor.WHITE + "Increases to " + ChatColor.YELLOW + (PROJECTILE_COUNT + BONUS_PROJECTILE_COUNT)
						+ ChatColor.WHITE + " when cast",
				ChatColor.WHITE + "above Y 150. Following segments",
				ChatColor.WHITE + "cost " + ChatColor.YELLOW + COST_REDUCTION + "% " + ChatColor.WHITE + "less", "",
				getCostText(MANA_COST) + " " + ChatColor.YELLOW + SpellActions.getArrowModelFromInt(dir)));
		lore.addAll(getMultiplierLore(MANA_COST_MULTIPLIER));
		meta.setLore(lore);
		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_TRIGGER);
		item.setItemMeta(meta);
		return item;
	}
}
