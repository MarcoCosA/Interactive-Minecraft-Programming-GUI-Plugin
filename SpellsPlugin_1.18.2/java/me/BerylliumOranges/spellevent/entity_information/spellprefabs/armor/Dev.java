package me.BerylliumOranges.spellevent.entity_information.spellprefabs.armor;

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

public class Dev extends AbstractSpellItem {
	public static final String NAME = ChatColor.RED + "Dev Armor";
	public static final int RARITY = 4;
	public static final boolean PLAYER_OBTAINABLE = false;

	public static ItemStack getContainer() {
		return GachaItems.getRandomItem(new ItemStack(Material.DIAMOND_CHESTPLATE), NAME, RARITY);
	}

	public static ItemStack[][] getSegments() {

		return new ItemStack[][] {
				{ new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR),
						new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR),
						new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR) },
				{ new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR),
						new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR),
						new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR) },
				{ new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR),
						new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR),
						new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR),
						new ItemStack(Material.AIR) },
				{ new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR),
						new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR),
						new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR) },
				{ new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR),
						new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR),
						new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR) },
				{ new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR),
						new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR),
						new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR) }, };
	}
}
