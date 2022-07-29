package me.BerylliumOranges.spellevent.entity_information.RPGClasses;

import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.BerylliumOranges.misc.Directories;
import me.BerylliumOranges.spellevent.entity_information.EntityStats;

public abstract class AbstractRPGClass {

	public final static ArrayList<AbstractRPGClass> ALL_RPG_CLASSES = loadRPGDummyObjects();

	public static final ArrayList<ItemStack> NEUTRAL_SKILLS = new ArrayList<ItemStack>();

	public enum Skills {
		EXTRA_KIDNEY(getExtraKidney());

		public ItemStack item;
		public String name;

		private Skills(ItemStack item) {
			this.item = item;
			this.name = item.getItemMeta().getDisplayName();
		}
	}

	public abstract ArrayList<ItemStack> getSkillItems(int tier);

	public abstract ItemStack getMenuItem();

	public abstract void onPurchaseClass(Player p);

	public abstract void onPurchaseSkill(ItemStack item, Player p);

	public abstract int getHealthBonus();

	public abstract int getManaBonus();

	public static void onPurchaseNeutralSkill(ItemStack item, Player p) {

	}

	@SuppressWarnings("unchecked")
	public static ArrayList<AbstractRPGClass> loadRPGDummyObjects() {
		ArrayList<AbstractRPGClass> classesList = new ArrayList<AbstractRPGClass>();
		String path = "me.BerylliumOranges.spellevent.entity_information.RPGClasses";
		for (Class<?> clazz : Directories.getClasses(path)) {
			try {
				if (AbstractRPGClass.class.isAssignableFrom(clazz) && clazz != AbstractRPGClass.class) {
					AbstractRPGClass rpg = (AbstractRPGClass) clazz.newInstance();
					classesList.add(rpg);
				}
			} catch (Exception er) {
				er.printStackTrace();
			}
		}
		return classesList;
	}

	public final static String INVENTORY_TAG_SKILL = ChatColor.DARK_RED + "Choose a Class Skill";
	public final static String INVENTORY_TAG_NEUTRAL_SKILL = ChatColor.YELLOW + "Choose a Neutral Skill";
	public final static String INVENTORY_TAG_CLASS = ChatColor.GOLD + "Choose a Class";

	public static Inventory getSkillsInventory(Player p) {
		Inventory inv = Bukkit.createInventory(p, 54, INVENTORY_TAG_SKILL);
		try {
			EntityStats stat = EntityStats.getEntityStats(p);
			for (String s : stat.getRPGClasses()) {
				for (AbstractRPGClass rpg : ALL_RPG_CLASSES) {
					if (rpg.getMenuItem().getItemMeta().getLocalizedName().equals(s)) {
						for (ItemStack item : rpg.getSkillItems(-1)) {
							inv.addItem(item);
						}
					}
				}
			}
		} catch (Exception er) {
			er.printStackTrace();
		}
		return inv;
	}

	public static Inventory getNeutralSkillsInventory(Player p) {
		Inventory inv = Bukkit.createInventory(p, 54, INVENTORY_TAG_SKILL);
		for (Skills s : Skills.values()) {
			inv.addItem(s.item);
		}
		return inv;
	}

	public static Inventory getRPGClassesInventory(Player p) {
		Inventory inv = Bukkit.createInventory(p, 9, INVENTORY_TAG_CLASS);
		try {
			EntityStats stat = EntityStats.getEntityStats(p);
			for (AbstractRPGClass rpg : ALL_RPG_CLASSES) {

				ItemStack item = rpg.getMenuItem();
				if (!stat.getRPGClasses().contains(item.getItemMeta().getDisplayName())) {
					ItemMeta meta = item.getItemMeta();
					ArrayList<String> lore = new ArrayList<String>(meta.getLore());
					lore.add("");
					lore.add(getSign(rpg.getHealthBonus()) + rpg.getHealthBonus() + ChatColor.RED + " Permanent Max Health");
					lore.add(getSign(rpg.getManaBonus()) + rpg.getManaBonus() + ChatColor.AQUA + " Permanent Max Mana");
					meta.setLore(lore);
					item.setItemMeta(meta);
					inv.addItem(item);
				}
			}
		} catch (Exception er) {
			er.printStackTrace();
		}
		return inv;
	}

	private static ChatColor[] colors = { ChatColor.WHITE, ChatColor.GREEN, ChatColor.YELLOW, ChatColor.RED };

	@SuppressWarnings("deprecation")
	public static ItemStack getSkillTotem(Player p, int tier) {
		ItemStack item = new ItemStack(Material.TOTEM_OF_UNDYING);
		ItemMeta meta = item.getItemMeta();
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.setDisplayName(
				colors[(int) Math.min(tier, colors.length - 1)] + "Tier " + tier + ChatColor.GRAY + "/3" + ChatColor.WHITE + " Skill Totem");
		meta.setLore(Arrays.asList(ChatColor.GRAY + "Click me to add a skill"));
		meta.setLocalizedName(INVENTORY_TAG_SKILL + "[" + p.getUniqueId() + "][" + tier + "]");
		item.setItemMeta(meta);
		return item;
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getNeutralSkillTotem(Player p, int tier) {
		ItemStack item = new ItemStack(Material.TOTEM_OF_UNDYING);
		ItemMeta meta = item.getItemMeta();
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.setDisplayName(
				colors[(int) Math.min(tier, colors.length - 1)] + "Tier " + tier + ChatColor.GRAY + "/2" + ChatColor.WHITE + " Neutral Skill Totem");
		meta.setLore(Arrays.asList(ChatColor.GRAY + "Click me to add a skill"));
		meta.setLocalizedName(INVENTORY_TAG_NEUTRAL_SKILL + "[" + p.getUniqueId() + "][" + tier + "]");
		item.setItemMeta(meta);
		return item;
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getClassTotem(Player p) {
		ItemStack item = new ItemStack(Material.TOTEM_OF_UNDYING);
		ItemMeta meta = item.getItemMeta();
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.setDisplayName(ChatColor.GOLD + "Class Totem");
		meta.setLore(Arrays.asList(ChatColor.GRAY + "Click me to add a class"));
		meta.setLocalizedName(INVENTORY_TAG_CLASS + "[" + p.getUniqueId() + "]");
		item.setItemMeta(meta);
		item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
		return item;
	}

	// NEUTRAL SKILL ITEMS ------------------------------

	@SuppressWarnings("deprecation")
	public static ItemStack getExtraKidney() {
		ItemStack item = new ItemStack(Material.BEEF);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.DARK_RED + "Extra Kidney");
		meta.setLore(Arrays.asList(ChatColor.RED + "+10% Permanent Max Health"));
		meta.setLocalizedName(meta.getDisplayName());
		item.setItemMeta(meta);
		return item;
	}

	public static void givePlayerItem(ItemStack item, Player p) {
		if (p.getInventory().first(item) >= 0 || p.getInventory().firstEmpty() >= 0) {
			p.getInventory().addItem(item);
		} else {
			p.getWorld().dropItem(p.getLocation(), item);
		}
	}

	public static String getSign(double input) {
		if (input < 0) {
			return ChatColor.RED + "";
		}
		return ChatColor.BLUE + "+";
	}
}
