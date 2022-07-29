package me.BerylliumOranges.spellevent.segments.b_triggers.armor_triggers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.BerylliumOranges.spellevent.other.Guideline;
import me.BerylliumOranges.spellevent.processes.SegmentProcess;
import me.BerylliumOranges.spellevent.processes.SpellActions;
import me.BerylliumOranges.spellevent.projectiles.SpellProjectile;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;
import me.BerylliumOranges.spellevent.segments.abstracts.EventTriggerSegment;

public final class BelowHP extends EventTriggerSegment {
	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 60 * 2;
	public static final int RARITY = 4;

	public static final List<String> ACCEPTED_TAGS = Arrays.asList(EventSegment.TAKE_DAMAGE_TRIGGER_TAG);

	public static Guideline processSegment(SegmentProcess proc) {
		double bonusDamage = 0;
		if (proc.getEvent() != null && proc.getEvent() instanceof EntityDamageEvent) {
			EntityDamageEvent event = (EntityDamageEvent) proc.getEvent();
			bonusDamage = event.getFinalDamage();
		}
		int min = proc.getPresentSegments()[proc.getY()][proc.getX()].getAmount();
		if ((proc.getCaster().getHealth() - bonusDamage) / proc.getCaster().getMaxHealth() > min / 100.0
				|| proc.addSegmentCooldown(BelowHP.class, (int) MANA_COST)) {
			return new Guideline(proc, false, true);
		}
		proc.setManaMultiplier(0);
		proc.setMaxCasts(1);
		alertCaster(proc.getCaster(), proc.getContainer().getItemMeta().getDisplayName(),
				"Below " + ChatColor.RED + min + "% HP" + ChatColor.WHITE + ", casting!", false);
		return findAndCallNext(proc);
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		int amount = Math.max(upgrades[0], 1);
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getDisplayName(amount));
		ArrayList<String> lore = new ArrayList<String>(Arrays.asList(
				ChatColor.WHITE + "Casts the spell only if you take", ChatColor.WHITE + "damage that would reduce your",
				ChatColor.RED + "HP " + ChatColor.WHITE + "below " + ChatColor.YELLOW + amount + "%", "",
				getCooldownLore((int) MANA_COST), getCastText(),
				getFreeText() + " " + ChatColor.YELLOW + SpellActions.getArrowModelFromInt(dir)));
		meta.setLore(lore);
		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(getDisplayName(0)) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_TRIGGER);
		item.setItemMeta(meta);
		item.setAmount(amount);
		return item;
	}

	private static String getDisplayName(int amount) {
		return getColorFromRarity(RARITY) + "When Below " + amount + "% HP";
	}

	public static int[] modifyUpgradesInformation(ItemStack item, int[] upgrades) {
		upgrades[0] = item.getAmount();
		return upgrades;
	}
}
