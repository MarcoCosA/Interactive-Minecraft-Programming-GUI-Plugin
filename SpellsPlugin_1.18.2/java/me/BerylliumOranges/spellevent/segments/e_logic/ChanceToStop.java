package me.BerylliumOranges.spellevent.segments.e_logic;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.BerylliumOranges.spellevent.other.Guideline;
import me.BerylliumOranges.spellevent.processes.SegmentProcess;
import me.BerylliumOranges.spellevent.processes.SpellActions;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;

public class ChanceToStop extends EventSegment {
	public static final String THIS_SEGMENT_TYPE = SEGMENT_TYPE_LOGIC;
	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 1;
	public static final int RARITY = 4;

	public static Guideline processSegment(SegmentProcess proc) {
		if (proc.subtractMana(MANA_COST) < 1
				|| Math.random() * 100 <= proc.getPresentSegments()[proc.getY()][proc.getX()].getAmount() * 5)
			return new Guideline(proc, true, false);
		return findAndCallNext(proc);
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		int amount = Math.max(upgrades[0], 1);
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getDisplayName(amount));
		meta.setLore(Arrays.asList(ChatColor.WHITE + "Stops the spell", ChatColor.DARK_GRAY + "Stacks",
				getCostText(MANA_COST) + " " + SpellActions.getArrowModelFromInt(dir)));

		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(getDisplayName(0)) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_LOGIC);
		item.setItemMeta(meta);
		return item;
	}

	private static String getDisplayName(int amount) {
		return getColorFromRarity(RARITY) + "" + (amount * 5) + "% Chance to Cancel";
	}

	public static int[] modifyUpgradesInformation(ItemStack item, int[] upgrades) {
		upgrades[0] = item.getAmount();
		return upgrades;
	}
}
