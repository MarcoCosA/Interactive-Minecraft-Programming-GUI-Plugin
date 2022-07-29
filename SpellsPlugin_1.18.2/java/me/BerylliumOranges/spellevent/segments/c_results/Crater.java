package me.BerylliumOranges.spellevent.segments.c_results;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.BerylliumOranges.main.SpellPluginMain;
import me.BerylliumOranges.spellevent.other.Guideline;
import me.BerylliumOranges.spellevent.processes.SegmentProcess;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;
import net.md_5.bungee.api.ChatColor;

public class Crater extends EventSegment {
	public static final String THIS_SEGMENT_TYPE = SEGMENT_TYPE_RESULT;
	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 15;
	public static final int ORIGINAL_RADIUS = 2;
	public static final int RARITY = 4;

	public static Guideline processSegment(SegmentProcess proc) {
		int[] upgrades = getUpgrades(proc);
		if (proc.subtractMana(MANA_COST) < 1)
			return new Guideline(proc, true, true);
		sphereTrap(proc.getSpellLocation(), getRadius(upgrades), getDuration(upgrades));
		return new Guideline(proc, true, false);
	}

	public static void sphereTrap(Location center, int radius, int time) {
		HashMap<Location, BlockData> map = new HashMap<Location, BlockData>();
		center.getWorld().playSound(center, Sound.BLOCK_PISTON_CONTRACT, 0.7f, 1.4f);
		if (center.getBlock().getType().equals(Material.REDSTONE_WIRE))
			center.getBlock().setType(Material.AIR);
		for (int Y = -radius; Y < radius; Y++) {
			for (int X = -radius; X < radius; X++) {
				for (int Z = -radius; Z < radius; Z++) {
					if ((X * X) + (Y * Y) + (Z * Z) <= radius * radius) {
						Block b = center.getWorld().getBlockAt(X + center.getBlockX(), Y + center.getBlockY(),
								Z + center.getBlockZ());
						if (!(b.getState() instanceof BlockInventoryHolder) && !b.getState().getType().isAir()) {
							map.put(b.getLocation(), b.getBlockData());
							b.setType(Material.AIR);
						}
					}
				}
			}
		}
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(SpellPluginMain.getInstance(), new Runnable() {
			public void run() {
				for (Map.Entry<Location, BlockData> entry : map.entrySet()) {
					entry.getKey().getBlock().setBlockData(entry.getValue());

				}
			}
		}, time);
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "Crater");
		meta.setLore(Arrays.asList("" + ChatColor.GREEN + getRadius(upgrades) + ChatColor.WHITE + " block radius ", "",
				getCostText(MANA_COST)));

		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_RESULT);
		item.setItemMeta(meta);
		return item;
	}

	public static int getRadius(int upgrades[]) {
		return (int) (ORIGINAL_RADIUS + Math.sqrt(upgrades[0] + upgrades[1]));
	}

	public static int getDuration(int upgrades[]) {
		return 20 + (upgrades[0] + upgrades[1]) * 10;
	}

}
