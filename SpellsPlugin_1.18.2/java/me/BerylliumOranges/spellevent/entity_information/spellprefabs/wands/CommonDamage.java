package me.BerylliumOranges.spellevent.entity_information.spellprefabs.wands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import me.BerylliumOranges.misc.GachaItems;
import me.BerylliumOranges.spellevent.entity_information.spellprefabs.AbstractSpellItem;
import me.BerylliumOranges.spellevent.segments.b_triggers.SingleProjectile;
import me.BerylliumOranges.spellevent.segments.c_results.DealDamage;
import me.BerylliumOranges.spellevent.segments.e_logic.AddCost;

public class CommonDamage extends AbstractSpellItem {
	public static final String NAME = ChatColor.GRAY + "Pathetic Damage";
	public static final int RARITY = 0;
	public static final boolean PLAYER_OBTAINABLE = true;
	public static final boolean SWORD = false;

	public static ItemStack getContainer() {
		return GachaItems.getRandomItem(new ItemStack(Material.WOODEN_HOE), NAME, RARITY);
	}

	public static ItemStack[][] getSegments() {
		return new ItemStack[][] { { null, null, null, null, null, null, null, null, null },
				{ null, null, null, null, SingleProjectile.getUpdatedItem(new int[2], 1), null, null, null, null },
				{ null, null, null, null, AddCost.getUpdatedItem(new int[2], 1), null, null, null, null },
				{ null, null, null, null, AddCost.getUpdatedItem(new int[2], 1), null, null, null, null },
				{ null, null, null, null, DealDamage.getUpdatedItem(new int[2], 0), null, null, null, null },
				{ null, null, null, null, null, null, null, null, null }, };
	}
}
