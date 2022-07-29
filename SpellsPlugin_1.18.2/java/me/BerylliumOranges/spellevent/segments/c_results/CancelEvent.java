package me.BerylliumOranges.spellevent.segments.c_results;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.BerylliumOranges.spellevent.other.Guideline;
import me.BerylliumOranges.spellevent.processes.SegmentProcess;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;

public class CancelEvent extends EventSegment {
	public static final String THIS_SEGMENT_TYPE = SEGMENT_TYPE_RESULT;
	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 100;
	public static final double ITEM_MANA_COST = 30;
	public static final int RARITY = 4;

	public static final boolean CONSUME_POT = false;

	public static Guideline processSegment(SegmentProcess proc) {
		int[] upgrades = getUpgrades(proc);
		if (proc.subtractMana(MANA_COST, false) < 1 || proc.addSegmentCooldown(CancelEvent.class, (int) ITEM_MANA_COST)
				|| proc.subtractMana(MANA_COST) < 1)
			return new Guideline(proc, true, true);

		if (proc.getEvent() instanceof Cancellable) {
			if (proc.getEvent() instanceof FoodLevelChangeEvent) {
				FoodLevelChangeEvent event = (FoodLevelChangeEvent) proc.getEvent();
				event.getItem().setAmount(event.getItem().getAmount() - 1);
				Player p = (Player) event.getEntity();
				p.updateInventory();
			}
			((Cancellable) proc.getEvent()).setCancelled(true);
		}

		return new Guideline(proc, true, false);
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "Cancel Event");
		ArrayList<String> lore = new ArrayList<String>(
				Arrays.asList(ChatColor.WHITE + "Cancels events like eating, dying", ChatColor.WHITE + "and damage"));
		lore.addAll(
				Arrays.asList(ChatColor.DARK_GRAY + "", getCooldownLore((int) ITEM_MANA_COST), getCostText(MANA_COST)));
		meta.setLore(lore);
		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_RESULT);
		item.setItemMeta(meta);
		return item;
	}
}
