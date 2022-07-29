package me.BerylliumOranges.spellevent.segments.g_spell_focus;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.BerylliumOranges.spellevent.entity_information.EntityStats;
import me.BerylliumOranges.spellevent.other.Guideline;
import me.BerylliumOranges.spellevent.processes.SegmentProcess;
import me.BerylliumOranges.spellevent.processes.SpellActions;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;
import me.BerylliumOranges.spellevent.segments.abstracts.EventTriggerSegment;

public class FocusLastSpell extends EventTriggerSegment {
	public static final String THIS_SEGMENT_TYPE = SEGMENT_TYPE_TRIGGER;
	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 5;
	public static final int RARITY = 2;

	public static Guideline processSegment(SegmentProcess proc) {
		int[] upgrades = EventSegment.getUpgrades(proc);
		if (proc.subtractMana(MANA_COST) < 1)
			return new Guideline(proc, true, false);
		proc.clearFocusedSpells();
		for (LivingEntity liv : proc.getTargetsCopy()) {
			EntityStats stat = EntityStats.getEntityStats(liv);
			if (stat.getLastCast() != null) {
				proc.addFocusedSpell(stat.getLastCast());
			}
		}
		return findAndCallNext(proc);
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "Focus Last Spell");
		meta.setLore(Arrays.asList(ChatColor.LIGHT_PURPLE + "Focuses " + ChatColor.WHITE + "the last spell cast",
				ChatColor.WHITE + "by each " + ChatColor.YELLOW + "target", "",
				getCostText(MANA_COST) + " " + ChatColor.LIGHT_PURPLE + SpellActions.getArrowModelFromInt(dir)));

		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_SPELL_FOCUS);
		item.setItemMeta(meta);
		return item;
	}
}
