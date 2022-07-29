package me.BerylliumOranges.spellevent.entity_information.spellprefabs.wands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import me.BerylliumOranges.misc.GachaItems;
import me.BerylliumOranges.spellevent.entity_information.spellprefabs.AbstractSpellItem;
import me.BerylliumOranges.spellevent.segments.b_triggers.Hailstorm;
import me.BerylliumOranges.spellevent.segments.b_triggers.PoorShot;
import me.BerylliumOranges.spellevent.segments.b_triggers.SingleProjectile;
import me.BerylliumOranges.spellevent.segments.b_triggers.TargetSelf;
import me.BerylliumOranges.spellevent.segments.c_results.DealDamage;
import me.BerylliumOranges.spellevent.segments.c_results.Explode;
import me.BerylliumOranges.spellevent.segments.d_modifiers.ShortenProjectile;
import me.BerylliumOranges.spellevent.segments.e_logic.AddCost;

public class CloudOfLead extends AbstractSpellItem {
	public static final String NAME = ChatColor.DARK_GRAY + "Cloud of Lead";
	public static final int RARITY = 3;
	public static final boolean PLAYER_OBTAINABLE = true;
	public static final boolean SWORD = false;

	public static ItemStack getContainer() {
		return GachaItems.getRandomItem(new ItemStack(Material.DIAMOND_HOE), NAME, RARITY);
	}

	public static ItemStack[][] getSegments() {
		return new ItemStack[][] { { null, null, null, null, null, null, null, null, null },
				{ null, null, SingleProjectile.getUpdatedItem(new int[2], 1), null, null, null, null, null, null },
				{ null, ShortenProjectile.getUpdatedItem(new int[2], 0),
						ShortenProjectile.getUpdatedItem(new int[2], 2), Hailstorm.getUpdatedItem(new int[2], 0),
						TargetSelf.getUpdatedItem(new int[2], 0), PoorShot.getUpdatedItem(new int[2], 1),
						Explode.getUpdatedItem(new int[2], 0), null, null },
				{ null, null, null, null, null, DealDamage.getUpdatedItem(new int[2], 0), null, null, null },
				{ null, null, null, null, null, null, null, null, null },
				{ null, null, null, null, null, null, null, null, null }, };
	}
}
