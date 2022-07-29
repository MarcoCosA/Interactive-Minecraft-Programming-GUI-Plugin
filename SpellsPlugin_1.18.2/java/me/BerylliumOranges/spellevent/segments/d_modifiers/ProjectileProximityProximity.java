package me.BerylliumOranges.spellevent.segments.d_modifiers;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.BerylliumOranges.main.SpellPluginMain;
import me.BerylliumOranges.spellevent.other.Guideline;
import me.BerylliumOranges.spellevent.processes.SegmentProcess;
import me.BerylliumOranges.spellevent.processes.SpellActions;
import me.BerylliumOranges.spellevent.projectiles.SpellProjectile;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSpellModifier;
import net.md_5.bungee.api.ChatColor;

public class ProjectileProximityProximity extends EventSpellModifier {
	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 5;
	public static final double ORIGINAL_RADIUS = 1.5;
	public static final int RARITY = 2;

	public static Guideline processSegment(SegmentProcess proc) {
		proc.incrementGeneration();
		if (proc.subtractMana(MANA_COST) < 1)
			return new Guideline(proc, true, true);
		proc.getProjectileModifiers().add(new ProjectileProximityProximity(proc));
		return findAndCallNext(proc);
	}

	public ProjectileProximityProximity(SegmentProcess proc) {
		super(proc);
	}

	@Override
	public boolean applyProjectileModifier(SpellProjectile proj, boolean finalCall) {
		for (Entity ent : SpellPluginMain.getNearbyEntities(proj.getLocation(), getRadius(upgrades))) {
			if (ent instanceof LivingEntity && !ent.isDead() && !ent.equals(proj.getProcess().getCaster())) {
				proj.setTicksLived(proj.getLifespan() + 1);
			}
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "" + ChatColor.UNDERLINE + "Projectile" + ChatColor.RESET + getColorFromRarity(RARITY)
				+ " Proximity Activation");
		meta.setLore(Arrays.asList(ChatColor.WHITE + "Projectile ends when an entity gets",
				ChatColor.WHITE + "within " + ChatColor.AQUA + getRadius(upgrades) + ChatColor.WHITE + " blocks", "",
				getCostText(MANA_COST) + " " + SpellActions.getArrowModelFromInt(dir)));
		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_MODIFIER);
		item.setItemMeta(meta);
		return item;
	}

	public static double getRadius(int[] upgrades) {
		return ORIGINAL_RADIUS + (upgrades[0] + upgrades[1]) * 0.5;
	}

}
