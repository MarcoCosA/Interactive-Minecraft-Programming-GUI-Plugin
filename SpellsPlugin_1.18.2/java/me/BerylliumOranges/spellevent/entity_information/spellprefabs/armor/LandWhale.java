package me.BerylliumOranges.spellevent.entity_information.spellprefabs.armor;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import me.BerylliumOranges.misc.GachaItems;
import me.BerylliumOranges.misc.GachaItems.ItemTrait;
import me.BerylliumOranges.spellevent.entity_information.spellprefabs.AbstractSpellItem;
import me.BerylliumOranges.spellevent.other.OtherItems;
import me.BerylliumOranges.spellevent.segments.b_triggers.armor_triggers.WhenFall;
import me.BerylliumOranges.spellevent.segments.b_triggers.armor_triggers.WhenTarget;
import me.BerylliumOranges.spellevent.segments.c_results.SeismicWave;
import me.BerylliumOranges.spellevent.segments.g_spell_focus.Counterspell;

public class LandWhale extends AbstractSpellItem {
	public static final Color TEXT_COLOR1 = new Color(52, 170, 235);
	public static final Color TEXT_COLOR2 = new Color(50, 90, 235);
	public static final String NAME = colorTextFade("Land Whale's Shoes", new Color[] { TEXT_COLOR1 });
	public static final int RARITY = 2;
	public static final boolean PLAYER_OBTAINABLE = true;

	public static ItemStack getContainer() {
		HashMap<String, Double> traitList = new HashMap<String, Double>();
		traitList.put(ItemTrait.MOVEMENT_SPEED.name, 30D);
		ItemStack item = GachaItems.getSpellItem(new ItemStack(Material.DIAMOND_BOOTS), NAME, traitList, RARITY);
		return item;
	}

	public static ItemStack[][] getSegments() {
		return new ItemStack[][] { { null, null, null, null, null, null, null, null, null },
				{ null, null, null, null, WhenFall.getUpdatedItem(new int[2], 1), null, null, null, null },
				{ null, null, null, null, OtherItems.singleUpgrade(), null, null, null, null, null },
				{ null, null, null, OtherItems.singleUpgrade(), SeismicWave.getUpdatedItem(new int[2], 0), OtherItems.singleUpgrade(), null, null,
						null },
				{ null, null, null, null, OtherItems.singleUpgrade(), null, null, null, null },
				{ null, null, null, null, null, null, null, null, null }, };
	}
}
