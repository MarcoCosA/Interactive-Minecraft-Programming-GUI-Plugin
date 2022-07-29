package me.BerylliumOranges.spellevent.entity_information.spellprefabs.wands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.inventory.ItemStack;

import me.BerylliumOranges.misc.GachaItems;
import me.BerylliumOranges.spellevent.entity_information.EntityStats;
import me.BerylliumOranges.spellevent.entity_information.spellprefabs.AbstractSpellItem;
import me.BerylliumOranges.spellevent.other.OtherItems;
import me.BerylliumOranges.spellevent.segments.b_triggers.SingleProjectile;
import me.BerylliumOranges.spellevent.segments.b_triggers.TargetSelf;
import me.BerylliumOranges.spellevent.segments.c_results.Crater;
import me.BerylliumOranges.spellevent.segments.c_results.Knockback;
import me.BerylliumOranges.spellevent.segments.c_results.Mirror;
import me.BerylliumOranges.spellevent.segments.c_results.Teleport;
import me.BerylliumOranges.spellevent.segments.d_modifiers.IgnoreBlocks;
import me.BerylliumOranges.spellevent.segments.d_modifiers.IntermittentCasting;
import me.BerylliumOranges.spellevent.segments.d_modifiers.ProjectileSpeed2;
import me.BerylliumOranges.spellevent.segments.d_modifiers.ProjectileSpeed3;
import me.BerylliumOranges.spellevent.segments.d_modifiers.RemoveCrit;
import me.BerylliumOranges.spellevent.segments.d_modifiers.ShortenProjectile;
import me.BerylliumOranges.spellevent.segments.e_logic.Downward;
import me.BerylliumOranges.spellevent.segments.e_logic.PreciseDelay;
import me.BerylliumOranges.spellevent.segments.e_logic.SimultaneousCast;
import me.BerylliumOranges.spellevent.segments.e_logic.Upward;
import me.BerylliumOranges.spellevent.segments.f_unfair.ResetItemCooldowns;
import me.BerylliumOranges.spellevent.segments.f_unfair.TargetNearestEnemy;

public class TeleportAboveEnemy extends AbstractSpellItem {
	public static final String NAME = ChatColor.WHITE + "Teleport Above";
	public static final int RARITY = 4;
	public static final boolean PLAYER_OBTAINABLE = false;
	public static final boolean SWORD = false;

	public static ItemStack getContainer() {
		return GachaItems.getRandomItem(new ItemStack(Material.DIAMOND_HOE), NAME, RARITY);
	}

	public static ItemStack[][] getSegments() {
		return new ItemStack[][] { { null, null, null, null, null, null, null, null, null },
				{ null, null, ShortenProjectile.getUpdatedItem(new int[2], 1), SingleProjectile.getUpdatedItem(new int[2], 0),
						SimultaneousCast.getUpdatedItem(new int[2], 0), Teleport.getUpdatedItem(new int[2], 0), null, null, null },
				{ null, null, TargetNearestEnemy.getUpdatedItem(new int[2], 1), ProjectileSpeed2.getUpdatedItem(new int[2], 3),
						ShortenProjectile.getUpdatedItem(new int[2], 1), null, null, null, null },
				{ null, null, Upward.getUpdatedItem(new int[2], 0), IgnoreBlocks.getUpdatedItem(new int[2], 3),
						ProjectileSpeed3.getUpdatedItem(new int[2], 1), SingleProjectile.getUpdatedItem(new int[2], 0),
						Crater.getUpdatedItem(new int[2], 0), null, null },
				{ null, null, null, null, Downward.getUpdatedItem(new int[2], 0), IntermittentCasting.getUpdatedItem(new int[2], 3),
						OtherItems.doubleUpgrade(), null, null },
				{ null, null, null, null, null, OtherItems.quadrupleUpgrade(), null, null, null } };
	}

	public static boolean meetsRequirements(LivingEntity caster) {
		if (caster instanceof Mob) {
			Mob mob = (Mob) caster;
			if (mob.getTarget() == null) {
				if (mob instanceof IronGolem) {
					EntityStats stats = EntityStats.getEntityStats(mob);
					stats.segmentCooldowns.clear();
				}
				return false;
			}
		}
		return true;
	}
}
