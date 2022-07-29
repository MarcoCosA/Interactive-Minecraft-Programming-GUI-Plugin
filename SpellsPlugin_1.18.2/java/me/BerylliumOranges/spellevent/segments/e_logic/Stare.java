package me.BerylliumOranges.spellevent.segments.e_logic;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import me.BerylliumOranges.spellevent.other.Guideline;
import me.BerylliumOranges.spellevent.processes.SegmentProcess;
import me.BerylliumOranges.spellevent.processes.SpellActions;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;

public class Stare extends EventSegment {
	public static final String THIS_SEGMENT_TYPE = SEGMENT_TYPE_LOGIC;
	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 5;
	public static final int ORIGINAL_DISTANCE = 25;
	public static final int RARITY = 2;

	public static Guideline processSegment(SegmentProcess proc) {
		int[] upgrades = getUpgrades(proc);
		if (proc.subtractMana(MANA_COST) < 1)
			return new Guideline(proc, true, true);
		proc.getSpellLocation().setDirection(new Vector(0, 1, 0));

		if (proc.getCaster() != null) {
			Block b = proc.getCaster().getTargetBlock(null, getDistance(upgrades));
			if (b != null) {
				proc.getSpellLocation().setDirection(
						b.getLocation().clone().toVector().subtract(proc.getSpellLocation().toVector()).normalize());
			}
		}

		return findAndCallNext(proc);
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "Deadly Stare");
		meta.setLore(Arrays.asList(ChatColor.WHITE + "Spell is pointed towards the " + ChatColor.GOLD + "block ",
				ChatColor.WHITE + "the caster is looking at",
				ChatColor.DARK_GRAY + "Becomes innacurate after " + getDistance(upgrades) + " blocks",
				getCostText(MANA_COST) + " " + SpellActions.getArrowModelFromInt(dir)));
		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_LOGIC);
		item.setItemMeta(meta);
		return item;
	}

	public static int getDistance(int[] upgrades) {
		return ORIGINAL_DISTANCE + (upgrades[0] + upgrades[1]) * 15;
	}

}
