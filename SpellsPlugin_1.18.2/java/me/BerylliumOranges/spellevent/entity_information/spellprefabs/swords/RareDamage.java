package me.BerylliumOranges.spellevent.entity_information.spellprefabs.swords;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import me.BerylliumOranges.misc.GachaItems;
import me.BerylliumOranges.spellevent.entity_information.spellprefabs.AbstractSpellItem;
import me.BerylliumOranges.spellevent.segments.b_triggers.TargetEnemy;
import me.BerylliumOranges.spellevent.segments.c_results.DealDamage;
import me.BerylliumOranges.spellevent.segments.e_logic.AddCost;

public class RareDamage extends AbstractSpellItem {
	public static final String NAME = ChatColor.BLUE + "Costly Damage";
	public static final int RARITY = 1;
	public static final boolean PLAYER_OBTAINABLE = true;
	public static final boolean SWORD = true;

	public static ItemStack getContainer() {
		return GachaItems.getRandomItem(new ItemStack(Material.IRON_SWORD), NAME, RARITY);
	}

	public static ItemStack[][] getSegments() {
		return new ItemStack[][] { { null, null, null, null, null, null, null, null, null },
				{ null, null, null, null, TargetEnemy.getUpdatedItem(new int[2], 1), null, null, null, null },
				{ null, null, null, null, AddCost.getUpdatedItem(new int[2], 1), null, null, null, null },
				{ null, null, null, null, DealDamage.getUpdatedItem(new int[2], 0), null, null, null, null },
				{ null, null, null, null, null, null, null, null, null },
				{ null, null, null, null, null, null, null, null, null }, };
	}
}
