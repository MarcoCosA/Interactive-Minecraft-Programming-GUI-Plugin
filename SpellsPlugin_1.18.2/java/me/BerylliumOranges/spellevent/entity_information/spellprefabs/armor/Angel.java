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
import me.BerylliumOranges.spellevent.segments.b_triggers.DomainExpansion;
import me.BerylliumOranges.spellevent.segments.b_triggers.TargetSelf;
import me.BerylliumOranges.spellevent.segments.b_triggers.armor_triggers.BelowHP;
import me.BerylliumOranges.spellevent.segments.c_results.ApplyPotion;
import me.BerylliumOranges.spellevent.segments.d_modifiers.PerfectDomain;
import net.md_5.bungee.api.ChatColor;

public class Angel extends AbstractSpellItem {
	public static final Color TEXT_COLOR = new Color(0, 230, 230);
	public static final String NAME = colorTextFade("Angel's Plate",
			new Color[] { TEXT_COLOR, Color.WHITE, TEXT_COLOR });
	public static final int RARITY = 3;
	public static final boolean PLAYER_OBTAINABLE = true;
	public static final boolean ARMOR = true;

	public static ItemStack getContainer() {
		return GachaItems.getRandomItem(new ItemStack(Material.DIAMOND_CHESTPLATE), NAME, RARITY);
	}

	public static ItemStack[][] getSegments() {
		ItemStack potion = new ItemStack(Material.POTION);
		PotionMeta pm = (PotionMeta) potion.getItemMeta();

		pm.setBasePotionData(new PotionData(PotionType.THICK));
		pm.addCustomEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 160, 4), false);
		pm.addCustomEffect(new PotionEffect(PotionEffectType.WEAKNESS, 160, 4), false);
		pm.setDisplayName(ChatColor.GRAY + "Potion of the Ultimate Safety");
		potion.setItemMeta(pm);
		return new ItemStack[][] { { null, null, null, null, null, null, null, null, null },
				{ null, null, null, null, null, null, null, null, null },
				{ null, null, null, BelowHP.getUpdatedItem(new int[2], 1),
						DomainExpansion.getUpdatedItem(new int[2], 0), ApplyPotion.getUpdatedItem(new int[2], 0), null,
						null, null, null },
				{ null, null, null, TargetSelf.getUpdatedItem(new int[2], 0),
						PerfectDomain.getUpdatedItem(new int[2], 3), potion, null, null, null },
				{ null, null, null, null, null, null, null, null, null },
				{ null, null, null, null, null, null, null, null, null }, };
	}
}
