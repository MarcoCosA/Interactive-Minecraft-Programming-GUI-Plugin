package me.BerylliumOranges.spellevent.segments.b_triggers;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import me.BerylliumOranges.spellevent.other.Guideline;
import me.BerylliumOranges.spellevent.processes.SegmentProcess;
import me.BerylliumOranges.spellevent.processes.SpellActions;
import me.BerylliumOranges.spellevent.projectiles.SpellProjectile;
import me.BerylliumOranges.spellevent.segments.abstracts.EventTriggerSegment;

public final class PoorShot extends EventTriggerSegment {
	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 2;
	public static final int RARITY = 1;
	public static final int PROJECTILE_COUNT = 1;

	public static final boolean IS_PROJECTILE = true;
	public static final double MULT = 0.007;

	public static Guideline processSegment(SegmentProcess proc) {
		proc.addTimesCloned(PROJECTILE_COUNT);
		if (proc.subtractMana(MANA_COST) < 1)
			return new Guideline(proc, true, true);

//		for (int i = 0; i < PROJECTILE_COUNT; i++) {
		try {

//			proc.getSpellLocation().setDirection(direction);
			SpellProjectile proj = new SpellProjectile(proc, proc.getSpellLocation().clone(),
					proc.getSpellLocation().getDirection());
			Vector direction = new Vector(Math.sqrt(Math.random()) * (Math.random() > 0.5 ? 1 : -1),
					Math.sqrt(Math.random()) * (Math.random() > 0.5 ? 1 : -1),
					Math.sqrt(Math.random()) * (Math.random() > 0.5 ? 1 : -1)).multiply(MULT);
			proj.setAcceleration(proj.getAcceleration().add(direction));
			proj.setCost(SpellProjectile.BASE_COST / 10.0);
		} catch (Exception e) {
			e.printStackTrace();
		}
//		}
		return new Guideline(proc, true, true);
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "Poor Shot");
		ArrayList<String> lore = new ArrayList<String>(Arrays.asList(
				ChatColor.WHITE + "Launches " + ChatColor.YELLOW + PROJECTILE_COUNT + ChatColor.GOLD + " Projectile ",
				"", getCostText(MANA_COST) + " " + ChatColor.YELLOW + SpellActions.getArrowModelFromInt(dir)));
		lore.addAll(getMultiplierLore(MANA_COST_MULTIPLIER));
		meta.setLore(lore);
		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_TRIGGER);
		item.setItemMeta(meta);
		return item;
	}
}
