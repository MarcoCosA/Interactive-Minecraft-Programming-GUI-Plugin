package me.BerylliumOranges.spellevent.entity_information.spellprefabs.wands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.inventory.ItemStack;

import me.BerylliumOranges.misc.GachaItems;
import me.BerylliumOranges.spellevent.entity_information.spellprefabs.AbstractSpellItem;
import me.BerylliumOranges.spellevent.other.OtherItems;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;
import me.BerylliumOranges.spellevent.segments.b_triggers.SingleProjectile;
import me.BerylliumOranges.spellevent.segments.b_triggers.TargetSelf;
import me.BerylliumOranges.spellevent.segments.c_results.Crater;
import me.BerylliumOranges.spellevent.segments.c_results.Knockback;
import me.BerylliumOranges.spellevent.segments.d_modifiers.IgnoreBlocks;
import me.BerylliumOranges.spellevent.segments.d_modifiers.IntermittentCasting;
import me.BerylliumOranges.spellevent.segments.d_modifiers.InvisibleProjectile;
import me.BerylliumOranges.spellevent.segments.d_modifiers.ProjectileSpeed2;
import me.BerylliumOranges.spellevent.segments.d_modifiers.ShortenProjectile;
import me.BerylliumOranges.spellevent.segments.e_logic.PreciseDelay;
import me.BerylliumOranges.spellevent.segments.e_logic.SimultaneousCast;
import me.BerylliumOranges.spellevent.segments.e_logic.Upward;

public class Jump extends AbstractSpellItem {
	public static final String NAME = ChatColor.WHITE + "Jump";
	public static final int RARITY = 4;
	public static final boolean PLAYER_OBTAINABLE = false;
	public static final boolean SWORD = false;

	public static ItemStack getContainer() {
		return GachaItems.getRandomItem(new ItemStack(Material.DIAMOND_HOE), NAME, RARITY);
	}

	public static ItemStack[][] getSegments() {
		return new ItemStack[][] { { null, null, null, null, null, null, null, null, null },
				{ null, null, TargetSelf.getUpdatedItem(new int[2], 0), Upward.getUpdatedItem(new int[2], 0),
						SimultaneousCast.getUpdatedItem(new int[2], 0), PreciseDelay.getUpdatedItem(new int[2], 0),
						Knockback.getUpdatedItem(new int[2], 0), null, null },
				{ null, null, null, IgnoreBlocks.getUpdatedItem(new int[2], 1), IntermittentCasting.getUpdatedItem(new int[2], 2),
						OtherItems.quadrupleUpgrade(), null, null, null },
				{ null, null, null, ProjectileSpeed2.getUpdatedItem(new int[2], 1), OtherItems.quadrupleUpgrade(),
						Crater.getUpdatedItem(new int[2], 0), OtherItems.quadrupleUpgrade(), null, null },
				{ null, null, null, ShortenProjectile.getUpdatedItem(new int[2], 0), InvisibleProjectile.getUpdatedItem(new int[2], 0),
						SingleProjectile.getUpdatedItem(new int[2], 3), null, null, null },
				{ null, null, null, null, null, null, null, null, null } };
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
