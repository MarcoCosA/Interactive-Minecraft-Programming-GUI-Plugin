package me.BerylliumOranges.spellevent.entity_information.spellprefabs.armor;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import me.BerylliumOranges.misc.GachaItems;
import me.BerylliumOranges.misc.GachaItems.ItemTrait;
import me.BerylliumOranges.spellevent.entity_information.spellprefabs.AbstractSpellItem;
import me.BerylliumOranges.spellevent.other.OtherItems;
import me.BerylliumOranges.spellevent.segments.b_triggers.TargetSelf;
import me.BerylliumOranges.spellevent.segments.b_triggers.armor_triggers.WhenNearbyDeath;
import me.BerylliumOranges.spellevent.segments.b_triggers.armor_triggers.WhenNearbyEat;
import me.BerylliumOranges.spellevent.segments.c_results.CancelEvent;
import me.BerylliumOranges.spellevent.segments.c_results.CloneItem;
import me.BerylliumOranges.spellevent.segments.c_results.Heal;
import me.BerylliumOranges.spellevent.segments.d_modifiers.PoorOdds;
import me.BerylliumOranges.spellevent.segments.d_modifiers.SoundModifier;
import me.BerylliumOranges.spellevent.segments.e_logic.SimultaneousCast;
import net.md_5.bungee.api.ChatColor;

public class GluttonsPlate extends AbstractSpellItem {
	public static final String NAME = colorTextFade("Glutton's Plate",
			new Color[] { new Color(245, 100, 66), new Color(245, 158, 66) });
	public static final int RARITY = 4;
	public static final boolean PLAYER_OBTAINABLE = true;
	public static final boolean ARMOR = true;

	public static ItemStack getContainer() {
		HashMap<String, Double> traitList = new HashMap<String, Double>();
		traitList.put(ItemTrait.ITEM_MANA_COST.name, -95.0);
		ArrayList<String> lore = new ArrayList<String>();
		ItemStack item = GachaItems.getSpellItem(new ItemStack(Material.DIAMOND_CHESTPLATE), NAME, traitList, RARITY,
				lore);
		return item;
	}

	public static ItemStack[][] getSegments() {
		return new ItemStack[][] { { null, null, null, null, null, null, null, null, null },
				{ null, null, null, WhenNearbyEat.getUpdatedItem(new int[2], 0),
						SimultaneousCast.getUpdatedItem(new int[2], 0), CancelEvent.getUpdatedItem(new int[2], 0), null,
						null, null, null },
				{ null, null, null, null, TargetSelf.getUpdatedItem(new int[2], 1), null, null, null, null },
				{ null, null, null, null, SoundModifier.getDiskPrefab(Sound.ENTITY_GENERIC_EAT.toString()), null, null,
						null, null },
				{ null, null, null, null, OtherItems.doubleUpgrade(), null, null, null, null },
				{ null, null, null, null, Heal.getUpdatedItem(new int[2], 0), null, null, null, null } };
	}
}
