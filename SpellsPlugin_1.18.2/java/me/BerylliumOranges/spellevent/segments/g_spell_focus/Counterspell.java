package me.BerylliumOranges.spellevent.segments.g_spell_focus;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.BerylliumOranges.spellevent.entity_information.EntityStats;
import me.BerylliumOranges.spellevent.entity_information.SpellSlot;
import me.BerylliumOranges.spellevent.other.Guideline;
import me.BerylliumOranges.spellevent.processes.SegmentProcess;
import me.BerylliumOranges.spellevent.processes.SpellActions;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;

public class Counterspell extends EventSegment {
	public static final String THIS_SEGMENT_TYPE = SEGMENT_TYPE_RESULT;
	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 25;
	public static final int RARITY = 2;

	public static Guideline processSegment(SegmentProcess proc) {
		if (proc.subtractMana(MANA_COST) < 1)
			return new Guideline(proc, true, true);

		for (SegmentProcess f : proc.getFocusedSpellsCopy()) {
			f.getSpellSlot().stopSpell();
			Location loc = f.getCaster().getLocation().add(0, f.getCaster().getHeight() / 2.0, 0);
			loc.getWorld().playSound(loc, Sound.BLOCK_BELL_USE, 1.05F, 2F);
			try {
				for (int i = 0; i < 7; i++)
					f.getCaster().getWorld().spawnParticle(Particle.SPELL_MOB, loc.getX() + Math.random() - 0.5, loc.getY() + Math.random() - 0.3,
							loc.getZ() + (Math.random() - 0.5) * 2, 0, RED, GREEN, BLUE, 1, null, true);
			} catch (Exception er) {
				er.printStackTrace();
			}
		}

		return new Guideline(proc, true, false);
	}

	public static final double RED = 75 / 255D;
	public static final double GREEN = 20 / 255D;
	public static final double BLUE = 255 / 255D;

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "Counterspell");
		meta.setLore(Arrays.asList(ChatColor.WHITE + "Stops all " + ChatColor.LIGHT_PURPLE + "focused " + ChatColor.WHITE + "spells", "",
				getCostText(MANA_COST)));

		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_SPELL_FOCUS_RESULT);
		item.setItemMeta(meta);
		return item;
	}
}
