package me.BerylliumOranges.spellevent.entity_information.spellprefabs.armor;

import java.awt.Color;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import me.BerylliumOranges.misc.GachaItems;
import me.BerylliumOranges.spellevent.entity_information.spellprefabs.AbstractSpellItem;
import me.BerylliumOranges.spellevent.segments.b_triggers.armor_triggers.BelowHP;
import me.BerylliumOranges.spellevent.segments.c_results.ApplyPotion;
import net.md_5.bungee.api.ChatColor;

public class Knight extends AbstractSpellItem {
	public static final String NAME = ChatColor.of(Color.LIGHT_GRAY) + "Knight's Plate";
	public static final int RARITY = 1;
	public static final boolean PLAYER_OBTAINABLE = true;
	public static final boolean ARMOR = true;
	
	public static ItemStack getContainer() {
		return GachaItems.getRandomItem(new ItemStack(Material.IRON_CHESTPLATE), NAME, RARITY);
	}

	public static ItemStack[][] getSegments() {
		ItemStack potion = new ItemStack(Material.POTION);
		PotionMeta pm = (PotionMeta) potion.getItemMeta();

		pm.setBasePotionData(new PotionData(PotionType.INSTANT_HEAL));
		pm.addCustomEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 400, 0), false);

		potion.setItemMeta(pm);
		return new ItemStack[][] { { null, null, null, null, null, null, null, null, null },
				{ null, null, null, null, null, null, null, null, null },
				{ null, null, null, BelowHP.getUpdatedItem(new int[] { 25, 0 }, 0),
						ApplyPotion.getUpdatedItem(new int[2], 0), null, null, null, null, null },
				{ null, null, null, null, potion, null, null, null, null },
				{ null, null, null, null, null, null, null, null, null },
				{ null, null, null, null, null, null, null, null, null }, };
	}
}
