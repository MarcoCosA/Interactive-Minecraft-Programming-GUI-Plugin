package me.BerylliumOranges.spellevent.entity_information.RPGClasses;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.BerylliumOranges.spellevent.entity_information.EntityStats;

public class Sorcerer extends AbstractRPGClass {
	public static enum Skills {
		EXTRA_SPELL(getExtraSpellSorcerer());

		public ItemStack item;
		public String name;

		private Skills(ItemStack item) {
			this.item = item;
			this.name = item.getItemMeta().getDisplayName();
		}
	}

	@Override
	public void onPurchaseClass(Player p) {
		EntityStats stat = EntityStats.getEntityStats(p);
	}

	@Override
	public void onPurchaseSkill(ItemStack item, Player p) {
		if (item.getItemMeta().getLocalizedName().equals("example    ")) {
			givePlayerItem(item, p);
		}
	}

	@Override
	public int getHealthBonus() {
		return 10;
	}

	@Override
	public int getManaBonus() {
		return 100;
	}

	@Override
	public ItemStack getMenuItem() {
		ItemStack item = new ItemStack(Material.AMETHYST_CLUSTER);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.DARK_PURPLE + "Sorcerer");
		meta.setLore(Arrays.asList(ChatColor.AQUA + "Immediate Bonus " + ChatColor.GRAY + "- ", ChatColor.GRAY + "You gain an extra spell slot but",
				ChatColor.GRAY + "you have a 20% chance to cast", ChatColor.GRAY + "a random segment every spell"));
		meta.setLocalizedName(meta.getDisplayName());
		item.setItemMeta(meta);
		return item;
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getRandomTrait() {
		ItemStack item = new ItemStack(Material.AMETHYST_CLUSTER);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.DARK_PURPLE + "" + ChatColor.LIGHT_PURPLE + "Random Trait");
		meta.setLore(Arrays.asList(ChatColor.GRAY + "Gain an extra spell slot but", ChatColor.GRAY + "the chance of a random segment",
				ChatColor.GRAY + "being cast is 35%"));
		item.setItemMeta(meta);
		meta.setLocalizedName(meta.getDisplayName());
		return item;
	}
	
	@SuppressWarnings("deprecation")
	public static ItemStack getExtraSpellSorcerer() {
		ItemStack item = new ItemStack(Material.AMETHYST_CLUSTER);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.DARK_PURPLE + "Sorcerer: " + ChatColor.LIGHT_PURPLE + "Random Magic");
		meta.setLore(Arrays.asList(ChatColor.GRAY + "Gain an extra spell slot but", ChatColor.GRAY + "the chance of a random segment",
				ChatColor.GRAY + "being cast is 35%"));
		item.setItemMeta(meta);
		meta.setLocalizedName(meta.getDisplayName());
		return item;
	}

	public static ArrayList<ItemStack> loadItemsList() {
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		for (Skills s : Skills.values()) {
			items.add(s.item);
		}
		return items;
	}

	@Override
	public ArrayList<ItemStack> getSkillItems(int tier) {
		return loadItemsList();
	}
}
