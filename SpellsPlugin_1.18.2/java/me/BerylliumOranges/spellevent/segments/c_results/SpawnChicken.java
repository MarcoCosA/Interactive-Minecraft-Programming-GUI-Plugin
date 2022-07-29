package me.BerylliumOranges.spellevent.segments.c_results;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Chicken;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import me.BerylliumOranges.spellevent.other.Guideline;
import me.BerylliumOranges.spellevent.processes.SegmentProcess;
import me.BerylliumOranges.spellevent.processes.SpellProcess;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;

public class SpawnChicken extends EventSegment {
	public static final String THIS_SEGMENT_TYPE = SEGMENT_TYPE_RESULT;
	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 20;
	public static final int ORIGINAL_NUMBER = 1;
	public static final int RARITY = 1;

	public static Guideline processSegment(SegmentProcess proc) {
		int[] upgrades = getUpgrades(proc);
		if (proc.subtractMana(MANA_COST) < 1)
			return new Guideline(proc, true, true);

		BukkitTask task = new BukkitRunnable() {
			final int num = getNumber(upgrades);
			int i = 0;

			public void run() {
				Chicken chicken = proc
						.getSpellLocation().getWorld().spawn(
								proc.getSpellLocation().clone().add(0, 0.5, 0)
										.add(getPerpendicularSpawnVector(i,
												proc.getSpellLocation().getDirection().multiply(1 + (i / 2)))),
								Chicken.class);
				chicken.setLootTable(null);
				i++;
				if (i >= num)
					this.cancel();
			}

		}.runTaskTimer(SpellProcess.plugin, 0, 1);
		proc.getSpellSlot().getTasks().add(task);

		return new Guideline(proc, true, false);
	}

	// literally the best solution i could find...
	public static Vector getPerpendicularSpawnVector(int index, Vector spellDirection) {
		if (index == 0)
			return new Vector(0, 0, 0);
		if (index % 2 == 0)
			return new Vector(0, 0, Math.cos(spellDirection.getX()) + Math.sin(spellDirection.getZ()));

		return new Vector(Math.cos(spellDirection.getX()) - Math.sin(spellDirection.getZ()), 0, 0);
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "Spawn Chicken");
		meta.setLore(Arrays.asList(
				ChatColor.WHITE + "Spawns " + ChatColor.GREEN + getNumber(upgrades) + ChatColor.WHITE + " chicken"
						+ (getNumber(upgrades) == 1 ? "" : "s"),
				"", getCostText(MANA_COST)  ));

		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_RESULT);
		item.setItemMeta(meta);
		return item;
	}

	public static int getNumber(int upgrades[]) {
		return (int) (ORIGINAL_NUMBER + (upgrades[0] + upgrades[1]) / 3.0);
	}

}
