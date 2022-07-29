package me.BerylliumOranges.spellevent.entity_information.spellprefabs.wands;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import me.BerylliumOranges.misc.GachaItems;
import me.BerylliumOranges.spellevent.entity_information.spellprefabs.AbstractSpellItem;
import me.BerylliumOranges.spellevent.segments.b_triggers.TargetSelf;
import me.BerylliumOranges.spellevent.segments.c_results.Flashbang;
import net.md_5.bungee.api.ChatColor;

public class FlashbangWand extends AbstractSpellItem {
	public static final String NAME = ChatColor.RED + "Flashbang";
	public static final int RARITY = 5;
	public static final boolean PLAYER_OBTAINABLE = false;
	public static final boolean SWORD = false;

	public static ItemStack getContainer() {
		return GachaItems.getRandomItem(new ItemStack(Material.IRON_HOE), NAME, 4);
	}

	public static ItemStack[][] getSegments() {
		return new ItemStack[][] { { null, null, null, null, null, null, null, null, null },
				{ null, null, null, null, null, null, null, null, null },
				{ null, null, null, null, TargetSelf.getUpdatedItem(new int[2], 1), null, null, null, null },
				{ null, null, null, null, Flashbang.getUpdatedItem(new int[2], 0), null, null, null, null },
				{ null, null, null, null, null, null, null, null, null },
				{ null, null, null, null, null, null, null, null, null }, };
	}
}
