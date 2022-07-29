package me.BerylliumOranges.spellevent.segments.c_results;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.BerylliumOranges.misc.GachaItems;
import me.BerylliumOranges.spellevent.other.Guideline;
import me.BerylliumOranges.spellevent.processes.SegmentProcess;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;
import net.md_5.bungee.api.ChatColor;

public class Heal extends EventSegment {
	public static final String THIS_SEGMENT_TYPE = SEGMENT_TYPE_RESULT;
	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 15;
	public static final double ORIGINAL_HEAL = 2.5;
	public static final int RARITY = -1;

	public static Guideline processSegment(SegmentProcess proc) {
		int[] upgrades = getUpgrades(proc);
		if (proc.subtractMana(MANA_COST) < 1)
			return new Guideline(proc, true, true);

		for (LivingEntity liv : proc.getTargetsCopy()) {
			double healthToHeal = getHeal(upgrades)
					* (1 + GachaItems.getTrait(liv, GachaItems.ItemTrait.HEALING) / 100.0);
			double healthHealed = 0;
			if (healthToHeal + liv.getHealth() > liv.getMaxHealth()) {
				healthHealed = liv.getMaxHealth() - liv.getHealth();
				liv.setHealth(liv.getMaxHealth());
			} else {
				healthHealed = healthToHeal;
				liv.setHealth(liv.getHealth() + healthToHeal);
			}

			EntityRegainHealthEvent en = new EntityRegainHealthEvent(liv, healthHealed, RegainReason.MAGIC);
			Bukkit.getServer().getPluginManager().callEvent(en);

			alertCaster(proc.getCaster(), ITEM,
					"+" + ChatColor.RED + Math.floor(healthHealed * 100.0) / 100.0 + ChatColor.DARK_RED + " health"
							+ ChatColor.WHITE + ", " + (int) liv.getHealth() + "/" + (int) liv.getMaxHealth(),
					false);
			doHealEffect(liv);
		}

		return new Guideline(proc, true, false);
	}

	public static final double RED = 255 / 255D;
	public static final double GREEN = 255 / 255D;
	public static final double BLUE = 0 / 255D;

	public static void doHealEffect(LivingEntity liv) {

		Location loc = liv.getLocation().add(0, liv.getHeight() / 2.0, 0);
		loc.getWorld().playSound(loc, Sound.ENTITY_GUARDIAN_AMBIENT, 1.05F, 2F);
		try {
			for (int i = 0; i < 5; i++)
				liv.getWorld().spawnParticle(Particle.SPELL_MOB, loc.getX() + Math.random() - 0.5,
						loc.getY() + Math.random() - 0.5, loc.getZ() + (Math.random() - 0.5) * 2, 0, RED, GREEN, BLUE,
						1, null, true);
		} catch (Exception er) {
			er.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "Heal");
		meta.setLore(Arrays.asList(
				ChatColor.WHITE + "Heals targets " + ChatColor.GREEN + getHeal(upgrades) + ChatColor.WHITE + " health",

				"", getCostText(MANA_COST)));

		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_RESULT);
		item.setItemMeta(meta);
		return item;
	}

	public static double getHeal(int upgrades[]) {
		return ORIGINAL_HEAL + upgrades[0] + upgrades[1];
	}

}
