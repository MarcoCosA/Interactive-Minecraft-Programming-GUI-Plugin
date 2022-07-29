package me.BerylliumOranges.spellevent.entity_information.spellprefabs.wands;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import me.BerylliumOranges.misc.GachaItems;
import me.BerylliumOranges.spellevent.entity_information.spellprefabs.AbstractSpellItem;
import me.BerylliumOranges.spellevent.segments.b_triggers.SingleProjectile;
import me.BerylliumOranges.spellevent.segments.c_results.Explode;
import me.BerylliumOranges.spellevent.segments.e_logic.Delay;
import net.md_5.bungee.api.ChatColor;

public class DelayedExplosion extends AbstractSpellItem {
	public static final String NAME = ChatColor.GRAY + "Element of Laches";
	public static final int RARITY = 0;
	public static final boolean PLAYER_OBTAINABLE = true;
	public static final boolean SWORD = false;

	public static ItemStack getContainer() {
		return GachaItems.getRandomItem(new ItemStack(Material.IRON_HOE), NAME, RARITY);
	}

	public static ItemStack[][] getSegments() {
		return new ItemStack[][] { { null, null, null, null, null, null, null, null, null },
				{ null, null, null, null, SingleProjectile.getUpdatedItem(new int[2], 1), null, null, null, null },
				{ null, null, null, null, Delay.getUpdatedItem(new int[2], 1), null, null, null, null },
				{ null, null, null, null, Delay.getUpdatedItem(new int[2], 1), null, null, null, null },
				{ null, null, null, null, Explode.getUpdatedItem(new int[2], 0), null, null, null, null },
				{ null, null, null, null, null, null, null, null, null }, };
	}
}
