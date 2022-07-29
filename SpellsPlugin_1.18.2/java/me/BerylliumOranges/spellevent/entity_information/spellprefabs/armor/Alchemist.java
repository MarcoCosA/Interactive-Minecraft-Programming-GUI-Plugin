package me.BerylliumOranges.spellevent.entity_information.spellprefabs.armor;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import me.BerylliumOranges.misc.GachaItems;
import me.BerylliumOranges.misc.GachaItems.ItemTrait;
import me.BerylliumOranges.spellevent.entity_information.spellprefabs.AbstractSpellItem;
import me.BerylliumOranges.spellevent.segments.b_triggers.armor_triggers.WhenNearbyDeath;
import me.BerylliumOranges.spellevent.segments.c_results.CloneItem;
import me.BerylliumOranges.spellevent.segments.d_modifiers.PoorOdds;
import net.md_5.bungee.api.ChatColor;

public class Alchemist extends AbstractSpellItem {
	public static final Color TEXT_COLOR1 = new Color(255, 255, 255);
	public static final Color TEXT_COLOR2 = new Color(100, 100, 100);
	public static final String NAME = colorTextFade("Alchemist's Plate",
			new Color[] { TEXT_COLOR1, new Color(255, 0, 255) });
	public static final int RARITY = 3;
	public static final boolean PLAYER_OBTAINABLE = true;
	public static final boolean ARMOR = true;

	public static ItemStack getContainer() {
		HashMap<String, Double> traitList = new HashMap<String, Double>();
		traitList.put(ItemTrait.ITEM_MANA_COST.name, -95.0);
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(ChatColor.DARK_GRAY + "While equipped, entities have a");
		lore.add(ChatColor.DARK_GRAY + "5% chance to drop a random");
		lore.add(ChatColor.DARK_GRAY + "segment on death");
		ItemStack item = GachaItems.getSpellItem(new ItemStack(Material.DIAMOND_CHESTPLATE), NAME, traitList, RARITY,
				lore);
		return item;
	}

	public static ItemStack[][] getSegments() {
		return new ItemStack[][] { { null, null, null, null, null, null, null, null, null },
				{ null, null, null, null, WhenNearbyDeath.getUpdatedItem(new int[2], 1), null, null, null, null },
				{ null, null, null, null, PoorOdds.getUpdatedItem(new int[2], 1), null, null, null, null, null },
				{ null, null, null, null, CloneItem.getUpdatedItem(new int[2], 0), null, null, null, null },
				{ null, null, null, null, GachaItems.getRandomSegmentIndicator(), null, null, null, null },
				{ null, null, null, null, null, null, null, null, null }, };
	}
}
