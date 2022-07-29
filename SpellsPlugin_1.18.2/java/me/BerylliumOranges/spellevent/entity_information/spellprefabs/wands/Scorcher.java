package me.BerylliumOranges.spellevent.entity_information.spellprefabs.wands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import me.BerylliumOranges.misc.GachaItems;
import me.BerylliumOranges.spellevent.entity_information.spellprefabs.AbstractSpellItem;
import me.BerylliumOranges.spellevent.segments.b_triggers.SingleProjectile;
import me.BerylliumOranges.spellevent.segments.c_results.Blaze;
import me.BerylliumOranges.spellevent.segments.c_results.DealDamage;
import me.BerylliumOranges.spellevent.segments.d_modifiers.ProjectileSpeed3;
import me.BerylliumOranges.spellevent.segments.e_logic.AddCost;

public class Scorcher extends AbstractSpellItem {
	public static final String NAME = ChatColor.BLUE + "Scorcher";
	public static final int RARITY = 1;
	public static final boolean PLAYER_OBTAINABLE = true;
	public static final boolean SWORD = false;

	public static ItemStack getContainer() {
		return GachaItems.getRandomItem(new ItemStack(Material.IRON_HOE), NAME, RARITY);
	}

	public static ItemStack[][] getSegments() {
		return new ItemStack[][] { { null, null, null, null, null, null, null, null, null },
				{ null, null, null, null, ProjectileSpeed3.getUpdatedItem(new int[2], 1), null, null, null, null },
				{ null, null, null, null, SingleProjectile.getUpdatedItem(new int[2], 1), null, null, null, null },
				{ null, null, null, null, Blaze.getUpdatedItem(new int[2], 0), null, null, null, null },
				{ null, null, null, null, null, null, null, null, null },
				{ null, null, null, null, null, null, null, null, null }, };
	}
}
