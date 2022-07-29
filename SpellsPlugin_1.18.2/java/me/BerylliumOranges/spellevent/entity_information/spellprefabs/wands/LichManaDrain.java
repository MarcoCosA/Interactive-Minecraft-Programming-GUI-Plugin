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
import me.BerylliumOranges.spellevent.segments.b_triggers.TargetSelf;
import me.BerylliumOranges.spellevent.segments.c_results.Immobilize;
import me.BerylliumOranges.spellevent.segments.c_results.ManaDrain;
import me.BerylliumOranges.spellevent.segments.e_logic.SimultaneousCast;
import me.BerylliumOranges.spellevent.segments.f_unfair.TargetNearestEnemy;

public class LichManaDrain extends AbstractSpellItem {
	public static final String NAME = ChatColor.DARK_RED + "Drain";
	public static final int RARITY = 4;
	public static final boolean PLAYER_OBTAINABLE = false;
	public static final boolean SWORD = false;

	public static ItemStack getContainer() {
		return GachaItems.getRandomItem(new ItemStack(Material.DIAMOND_HOE), NAME, RARITY);
	}

	public static ItemStack[][] getSegments() {
		return new ItemStack[][] { { null, null, null, null, null, null, null, null, null },
				{ null, null, null, null, TargetNearestEnemy.getUpdatedItem(new int[2], 1), null, null, null, null },
				{ null, null, null, null, OtherItems.quadrupleUpgrade(), null, null, null, null },
				{ null, null, null, OtherItems.quadrupleUpgrade(), ManaDrain.getUpdatedItem(new int[2], 0), OtherItems.quadrupleUpgrade(), null, null,
						null },
				{ null, null, null, null, OtherItems.quadrupleUpgrade(), null, null, null, null },
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
