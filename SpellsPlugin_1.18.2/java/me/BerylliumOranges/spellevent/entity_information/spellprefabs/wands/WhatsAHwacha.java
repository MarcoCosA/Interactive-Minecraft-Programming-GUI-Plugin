package me.BerylliumOranges.spellevent.entity_information.spellprefabs.wands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.BerylliumOranges.misc.GachaItems;
import me.BerylliumOranges.spellevent.entity_information.spellprefabs.AbstractSpellItem;
import me.BerylliumOranges.spellevent.segments.b_triggers.Hwacha;
import me.BerylliumOranges.spellevent.segments.c_results.Explode;
import me.BerylliumOranges.spellevent.segments.d_modifiers.ApplyGravity;
import me.BerylliumOranges.spellevent.segments.d_modifiers.EconomicalMagic;
import me.BerylliumOranges.spellevent.segments.e_logic.Delay;
import me.BerylliumOranges.spellevent.segments.e_logic.SimultaneousCast;

public class WhatsAHwacha extends AbstractSpellItem {
	public static final String NAME = ChatColor.BLUE + "Whats a Hwacha";
	public static final int RARITY = 1;
	public static final boolean PLAYER_OBTAINABLE = true;
	public static final boolean SWORD = false;

	public static ItemStack getContainer() {
		ItemStack item = GachaItems.getRandomItem(new ItemStack(Material.IRON_HOE), NAME, RARITY);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(NAME + "?");
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack[][] getSegments() {
		return new ItemStack[][] { { null, null, null, null, null, null, null, null, null },
				{ null, null, null, EconomicalMagic.getUpdatedItem(new int[2], 1), null, null, null, null, null },
				{ null, null, null, ApplyGravity.getUpdatedItem(new int[2], 0),
						SimultaneousCast.getUpdatedItem(new int[2], 0), Delay.getUpdatedItem(new int[2], 2), null, null,
						null },
				{ null, null, null, Explode.getUpdatedItem(new int[2], 0), Hwacha.getUpdatedItem(new int[2], 2), null,
						null, null, null },
				{ null, null, null, null, null, null, null, null, null },
				{ null, null, null, null, null, null, null, null, null }, };
	}
}
