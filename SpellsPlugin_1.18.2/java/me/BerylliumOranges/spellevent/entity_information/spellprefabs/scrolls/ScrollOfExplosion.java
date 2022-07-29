package me.BerylliumOranges.spellevent.entity_information.spellprefabs.scrolls;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import me.BerylliumOranges.misc.GachaItems;
import me.BerylliumOranges.misc.GachaItems.ItemTrait;
import me.BerylliumOranges.spellevent.entity_information.spellprefabs.AbstractSpellItem;
import me.BerylliumOranges.spellevent.other.OtherItems;
import me.BerylliumOranges.spellevent.segments.b_triggers.SingleProjectile;
import me.BerylliumOranges.spellevent.segments.c_results.Explode;

public class ScrollOfExplosion extends AbstractSpellItem {
	public static final String NAME = ChatColor.GRAY + "Scroll of Explosion";
	public static final int RARITY = 1;
	public static final boolean PLAYER_OBTAINABLE = true;
	public static final boolean SCROLL = true;

	public static ItemStack getContainer() {
		HashMap<String, Double> traitList = new HashMap<String, Double>();
		traitList.put(ItemTrait.CRIT_CHANCE.name, 200.0);
		return GachaItems.getSpellItem(new ItemStack(Material.PAPER), NAME, traitList, RARITY);
	}

	public static ItemStack[][] getSegments() {
		return new ItemStack[][] { { null, null, null, null, null, null, null, null, null },
				{ null, null, null, null, null, null, null, null, null },
				{ null, null, null, null, SingleProjectile.getUpdatedItem(new int[2], 1), null, null, null, null },
				{ null, null, null, OtherItems.quadrupleUpgrade(), Explode.getUpdatedItem(new int[2], 0),
						OtherItems.quadrupleUpgrade(), null, null, null },
				{ null, null, null, null, null, null, null, null, null },
				{ null, null, null, null, null, null, null, null, null }, };
	}
}
