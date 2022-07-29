package me.BerylliumOranges.spellevent.entity_information.spellprefabs.armor;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import me.BerylliumOranges.misc.GachaItems;
import me.BerylliumOranges.spellevent.entity_information.entityprefabs.ads.Steve;
import me.BerylliumOranges.spellevent.entity_information.spellprefabs.AbstractSpellItem;
import me.BerylliumOranges.spellevent.segments.b_triggers.armor_triggers.WhenDamaged;
import me.BerylliumOranges.spellevent.segments.c_results.SummonEgg;
import me.BerylliumOranges.spellevent.segments.e_logic.ChanceToStop;
import net.md_5.bungee.api.ChatColor;

public class Steves extends AbstractSpellItem {
	public static final String NAME = ChatColor.RED + "Steves";
	public static final int RARITY = 4;
	public static final boolean PLAYER_OBTAINABLE = true;
	public static final boolean ARMOR = true;

	public static ItemStack getContainer() {
		HashMap<String, Double> traitList = new HashMap<String, Double>();
		traitList.put(GachaItems.ItemTrait.ITEM_MANA_COST.name, -95D);
		ItemStack item = GachaItems.getSpellItem(new ItemStack(Material.DIAMOND_CHESTPLATE), NAME, traitList, RARITY);
		return item;
	}

	public static ItemStack[][] getSegments() {
		int chance = 18;
		ItemStack seg = ChanceToStop.getUpdatedItem(new int[] { chance, 0 }, 1);
		seg.setAmount(chance);
		return new ItemStack[][] { { null, null, null, null, null, null, null, null, null },
				{ null, null, null, null, WhenDamaged.getUpdatedItem(new int[2], 1), null, null, null, null },
				{ null, null, null, null, seg, null, null, null, null, null },
				{ null, null, null, null, SummonEgg.getUpdatedItem(new int[2], 0), null, null, null, null },
				{ null, null, null, null, Steve.ITEM, null, null, null, null },
				{ null, null, null, null, null, null, null, null, null }, };
	}
}
