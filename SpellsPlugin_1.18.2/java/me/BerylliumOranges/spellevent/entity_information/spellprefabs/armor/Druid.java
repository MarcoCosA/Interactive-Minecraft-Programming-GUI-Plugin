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

public class Druid extends AbstractSpellItem {
	public static final Color TEXT_COLOR1 = new Color(105, 85, 4);
	public static final Color TEXT_COLOR2 = new Color(30, 200, 0);
	public static final String NAME = colorTextFade("Druid's Plate", new Color[] { TEXT_COLOR1, TEXT_COLOR2 });
	public static final int RARITY = 3;
	public static final double HEALING_AMOUNT = 50;
	public static final boolean PLAYER_OBTAINABLE = true;
//	public static final boolean ARMOR = true;

	public static ItemStack getContainer() {
		HashMap<String, Double> traitList = new HashMap<String, Double>();
		traitList.put(ItemTrait.HEALING.name, HEALING_AMOUNT);
		ArrayList<String> lore = new ArrayList<String>();
//		lore.add(0, ChatColor.DARK_GRAY + "Always has " + HEALTH_AMOUNT + "% lifesteal");
		ItemStack item = GachaItems.getSpellItem(new ItemStack(Material.DIAMOND_CHESTPLATE), NAME, traitList, RARITY,
				lore);
		return item;
	}

	public static ItemStack[][] getSegments() {
		return new ItemStack[][] { { null, null, null, null, null, null, null, null, null },
				{ null, null, null, null, null, null, null, null, null },
				{ null, null, null, null, null, null, null, null, null, null },
				{ null, null, null, null, null, null, null, null, null },
				{ null, null, null, null, null, null, null, null, null },
				{ null, null, null, null, null, null, null, null, null }, };
	}
}
