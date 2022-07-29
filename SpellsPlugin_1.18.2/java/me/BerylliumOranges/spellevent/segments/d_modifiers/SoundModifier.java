package me.BerylliumOranges.spellevent.segments.d_modifiers;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.BerylliumOranges.main.SpellPluginMain;
import me.BerylliumOranges.spellevent.other.Guideline;
import me.BerylliumOranges.spellevent.processes.SegmentProcess;
import me.BerylliumOranges.spellevent.projectiles.SpellProjectile;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSpellModifier;

public class SoundModifier extends EventSpellModifier {

	public static final String THIS_SEGMENT_TYPE = SEGMENT_TYPE_SOUND;
	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public static final double MANA_COST = 1;

	public static final int RARITY = -1;
	public static final int SOUND_NAME_INDEX_IN_LORE = 0;
	public static final ArrayList<ItemStack> ALL_SOUNDS = getAllSounds();
	public static final ArrayList<String> ALL_SOUND_NAMES = new ArrayList<String>();

	public static Guideline processSegment(SegmentProcess proc) {
		if (proc.subtractMana(MANA_COST) < 1)
			return new Guideline(proc, true, true);
		int[] upgrades = getUpgrades(proc);
		try {
			String s = proc.getPresentSegments()[proc.getY()][proc.getX()].getItemMeta().getLocalizedName();
			proc.getSpellLocation().getWorld().playSound(proc.getSpellLocation(),
					Sound.valueOf(SpellPluginMain.getStringInBrackets(s, 1)), 0.5F + upgrades[0] + upgrades[1],
					(float) ((proc.getY() * 2.0) - (proc.getPresentSegments().length - 1))
							/ ((proc.getPresentSegments().length - 1)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return findAndCallNext(proc);
	}

	@Override
	public boolean applyProjectileModifier(SpellProjectile proj, boolean finalCall) {
		return true;
	}

	public static ArrayList<ItemStack> getAllSounds() {
		ArrayList<ItemStack> sounds = new ArrayList<ItemStack>();
		for (int i = 0; i < Sound.values().length; i++) {
			sounds.add(getDiskPrefab(Sound.values()[i].name()));
		}
		return sounds;
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getDiskPrefab(String soundName) {
		ItemStack item = new ItemStack(Material.MUSIC_DISC_STAL);
		ItemMeta meta = item.getItemMeta();
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_DYE);
		meta.setDisplayName(ChatColor.WHITE + "Sound Emitter");
		ArrayList<String> lore = new ArrayList<String>(
				Arrays.asList(ChatColor.AQUA + soundName, "", getCostText(MANA_COST)));
		meta.setLore(lore);
		meta.setLocalizedName(
				THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "][" + soundName + "]");
		item.setItemMeta(meta);
		return item;
	}

	public SoundModifier(SegmentProcess proc) {
		super(proc);
	}
}
