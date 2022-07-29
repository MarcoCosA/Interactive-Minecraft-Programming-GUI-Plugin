package me.BerylliumOranges.spellevent.segments.c_results;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;

import me.BerylliumOranges.spellevent.entity_information.entityprefabs.AbstractEntityPrefab;
import me.BerylliumOranges.spellevent.other.Guideline;
import me.BerylliumOranges.spellevent.processes.SegmentProcess;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;

public class SummonEgg extends EventSegment {
	public static final String THIS_SEGMENT_TYPE = SEGMENT_TYPE_RESULT;
	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 80;
	public static final int RARITY = 4;

	public static Guideline processSegment(SegmentProcess proc) {
		int[] upgrades = getUpgrades(proc);
		if (proc.subtractMana(MANA_COST, false) < 1)
			return new Guideline(proc, true, true);
		if (proc.getY() < proc.getTimesProced().length) {
			ItemStack item = proc.getPresentSegments()[proc.getY() + 1][proc.getX()];
			if (item != null && item.hasItemMeta() && item.getItemMeta() instanceof SpawnEggMeta) {
				AbstractEntityPrefab ent = AbstractEntityPrefab.getEntityPrefab(item);
				if (ent == null) {
					try {
						EntityType type = EntityType.valueOf(item.getType().toString().toUpperCase()
								.substring(0, item.getType().toString().toUpperCase().indexOf("_SPAWN_EGG")).trim());
						proc.getSpellLocation().getWorld().spawnEntity(proc.getSpellLocation().clone().add(0, 0.2, 0),
								type);
						alertCaster(proc.getCaster(), ITEM, "Spawned "
								+ StringUtils.capitaliseAllWords(type.toString().toLowerCase().replace("_", " ")),
								false);
					} catch (Exception er) {
						er.printStackTrace();
					}
				} else {
					LivingEntity entity = ent.spawnEntity(proc.getSpellLocation());
					String name = entity.getCustomName();
					if (name == null || name.isEmpty())
						name = entity.getType().toString();
					alertCaster(proc.getCaster(), ITEM,
							"Spawned " + StringUtils.capitaliseAllWords(name.toLowerCase().replace("_", " ")), false);
				}
			}
		}
		return new Guideline(proc, true, false);
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "Summon");
		ArrayList<String> lore = new ArrayList<String>(Arrays.asList(ChatColor.WHITE + "Spawns an entity from the ",
				ChatColor.GREEN + "spawn egg " + ChatColor.WHITE + "under this segment."));
		lore.addAll(Arrays.asList(ChatColor.DARK_GRAY + "", getCostText(MANA_COST)));
		meta.setLore(lore);
		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_RESULT);
		item.setItemMeta(meta);
		return item;
	}

}
