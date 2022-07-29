package me.BerylliumOranges.spellevent.segments.f_unfair;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.BerylliumOranges.main.SpellPluginMain;
import me.BerylliumOranges.spellevent.entity_information.EntityStats;
import me.BerylliumOranges.spellevent.other.Guideline;
import me.BerylliumOranges.spellevent.processes.SegmentProcess;
import me.BerylliumOranges.spellevent.processes.SpellActions;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;

public class ResetItemCooldowns extends EventSegment {
	public static final String THIS_SEGMENT_TYPE = SEGMENT_TYPE_LOGIC;
	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 0;
	public static final int RARITY = 5;
	public static final double MULTIPLIER = 10;

	public static Guideline processSegment(SegmentProcess proc) {
		if (proc.subtractMana(MANA_COST) < 1)
			return new Guideline(proc, true, true);
		EntityStats stats = EntityStats.getEntityStats(proc.getCaster());
		stats.segmentCooldowns.clear();
		return findAndCallNext(proc);
	}

	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.RED + "Reset Item Cooldowns");
		meta.setLore(Arrays.asList(ChatColor.RED + "Targets " + ChatColor.WHITE + "a random player if the",
				ChatColor.YELLOW + "caster " + ChatColor.WHITE + "is a mob and vice versa", "",
				EventSegment.getCostText(MANA_COST) + " " + ChatColor.RED + SpellActions.getArrowModelFromInt(dir)));
		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_UNFAIR);
		item.setItemMeta(meta);
		return item;
	}

	public static LivingEntity getNearestEnemy(LivingEntity caster) {
		double minDistance = Double.MAX_VALUE;
		EntityStats stats = EntityStats.getEntityStats(caster);

		boolean isEnemy = stats.isEnemy();
		LivingEntity target = null;

		for (Entity ent : SpellPluginMain.getNearbyEntities(caster.getLocation(), 100)) {
			if (ent instanceof LivingEntity && !ent.equals(caster)) {
				if (isEnemy && ent instanceof Player) {
					if (minDistance >= caster.getLocation().distanceSquared(ent.getLocation())) {
						minDistance = caster.getLocation().distanceSquared(ent.getLocation());
						target = (LivingEntity) ent;
					}
				} else if (!isEnemy) {
					EntityStats targetStats = EntityStats.getEntityStats((LivingEntity) ent);
					if (targetStats.isEnemy()) {
						if (minDistance >= caster.getLocation().distanceSquared(ent.getLocation())) {
							minDistance = caster.getLocation().distanceSquared(ent.getLocation());
							target = (LivingEntity) ent;
						}
					}
				}
			}
		}
		return target;
	}
}
