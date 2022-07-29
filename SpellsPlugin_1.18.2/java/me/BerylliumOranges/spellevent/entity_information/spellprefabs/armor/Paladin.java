package me.BerylliumOranges.spellevent.entity_information.spellprefabs.armor;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import me.BerylliumOranges.misc.GachaItems;
import me.BerylliumOranges.misc.GachaItems.ItemTrait;
import me.BerylliumOranges.spellevent.entity_information.spellprefabs.AbstractSpellItem;
import me.BerylliumOranges.spellevent.segments.b_triggers.armor_triggers.WhenTarget;
import me.BerylliumOranges.spellevent.segments.g_spell_focus.Counterspell;

public class Paladin extends AbstractSpellItem {
	public static final Color TEXT_COLOR = new Color(255, 155, 0);
	public static final String NAME = colorTextFade("Paladin's Plate", new Color[] { TEXT_COLOR, Color.RED });
	public static final int RARITY = 3;
	public static final double HEALTH_AMOUNT = -20;
	public static final double MANA_COST_AMOUNT = -95;
	public static final boolean PLAYER_OBTAINABLE = true;
	public static final boolean ARMOR = true;

	public static ItemStack getContainer() {
		HashMap<String, Double> traitList = new HashMap<String, Double>();
		traitList.put(ItemTrait.ITEM_MANA_COST.name, MANA_COST_AMOUNT);
		traitList.put(ItemTrait.MAX_HEALTH.name, HEALTH_AMOUNT);
		ArrayList<String> lore = new ArrayList<String>();
//		lore.add(0, ChatColor.DARK_GRAY + "Always has " + HEALTH_AMOUNT + "% lifesteal");
		ItemStack item = GachaItems.getSpellItem(new ItemStack(Material.DIAMOND_CHESTPLATE), NAME, traitList, RARITY,
				lore);
		return item;
	}

	public static ItemStack[][] getSegments() {
		return new ItemStack[][] { { null, null, null, null, null, null, null, null, null },
				{ null, null, null, null, null, null, null, null, null },
				{ null, null, null, null, WhenTarget.getUpdatedItem(new int[2], 1), null, null, null, null, null },
				{ null, null, null, null, Counterspell.getUpdatedItem(new int[2], 0), null, null, null, null },
				{ null, null, null, null, null, null, null, null, null },
				{ null, null, null, null, null, null, null, null, null }, };
	}
}
