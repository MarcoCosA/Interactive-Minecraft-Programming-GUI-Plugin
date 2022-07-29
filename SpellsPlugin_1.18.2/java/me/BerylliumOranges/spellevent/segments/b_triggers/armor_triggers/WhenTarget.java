package me.BerylliumOranges.spellevent.segments.b_triggers.armor_triggers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.BerylliumOranges.spellevent.other.Guideline;
import me.BerylliumOranges.spellevent.processes.SegmentProcess;
import me.BerylliumOranges.spellevent.processes.SpellActions;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;
import me.BerylliumOranges.spellevent.segments.abstracts.EventTriggerSegment;

public final class WhenTarget extends EventTriggerSegment {
	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 30;

	public static final int RARITY = 4;

	public static final List<String> ACCEPTED_TAGS = Arrays.asList(EventSegment.TARGET_TRIGGER_TAG);

	public static Guideline processSegment(SegmentProcess proc) {

		if (/* proc.getTargetsCopy().contains(proc.getCaster()) || */!hasTag(proc, WhenTarget.class)
				|| proc.addSegmentCooldown(WhenTarget.class, (int) MANA_COST)) {
			return new Guideline(proc, false, true);
		}
		proc.setManaMultiplier(0);
		proc.setMaxCasts(1);

		alertCaster(proc.getCaster(), proc.getContainer().getItemMeta().getDisplayName(),
				ChatColor.WHITE + "You've been " + ChatColor.YELLOW + "targetted" + ChatColor.WHITE + ", casting!",
				false);
		return findAndCallNext(proc);
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "When Targetted");
		ArrayList<String> lore = new ArrayList<String>(Arrays.asList(ChatColor.WHITE + "Casts the spell if you are",
				ChatColor.YELLOW + "targetted " + ChatColor.WHITE + "by another entity", "",
				getCooldownLore((int) MANA_COST), getCastText(),
				getFreeText() + " " + ChatColor.YELLOW + SpellActions.getArrowModelFromInt(dir)));
		meta.setLore(lore);
		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_TRIGGER);
		item.setItemMeta(meta);
		return item;
	}
}
