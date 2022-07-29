package me.BerylliumOranges.spellevent.entity_information.spellprefabs.wands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import me.BerylliumOranges.misc.GachaItems;
import me.BerylliumOranges.spellevent.entity_information.spellprefabs.AbstractSpellItem;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;
import me.BerylliumOranges.spellevent.segments.b_triggers.Hwacha;
import me.BerylliumOranges.spellevent.segments.b_triggers.SingleProjectile;
import me.BerylliumOranges.spellevent.segments.d_modifiers.ApplyGravity;
import me.BerylliumOranges.spellevent.segments.d_modifiers.BlockFallingPath;
import me.BerylliumOranges.spellevent.segments.d_modifiers.EconomicalMagic;
import me.BerylliumOranges.spellevent.segments.d_modifiers.ShortenProjectile;
import me.BerylliumOranges.spellevent.segments.e_logic.IgnoreMe;
import me.BerylliumOranges.spellevent.segments.e_logic.Upward;

public class ObdominalWall extends AbstractSpellItem {
	public static final String NAME = ChatColor.BLUE + "Obdominal Wall";
	public static final int RARITY = 2;
	public static final boolean PLAYER_OBTAINABLE = true;
	public static final boolean SWORD = false;

	public static ItemStack getContainer() {
		return GachaItems.getRandomItem(new ItemStack(Material.IRON_HOE), NAME, RARITY);
	}

	public static ItemStack[][] getSegments() {
		ItemStack shortenedProj = ShortenProjectile.getUpdatedItem(new int[2], 1);
		shortenedProj.setAmount(4);

		return new ItemStack[][] {
				{ null, null, EventSegment.getUpdatedItem(new int[2], 1), null, null, null, null, null, null },
				{ null, null, ApplyGravity.getUpdatedItem(new int[2], 3), null, null, null, null, null, null },
				{ null, null, EconomicalMagic.getUpdatedItem(new int[2], 0), shortenedProj,
						Upward.getUpdatedItem(new int[2], 2), null, null, null, null },
				{ null, null, null, IgnoreMe.getUpdatedItem(new int[2], 0), null, Hwacha.getUpdatedItem(new int[2], 0),
						null, null, null },
				{ null, null, null, SingleProjectile.getUpdatedItem(new int[2], 0),
						BlockFallingPath.getUpdatedItem(new int[2], 3), null, null, null, null },
				{ null, null, null, null, null, null, null, null, null }, };
	}
}
