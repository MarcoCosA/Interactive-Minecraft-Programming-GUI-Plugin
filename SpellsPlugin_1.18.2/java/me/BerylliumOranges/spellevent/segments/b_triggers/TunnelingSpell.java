package me.BerylliumOranges.spellevent.segments.b_triggers;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import me.BerylliumOranges.spellevent.other.Guideline;
import me.BerylliumOranges.spellevent.processes.SegmentProcess;
import me.BerylliumOranges.spellevent.processes.SpellActions;
import me.BerylliumOranges.spellevent.segments.abstracts.EventTriggerSegment;

public final class TunnelingSpell extends EventTriggerSegment {
	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 15;
	public static final int NUM_CAST = 5;

	public static final int RARITY = 1;
	public static final boolean IS_PROJECTILE = true;

	public static Guideline processSegment(SegmentProcess proc) {
		proc.addTimesCloned(NUM_CAST);
		if (proc.subtractMana(MANA_COST) < 1)
			return new Guideline(proc, true, true);

		proc.setTarget(null);
		Vector direction = proc.getSpellLocation().getDirection();
		direction.setY(0).normalize().multiply(8);
		Location loc = proc.getSpellLocation().subtract(0, 0, 0);
		Vector up = direction.clone();
		up.setY(1000).normalize();
		for (int i = 0; i < NUM_CAST; i++) {

			try {
				SegmentProcess clone = (SegmentProcess) proc.clone();
				Location temp = loc.clone();

				temp.setDirection(up);
				clone.setSpellLocation(temp);
				findAndCallNext(clone);
			} catch (Exception e) {
				e.printStackTrace();
			}
			loc.add(direction);
		}

		return new Guideline(proc, true, true);
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "Tunneling");
		ArrayList<String> lore = new ArrayList<String>(
				Arrays.asList(
						ChatColor.WHITE + "Casts the spell " + ChatColor.YELLOW + NUM_CAST + " " + ChatColor.WHITE
								+ "times.",
						ChatColor.WHITE + "Does not " + ChatColor.YELLOW + "target", "",
						getCostText(MANA_COST) + " " + ChatColor.YELLOW + SpellActions.getArrowModelFromInt(dir)));
		lore.addAll(getMultiplierLore(MANA_COST_MULTIPLIER));
		meta.setLore(lore);
		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_TRIGGER);
		item.setItemMeta(meta);
		return item;
	}
}
