package me.BerylliumOranges.spellevent.entity_information.spellprefabs.wands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import me.BerylliumOranges.misc.GachaItems;
import me.BerylliumOranges.spellevent.entity_information.spellprefabs.AbstractSpellItem;
import me.BerylliumOranges.spellevent.other.OtherItems;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;
import me.BerylliumOranges.spellevent.segments.b_triggers.Artillery;
import me.BerylliumOranges.spellevent.segments.c_results.Explode;
import me.BerylliumOranges.spellevent.segments.d_modifiers.IgnoreBlocks;
import me.BerylliumOranges.spellevent.segments.d_modifiers.ImpactProjectile;
import me.BerylliumOranges.spellevent.segments.d_modifiers.LengthenProjectile;
import me.BerylliumOranges.spellevent.segments.d_modifiers.ShortenProjectile;

public class ArtilleryCannon extends AbstractSpellItem {
	public static final String NAME = ChatColor.GOLD + "Artillery Cannon";
	public static final int RARITY = 4;
	public static final boolean PLAYER_OBTAINABLE = false;
	public static final boolean SWORD = false;

	public static ItemStack getContainer() {
		return GachaItems.getRandomItem(new ItemStack(Material.DIAMOND_HOE), NAME, RARITY);
	}

	public static ItemStack[][] getSegments() {

		ItemStack shorten = ShortenProjectile.getUpdatedItem(new int[2], 0);
		shorten.setAmount(3);
		return new ItemStack[][] { { null, null, null, null, null, null, null, null, null },
				{ null, null, null, null, OtherItems.quadrupleUpgrade(), null, null, null, null },
				{ null, null, shorten, IgnoreBlocks.getUpdatedItem(new int[2], 1), Explode.getUpdatedItem(new int[2], 0),
						EventSegment.getUpdatedItem(new int[2], 2), null, null, null },
				{ null, null, null, Artillery.getUpdatedItem(new int[2], 1), OtherItems.quadrupleUpgrade(), Artillery.getUpdatedItem(new int[2], 3),
						null, null, null },
				{ null, null, null, ImpactProjectile.getUpdatedItem(new int[2], 0), null, LengthenProjectile.getUpdatedItem(new int[2], 3), null,
						null, null },
				{ null, null, null, null, null, null, null, null, null } };
	}

	public static boolean meetsRequirements(LivingEntity caster) {

		return true;
	}
}
