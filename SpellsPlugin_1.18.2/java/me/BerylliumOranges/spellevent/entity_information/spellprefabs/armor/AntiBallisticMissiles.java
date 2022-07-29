package me.BerylliumOranges.spellevent.entity_information.spellprefabs.armor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import me.BerylliumOranges.misc.GachaItems;
import me.BerylliumOranges.misc.GachaItems.ItemTrait;
import me.BerylliumOranges.spellevent.entity_information.spellprefabs.AbstractSpellItem;
import me.BerylliumOranges.spellevent.segments.b_triggers.Flak;
import me.BerylliumOranges.spellevent.segments.b_triggers.armor_triggers.WhenNearbyProjectile;
import me.BerylliumOranges.spellevent.segments.d_modifiers.ApplyGravity;
import me.BerylliumOranges.spellevent.segments.d_modifiers.IgnoreBlocks;
import me.BerylliumOranges.spellevent.segments.d_modifiers.IgnoreEntities;
import me.BerylliumOranges.spellevent.segments.d_modifiers.InterceptorProjectile;
import me.BerylliumOranges.spellevent.segments.d_modifiers.ProjectileSpeed1;
import me.BerylliumOranges.spellevent.segments.g_spell_focus.Counterspell;
import net.md_5.bungee.api.ChatColor;

public class AntiBallisticMissiles extends AbstractSpellItem {
	public static final String NAME = ChatColor.GRAY + "Antiballistic Missile Defense System";
	public static final int RARITY = 2;
	public static final double MANA_COST_AMOUNT = -60;
	public static final boolean PLAYER_OBTAINABLE = true;
	public static final boolean ARMOR = true;

	public static ItemStack getContainer() {
		HashMap<String, Double> traitList = new HashMap<String, Double>();
		traitList.put(ItemTrait.ITEM_MANA_COST.name, MANA_COST_AMOUNT);
		ArrayList<String> lore = new ArrayList<String>();

		ItemStack item = GachaItems.getSpellItem(new ItemStack(Material.LEATHER_HELMET), NAME, traitList, RARITY, lore);
		LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
		meta.setColor((org.bukkit.Color.fromRGB(180, 180, 180)));
		meta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE,
				new AttributeModifier(UUID.randomUUID(), "GENERIC_KNOCKBACK_RESISTANCE", 10, Operation.ADD_NUMBER, EquipmentSlot.HEAD));
		meta.addAttributeModifier(Attribute.GENERIC_ARMOR,
				new AttributeModifier(UUID.randomUUID(), "GENERIC_ARMOR", 2, Operation.ADD_NUMBER, EquipmentSlot.HEAD));
		meta.addItemFlags(ItemFlag.HIDE_DYE);
		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack[][] getSegments() {
		return new ItemStack[][] { { null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null },
				{ null, null, WhenNearbyProjectile.getUpdatedItem(new int[2], 0), IgnoreEntities.getUpdatedItem(new int[2], 1),
						ApplyGravity.getUpdatedItem(new int[2], 0), ProjectileSpeed1.getUpdatedItem(new int[2], 1), null, null, null },
				{ null, null, null, IgnoreBlocks.getUpdatedItem(new int[2], 0), InterceptorProjectile.getUpdatedItem(new int[2], 3),
						Flak.getUpdatedItem(new int[2], 0), Counterspell.getUpdatedItem(new int[2], 0), null, null },
				{ null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null } };
	}
}
