package me.BerylliumOranges.spellevent.segments.b_triggers;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.BerylliumOranges.spellevent.other.Guideline;
import me.BerylliumOranges.spellevent.processes.SegmentProcess;
import me.BerylliumOranges.spellevent.processes.SpellActions;
import me.BerylliumOranges.spellevent.segments.abstracts.EventTriggerSegment;

public final class TargetSelf extends EventTriggerSegment {
	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 1;

	public static final int RARITY = 0;

	public static Guideline processSegment(SegmentProcess proc) {
		if (proc.subtractMana(MANA_COST) < 1)
			return new Guideline(proc, true, true);
		proc.setTarget(proc.getCaster());
		proc.setSpellLocation(proc.getCaster().getEyeLocation().subtract(0, 0.2, 0));
		return findAndCallNext(proc);
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "Target Self");
		ArrayList<String> lore = new ArrayList<String>(
				Arrays.asList(ChatColor.YELLOW + "Targets " + ChatColor.WHITE + "you", "",
						getCostText(MANA_COST) + " " + ChatColor.YELLOW + SpellActions.getArrowModelFromInt(dir)));
		lore.addAll(getMultiplierLore(MANA_COST_MULTIPLIER));
		meta.setLore(lore);
		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_TRIGGER);
		item.setItemMeta(meta);
		return item;
	}
}
