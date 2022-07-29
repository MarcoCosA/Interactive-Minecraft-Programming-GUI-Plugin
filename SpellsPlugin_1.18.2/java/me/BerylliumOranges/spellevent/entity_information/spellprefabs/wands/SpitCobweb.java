package me.BerylliumOranges.spellevent.entity_information.spellprefabs.wands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.inventory.ItemStack;

import me.BerylliumOranges.misc.GachaItems;
import me.BerylliumOranges.spellevent.entity_information.spellprefabs.AbstractSpellItem;
import me.BerylliumOranges.spellevent.other.OtherItems;
import me.BerylliumOranges.spellevent.segments.b_triggers.SingleProjectile;
import me.BerylliumOranges.spellevent.segments.c_results.DealDamage;
import me.BerylliumOranges.spellevent.segments.d_modifiers.BlockFallingPath;
import me.BerylliumOranges.spellevent.segments.d_modifiers.ContinueOnHitProjectile;
import me.BerylliumOranges.spellevent.segments.d_modifiers.IgnoreBlocks;
import me.BerylliumOranges.spellevent.segments.d_modifiers.IgnoreEntities;
import me.BerylliumOranges.spellevent.segments.d_modifiers.InvisibleProjectile;
import me.BerylliumOranges.spellevent.segments.d_modifiers.ProjectileSpeed3;
import me.BerylliumOranges.spellevent.segments.d_modifiers.ShortenProjectile;

public class SpitCobweb extends AbstractSpellItem {
	public static final String NAME = ChatColor.DARK_RED + "Lobster Silk";
	public static final int RARITY = 5;
	public static final boolean PLAYER_OBTAINABLE = false;
	public static final boolean SWORD = false;

	public static ItemStack getContainer() {
		return GachaItems.getRandomItem(new ItemStack(Material.IRON_HOE), NAME, 4);
	}

	public static ItemStack[][] getSegments() {
		ItemStack seg = ShortenProjectile.getUpdatedItem(new int[2], 1);
		seg.setAmount(2);
		return new ItemStack[][] { { null, null, null, null, null, null, null, null, null },
				{ null, null, null, null, null, null, null, null, null },
				{ null, null, null, BlockFallingPath.getUpdatedItem(new int[2], 0),
						InvisibleProjectile.getUpdatedItem(new int[2], 0),
						ContinueOnHitProjectile.getUpdatedItem(new int[2], 1), null, null, null },
				{ null, null, null, OtherItems.doubleUpgrade(), null, seg, null, null, null },
				{ null, null, DealDamage.getUpdatedItem(new int[2], 0), SingleProjectile.getUpdatedItem(new int[2], 2),
						IgnoreBlocks.getUpdatedItem(new int[2], 2), ProjectileSpeed3.getUpdatedItem(new int[2], 2),
						null, null, null },
				{ null, null, null, null, null, null, null, null, null }, };
	}

	public static boolean meetsRequirements(LivingEntity caster) {
		if (caster instanceof Mob) {
			Mob mob = (Mob) caster;
			if (mob.getTarget() == null) {
				return false;
			}
		}
		return true;
	}

}
