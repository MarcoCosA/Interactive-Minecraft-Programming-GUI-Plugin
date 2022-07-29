package me.BerylliumOranges.spellevent.entity_information.spellprefabs.swords;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import me.BerylliumOranges.misc.GachaItems;
import me.BerylliumOranges.spellevent.entity_information.spellprefabs.AbstractSpellItem;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;
import me.BerylliumOranges.spellevent.segments.b_triggers.TargetSelf;
import me.BerylliumOranges.spellevent.segments.c_results.ApplyPotion;
import me.BerylliumOranges.spellevent.segments.c_results.Sacrifice;
import me.BerylliumOranges.spellevent.segments.d_modifiers.EconomicalMagic;
import me.BerylliumOranges.spellevent.segments.d_modifiers.SoundModifier;
import me.BerylliumOranges.spellevent.segments.e_logic.RandomDirection;

public class MarkOfAPsychic extends AbstractSpellItem {
	public static final String NAME = ChatColor.DARK_PURPLE + "Mark of a Psychic";
	public static final int RARITY = 2;
	public static final boolean PLAYER_OBTAINABLE = true;
	public static final boolean SWORD = true;

	public static ItemStack getContainer() {
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(0, ChatColor.DARK_GRAY + "1/16 chance to try");
		lore.add(1, ChatColor.DARK_GRAY + "and murder you");
		ItemStack item = GachaItems.getRandomItem(new ItemStack(Material.DIAMOND_SWORD), NAME, RARITY - 1, lore);

		return item;
	}

	public static ItemStack[][] getSegments() {
		Potion pot = new Potion(PotionType.STRENGTH, 1, false, true);
		ItemStack potion = pot.toItemStack(1);
//		ItemStack potion = new ItemStack(Material.POTION);
//		PotionMeta meta = (PotionMeta) potion.getItemMeta();
//		meta.setMainEffect(PotionEffectType.INCREASE_DAMAGE);
//		potion.setItemMeta(meta);
		return new ItemStack[][] {
				{ null, null, null, null, ApplyPotion.getUpdatedItem(new int[2], 0), null, null, null, null },
				{ null, null, null, null, potion, null, null, null, null },
				{ null, null, null, null, RandomDirection.getUpdatedItem(new int[2], 3), null, null, null, null },
				{ null, null, null, EconomicalMagic.getUpdatedItem(new int[2], 0),
						TargetSelf.getUpdatedItem(new int[2], 3), null, null, null, null },
				{ null, null, null, null, RandomDirection.getUpdatedItem(new int[2], 3), null, null, null, null },
				{ null, null, null, null, EventSegment.getUpdatedItem(new int[2], 0),
						SoundModifier.getDiskPrefab(Sound.ENTITY_GOAT_DEATH.name()),
						Sacrifice.getUpdatedItem(new int[2], 0), null, null }, };
	}
}
