package me.BerylliumOranges.spellevent.entity_information.spellprefabs.wands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import me.BerylliumOranges.misc.GachaItems;
import me.BerylliumOranges.spellevent.entity_information.spellprefabs.AbstractSpellItem;
import me.BerylliumOranges.spellevent.other.OtherItems;
import me.BerylliumOranges.spellevent.segments.b_triggers.Artillery;
import me.BerylliumOranges.spellevent.segments.b_triggers.Flak;
import me.BerylliumOranges.spellevent.segments.c_results.ApplySplashPotion;
import me.BerylliumOranges.spellevent.segments.d_modifiers.ApplyGravity;
import me.BerylliumOranges.spellevent.segments.d_modifiers.EconomicalMagic;
import me.BerylliumOranges.spellevent.segments.d_modifiers.IgnoreBlocks;
import me.BerylliumOranges.spellevent.segments.d_modifiers.ProjectileProximityProximity;
import me.BerylliumOranges.spellevent.segments.d_modifiers.ShortenProjectile;
import me.BerylliumOranges.spellevent.segments.d_modifiers.SlowProjectile;

public class ArtillerySlowCannon extends AbstractSpellItem {
	public static final String NAME = ChatColor.AQUA + "Litter The Ground With Broken Glass";
	public static final int RARITY = 4;
	public static final boolean PLAYER_OBTAINABLE = false;
	public static final boolean SWORD = false;

	public static ItemStack getContainer() {
		return GachaItems.getRandomItem(new ItemStack(Material.DIAMOND_HOE), NAME, RARITY);
	}

	public static ItemStack[][] getSegments() {
		Potion pot = new Potion(PotionType.SLOWNESS, 1, true, false);
		ItemStack potion = pot.toItemStack(1);
		return new ItemStack[][] { { null, null, null, null, null, null, null, null, null },
				{ null, null, EconomicalMagic.getUpdatedItem(new int[2], 0), IgnoreBlocks.getUpdatedItem(new int[2], 1),
						ApplyGravity.getUpdatedItem(new int[2], 0), Flak.getUpdatedItem(new int[2], 1), null, null, null },
				{ null, null, OtherItems.quadrupleUpgrade(), ProjectileProximityProximity.getUpdatedItem(new int[2], 1),
						OtherItems.quadrupleUpgrade(), ApplySplashPotion.getUpdatedItem(new int[2], 0), null, null, null },
				{ null, null, null, Artillery.getUpdatedItem(new int[2], 0), SlowProjectile.getUpdatedItem(new int[2], 3), potion, null, null, null },
				{ null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null } };
	}

	public static boolean meetsRequirements(LivingEntity caster) {

		return true;
	}
}
