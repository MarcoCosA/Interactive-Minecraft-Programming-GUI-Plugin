package me.BerylliumOranges.spellevent.segments.c_results;

import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import me.BerylliumOranges.main.SpellPluginMain;
import me.BerylliumOranges.spellevent.entity_information.EntityStats;
import me.BerylliumOranges.spellevent.other.Guideline;
import me.BerylliumOranges.spellevent.processes.SegmentProcess;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;
import net.md_5.bungee.api.ChatColor;

public class ManaDrain extends EventSegment {
	public static final String THIS_SEGMENT_TYPE = SEGMENT_TYPE_RESULT;
	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 30;
	public static final double AMOUNT = 350;
	public static final double PERCENT = 30;
	public static final double DURATION = 5;
	public static final int RARITY = 2;

	public static Guideline processSegment(SegmentProcess proc) {
		int[] upgrades = getUpgrades(proc);
		if (proc.getTargetsCopy().isEmpty() || proc.subtractMana(proc.getStats().getMaxMana() * (MANA_COST / 100.0), false) < 1
				|| proc.subtractMana(proc.getStats().getMaxMana() * (MANA_COST / 100.0)) < 1)
			return new Guideline(proc, true, true);

		for (LivingEntity target : proc.getTargetsCopy()) {
			final double percent;
			final double amount;
			if (proc.getCaster() instanceof Mob && target instanceof Player) {
				percent = getPercentage(upgrades) * 2;
				amount = getAmount(upgrades) * 2;
			} else {
				percent = getPercentage(upgrades);
				amount = getAmount(upgrades);
			}
			int inverseFrequency = (int) Math.max(1, 5 - (amount / 200.0));
			BukkitTask task = new BukkitRunnable() {
				double amountDrained = 0;
				int count = 0;
				EntityStats stat = EntityStats.getEntityStats(target);

				public void run() {

					if (count % inverseFrequency == 0 && count < (DURATION - 0.4) * 20) {
						Location loc = target.getLocation().clone().add(0, target.getHeight() / 2.0, 0);
						Vector dir = proc.getCaster().getLocation().clone().subtract(loc).toVector().normalize().multiply(0.5);
						target.getWorld().spawnParticle(Particle.DRAGON_BREATH, loc, 0, dir.getX(), dir.getY() * 0.8, dir.getZ());
						if (target.getLocation().distanceSquared(proc.getCaster().getLocation()) > 400) {
							target.getWorld()
									.spawnParticle(
											Particle.DRAGON_BREATH, proc.getCaster().getLocation().clone()
													.add(0, proc.getCaster().getHeight() / 2.0, 0).subtract(dir.clone().multiply(20)),
											0, dir.getX(), dir.getY() * 0.8, dir.getZ());
						}
					}
					double amountToDrain = Math.min((stat.getMaxMana() * (percent / 100.0)), amount) / (double) (DURATION * 20.0);
					amountDrained += stat.getMana() - stat.subtractMana(amountToDrain);

					count++;
					if (count >= DURATION * 20) {
						this.cancel();
					}
				}

				@Override
				public void cancel() {
					alertCaster(proc.getCaster(), ITEM,
							"Drained " + ChatColor.AQUA + (int) amountDrained + ChatColor.WHITE + " mana from " + getAlertName(target)
									+ ChatColor.WHITE + " (" + ChatColor.AQUA + Math.round((stat.getMana() / stat.getMaxMana()) * 100)
									+ ChatColor.WHITE + "% remaining)",
							IS_PROJECTILE);
					super.cancel();
				}
			}.runTaskTimer(SpellPluginMain.getInstance(), 0, 0);
			proc.getSpellSlot().getTasks().add(task);

		}
		return new Guideline(proc, true, false);
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "Mana Drain");
		meta.setLore(Arrays.asList(
				ChatColor.WHITE + "Subtracts " + ChatColor.GREEN + getPercentage(upgrades) + ChatColor.WHITE + "% (maximum " + ChatColor.GREEN
						+ (int) getAmount(upgrades) + ChatColor.WHITE + ")",
				ChatColor.WHITE + "of " + ChatColor.YELLOW + "targets " + ChatColor.WHITE + "max mana over a",
				ChatColor.WHITE + "period of " + ChatColor.GREEN + DURATION + ChatColor.WHITE + " seconds", "", getPercentMaxManaCostText(MANA_COST)));

		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_RESULT);
		item.setItemMeta(meta);
		return item;
	}

	public static double getAmount(int upgrades[]) {
		return Math.floor((AMOUNT + (upgrades[0] + upgrades[1]) * 25) * 100.0) / 100.0;
	}

	public static double getPercentage(int upgrades[]) {
		return Math.floor((PERCENT + (upgrades[0] + upgrades[1]) * 1.25) * 100.0) / 100.0;
	}

}
