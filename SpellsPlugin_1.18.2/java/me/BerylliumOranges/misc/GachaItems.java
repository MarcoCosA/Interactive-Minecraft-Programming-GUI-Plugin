package me.BerylliumOranges.misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.BerylliumOranges.spellevent.other.OtherItems;
import me.BerylliumOranges.spellevent.processes.EventItemStorage;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;
import net.md_5.bungee.api.ChatColor;

public class GachaItems {

	public static final String SHOPKEEPER_TAG = ChatColor.YELLOW + "Shopekeeper";

	public static final String SPELL_ITEM_TAG = "EventItem";
	public static final int BOSS_CHANCE = 10;
	public static final int LEGENDARY_CHANCE = 5;
	public static final int EPIC_CHANCE = 15;
	public static final int RARE_CHANCE = 30;
	public static final int COMMON_CHANCE = 50;

	// Only use default minecraft color codes here
	public static enum ItemTrait {
		LIFESTEAL(ChatColor.DARK_RED + "Lifesteal", 20, true),
		CRIT_CHANCE(ChatColor.DARK_PURPLE + "Spell Crit Chance", 20, true),
		MOVEMENT_SPEED(ChatColor.BLUE + "Movement Speed", 30, true),
		MAX_MANA(ChatColor.LIGHT_PURPLE + "Max Mana", 30, true), MAX_HEALTH(ChatColor.RED + "Max Health", 35, true),
		ITEM_MANA_COST(ChatColor.GOLD + "Item Cooldown", 0, false), HEALING(ChatColor.GREEN + "Healing", 0, false);

		public double range;
		public String name;
		private boolean available;

		private ItemTrait(String name, double range, boolean available) {
			this.name = name;
			this.range = range;
			this.available = available;
		}

		public static final ArrayList<ItemTrait> WILD_TRAITS = getAvailableTraits();

		private static ArrayList<ItemTrait> getAvailableTraits() {
			ArrayList<ItemTrait> traits = new ArrayList<ItemTrait>();
			for (ItemTrait t : ItemTrait.values()) {
				if (t.available) {
					traits.add(t);
				}
			}
			return traits;
		}
	}

	public static int randomRarityInt() {
		int rand = (int) (Math.random() * 100);

		if (rand > 100 - LEGENDARY_CHANCE)
			return 3;
		if (rand > 100 - EPIC_CHANCE)
			return 2;
		if (rand > 100 - RARE_CHANCE)
			return 1;
		return 0;
	}

	public static int randomRarityInt(long seed, int rarityMax) {
		int rand = (int) (new Random(seed).nextDouble() * 100);

		if (rand > 100 - BOSS_CHANCE)
			return 4;
		if (rand > 100 - (LEGENDARY_CHANCE * 2) - BOSS_CHANCE)
			return 3;
		if (rand > 100 - EPIC_CHANCE - BOSS_CHANCE)
			return 2;
		if (rand > 100 - RARE_CHANCE - BOSS_CHANCE)
			return 1;
		return 0;
	}

//	public static final String CURRENCY_TAG = ChatColor.LIGHT_PURPLE + "Monster Shard";
	public static final ItemStack CURRENCY = getCurrency(1);

	public static ItemStack getCurrency(int amount) {
		ItemStack item = new ItemStack(Material.EMERALD, amount);
//		ItemMeta meta = item.getItemMeta();
//		meta.setDisplayName(CURRENCY_TAG);
//		item.setItemMeta(meta);
		return item;
	}

	public static List<ItemStack> getCurrencyAsList(int amount) {

		if (amount > 64) {
			int leftover = amount - 64;
			return Arrays.asList(getCurrency(64), getCurrency(Math.min(64, leftover)));
		}
		return new ArrayList<ItemStack>(Arrays.asList(getCurrency(amount)));
	}

	public static boolean isArmor(Material type) {
		String s = type.toString().toUpperCase();
		if (s.contains("HELMET") || s.contains("CHEST") || s.contains("LEGGINGS") || s.contains("BOOTS"))
			return true;
		return false;
	}

	public static ItemStack enchantItem(ItemStack item, int mod) {
		ItemStack clone = item.clone();
		int numEnchants = (int) (Math.random() * 2);
		ArrayList<Enchantment> enchs = new ArrayList<Enchantment>();
		if (isArmor(item.getType())) {
			enchs.addAll(getArmorEnchants());
		} else {
			enchs.addAll(getToolEnchants());
		}
		int attempts = 0;
		if (!item.getType().toString().toUpperCase().contains("HOE")) {
			while (numEnchants > 0) {
				attempts++;
				if (attempts > 10) {
					break;
				}
				try {
					int rnd = (int) (Math.random() * enchs.size());
					if (enchs.get(rnd).canEnchantItem(item)) {

						numEnchants--;
						clone.addUnsafeEnchantment(enchs.get(rnd),
								(int) Math.max(1, enchs.get(rnd).getMaxLevel() - Math.random() * (3 - mod)));

					}
				} catch (Exception er) {
					er.printStackTrace();
				}
			}
		}
		return clone;
	}

	public static ArrayList<Enchantment> getArmorEnchants() {
		ArrayList<Enchantment> enchantments = new ArrayList<Enchantment>();
		enchantments.addAll(Arrays.asList(Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.PROTECTION_EXPLOSIONS,
				Enchantment.PROTECTION_FALL, Enchantment.PROTECTION_FIRE, Enchantment.PROTECTION_PROJECTILE));
		if (Math.random() * 4 == 1) {
			enchantments.add(Enchantment.MENDING);
		}
		return enchantments;
	}

	public static ArrayList<Enchantment> getToolEnchants() {
		ArrayList<Enchantment> enchantments = new ArrayList<Enchantment>();
		enchantments.addAll(Arrays.asList(Enchantment.DAMAGE_ALL, Enchantment.DAMAGE_UNDEAD, Enchantment.FIRE_ASPECT));
		if (Math.random() * 4 == 1) {
			enchantments.add(Enchantment.MENDING);
		}
		return enchantments;
	}

	public static final ArrayList<String> CHANCES_LORE = new ArrayList<String>(Arrays.asList(
			ChatColor.GOLD + " • " + LEGENDARY_CHANCE + "% Legendary", "",
			ChatColor.DARK_PURPLE + " • " + EPIC_CHANCE + "% Epic", "", ChatColor.BLUE + " • " + RARE_CHANCE + "% Rare",
			"", ChatColor.GRAY + " • " + COMMON_CHANCE + "% Common"));

	public static final String RANDOM_TAG = ChatColor.WHITE + "Random ";
	public static final String RANDOM_ARMOR_TAG = RANDOM_TAG + "Armor";

	public static ItemStack getRandomArmorIndicator() {
		ItemStack item = new ItemStack(Material.IRON_CHESTPLATE);
		item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(RANDOM_ARMOR_TAG);
		meta.setLocalizedName(RANDOM_ARMOR_TAG);
		meta.setLore(CHANCES_LORE);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(meta);

		return item;
	}

	public static final String RANDOM_TOOL_TAG = RANDOM_TAG + "Weapon";

	public static ItemStack getRandomToolIndicator() {
		ItemStack item = new ItemStack(Material.IRON_SWORD);
		item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(RANDOM_TOOL_TAG);
		meta.setLocalizedName(RANDOM_TOOL_TAG);
		meta.setLore(CHANCES_LORE);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(meta);
		return item;
	}

	public static final String RANDOM_SEGMENT_TAG = RANDOM_TAG + "Segment";

	public static ItemStack getRandomSegmentIndicator() {

		ItemStack item = EventSegment.getUpdatedItem(new int[2], 0);
		try {
			item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(RANDOM_SEGMENT_TAG);
			meta.setLocalizedName(RANDOM_SEGMENT_TAG);
			meta.setLore(CHANCES_LORE);
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			item.setItemMeta(meta);
		} catch (Exception er) {
			er.printStackTrace();
		}
		return item;
	}

	public static ItemStack getRandomSegment() {
		return getRandomSegment(randomRarityInt());
	}

	public static ItemStack getRandomSegment(int rarity) {
		ArrayList<ItemStack> segs = getAllSegmentsOfRarity(rarity);
		return segs.get((int) (Math.random() * segs.size()));
	}

	public static ArrayList<ItemStack> getAllSegmentsOfRarity(int rarity) {
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		try {
			for (Class<EventSegment> seg : EventSegment.ALL_SEGMENTS) {
				if (seg.getField("RARITY").get(null).equals(rarity)) {
					items.add((ItemStack) seg.getMethod("getUpdatedItem", int[].class, int.class).invoke(null,
							new int[2], 0));
				}
			}
			if (rarity == 3) {
				items.add(OtherItems.quadrupleUpgrade());
			} else if (rarity == 2) {
				items.add(OtherItems.doubleUpgrade());
			} else if (rarity == 1) {
				items.add(OtherItems.singleUpgrade());
			}
		} catch (Exception er) {
			er.printStackTrace();
		}
		return items;
	}

	public static ArrayList<Class<EventSegment>> getProjectileSegmentsOfRarity(int rarity) {
		ArrayList<Class<EventSegment>> items = new ArrayList<Class<EventSegment>>();
		try {
			for (Class<EventSegment> seg : EventSegment.ALL_SEGMENTS) {
				if ((boolean) seg.getField("IS_PROJECTILE").get(null)
						&& seg.getField("RARITY").get(null).equals(rarity)) {
					items.add(seg);
				}
			}
		} catch (Exception er) {
			er.printStackTrace();
		}
		return items;
	}

	public static ArrayList<Class<EventSegment>> getResultSegmentsOfRarity(int rarity) {
		ArrayList<Class<EventSegment>> items = new ArrayList<Class<EventSegment>>();
		try {
			for (Class<EventSegment> seg : EventSegment.ALL_RESULT_SEGMENTS) {
				if (seg.getField("RARITY").get(null).equals(rarity)) {
					items.add(seg);
				}
			}
		} catch (Exception er) {
			er.printStackTrace();
		}
		return items;
	}

	public static ArrayList<Class<EventSegment>> getModifierSegmentsOfRarity(int rarity) {
		ArrayList<Class<EventSegment>> items = new ArrayList<Class<EventSegment>>();
		try {
			for (Class<EventSegment> seg : EventSegment.ALL_MODIFIER_SEGMENTS) {
				if (seg.getField("RARITY").get(null).equals(rarity)) {
					items.add(seg);
				}
			}
		} catch (Exception er) {
			er.printStackTrace();
		}
		return items;
	}

	public static ItemStack getRandomItem() {
		return getRandomItem(randomRarityInt());
	}

	public static ItemStack getRandomItem(int rarity) {
		if (Math.random() * 3 == 1) {
			return getRandomTool(rarity);
		}
		return getRandomArmor(rarity);
	}

	public static ItemStack getRandomArmor() {
		return getRandomArmor(randomRarityInt());
	}

	public static ItemStack getRandomTool() {
		return getRandomTool(randomRarityInt());
	}

	public static ItemStack getRandomTool(int rarity) {
		return getRandomTool(rarity, Math.random() >= 0.5);
	}

	public static ItemStack getRandomTool(int rarity, boolean sword) {
		ArrayList<Material> list = new ArrayList<Material>();
		if (rarity == 0) {
			if (sword)
				list.add(Material.WOODEN_SWORD);
			else
				list.add(Material.WOODEN_HOE);
		}
		if (rarity == 1) {
			if (sword)
				list.add(Material.IRON_SWORD);
			else
				list.add(Material.IRON_HOE);
		}
		if (rarity > 1) {
			if (sword)
				list.add(Material.DIAMOND_SWORD);
			else
				list.add(Material.DIAMOND_HOE);
		}
		return getRandomItem(list, rarity);

	}

	public static enum Rarity {
		COMMON("Common", ChatColor.GRAY + "Common"), RARE("Rare", ChatColor.BLUE + "Rare"),
		EPIC("Epic", ChatColor.LIGHT_PURPLE + "Epic"), LEGENDARY("Legendary", ChatColor.GOLD + "Legendary"),
		UNIQUE("Unique", ChatColor.RED + "Unique");

		public String text;
		private String inGameText;

		private Rarity(String text, String inGameText) {
			this.text = text;
			this.inGameText = inGameText;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public String getInGameText() {
			return inGameText;
		}

		public void setInGameText(String inGameText) {
			this.inGameText = inGameText;
		}
	}

	public static ItemStack getRandomArmor(int rarity) {
		ArrayList<Material> list = new ArrayList<Material>();
		if (rarity == 0) {
			list.addAll(Arrays.asList(Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE,
					Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS));
		}
		if (rarity == 1) {
			list.addAll(Arrays.asList(Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS,
					Material.IRON_BOOTS));
		}
		if (rarity > 1) {
			list.addAll(Arrays.asList(Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS,
					Material.DIAMOND_BOOTS));
		}
		return getRandomItem(list, rarity);
	}

	public static ItemStack getRandomItem(ArrayList<Material> matTypes, int rarity) {
		return getRandomItem(new ItemStack(matTypes.get((int) (matTypes.size() * Math.random()))), null, rarity);
	}

	public static ItemStack getRandomItem(ItemStack item, String localName, int rarity, ArrayList<String> comment) {
		return getSpellItem(item, localName, getRandomTraits(rarity, item.getType()), rarity, comment);
	}

	public static ItemStack getRandomItem(ItemStack item, String localName, int rarity) {
		return getSpellItem(item, localName, getRandomTraits(rarity, item.getType()), rarity, new ArrayList<String>());
	}

	public static ItemStack getSpellItem(ItemStack item, String localName, HashMap<String, Double> traitList,
			int rarity) {
		return getSpellItem(item, localName, traitList, rarity, new ArrayList<String>());
	}

	public static ItemStack getSpellItem(ItemStack item, String localName, HashMap<String, Double> traitList,
			int rarity, ArrayList<String> comment) {
		try {
			String traits = "";
			ItemMeta meta = item.getItemMeta();

			if (localName == null) {
				if (item.getType().toString().contains("HOE")) {
					meta.setDisplayName(ChatColor.WHITE
							+ getInGameName(item.getType()).substring(0, getInGameName(item.getType()).indexOf("Hoe"))
							+ "Wand");
				}
			} else {
				meta.setDisplayName(localName);
			}

			ArrayList<String> lore = new ArrayList<String>();
			if (meta.getLore() != null) {
				lore.addAll(meta.getLore());
			}
			lore.add(Rarity.values()[rarity].getInGameText());
			lore.addAll(comment);
			lore.add("");
			for (Map.Entry<String, Double> ent : traitList.entrySet()) {
				traits += "[" + ent.getKey() + "]" + "[" + ent.getValue() + "]";
				if (ent.getKey().equals(ItemTrait.ITEM_MANA_COST.name)) {
					lore.add(ChatColor.BLUE + "" + ent.getValue().intValue() + "% " + ent.getKey());
				} else if (ent.getValue() > 0)
					lore.add(ChatColor.BLUE + "+" + ent.getValue().intValue() + "% " + ent.getKey());
				else
					lore.add(ChatColor.RED + "" + ent.getValue().intValue() + "% " + ent.getKey());
			}

			meta.setLore(lore);
			String armorTag = "";
			if (isArmor(item.getType())) {
				armorTag = EventItemStorage.IS_ARMOR_TAG;
			}
//			if (item.getType().getMaxDurability() > 0) {
			meta.setLocalizedName(SPELL_ITEM_TAG + Rarity.values()[rarity].text + armorTag + "[" + localName + "]" + "["
					+ getRandom4Digits() + "]" + traits);
//			} else
//				meta.setLocalizedName(traits);
			item.setItemMeta(meta);
		} catch (Exception er) {
			er.printStackTrace();
		}
		return item;
	}

	public static String removeTraitsFromName(String localName) {
		int index = 0;
		index = localName.indexOf(']', localName.indexOf(']') + 1);
		if (index == -1)
			return localName;
		return localName.substring(0, index + 1);
	}

	public static String getInGameName(Material m) {
		return StringUtils.capitaliseAllWords(StringUtils.replace(m.toString().toLowerCase(), "_", " "));
	}

	public static String getRandom4Digits() {
		return RandomStringUtils.random(4, true, true);
	}

	public static HashMap<String, Double> getRandomTraits(int rarity, Material type) {
		double min = 0.5;
		if (rarity == 3)
			min = 0.75;
		if (rarity == 0 || rarity == 2)
			min = 0;
		while (true) {
			boolean doubledUp = false;
			HashMap<String, Double> map = new HashMap<String, Double>();
			ItemTrait t1 = ItemTrait.WILD_TRAITS.get((int) ((ItemTrait.WILD_TRAITS.size()) * Math.random()));
			if (rarity >= 2) {
				ItemTrait t2 = ItemTrait.WILD_TRAITS.get((int) ((ItemTrait.WILD_TRAITS.size()) * Math.random()));
				if (t1.equals(t2)) {
					int num = (int) (Math.random() * t2.range * 2);
					num = (int) Math.max(num, (min + 1) * t2.range) / 5 * 5;
					map.put(t2.name, (double) num);
					map.put(t1.name, (double) num);
					doubledUp = true;
				} else {
					int num = (int) (Math.random() * t2.range);
					num = (int) Math.max(num, min * t2.range) / 5 * 5;
					map.put(t2.name, (double) Math.max(num, 5));
				}
			}
			if (!doubledUp) {
				int num = (int) (Math.random() * t1.range);
				num = (int) Math.max(Math.max(num, 5), min * t1.range) / 5 * 5;
				num = (int) Math.min((min + 0.5) * t1.range, num);
				map.put(t1.name, (double) Math.max(num, 5));
			}
			boolean goOn = false;
			for (Entry<String, Double> ent : map.entrySet()) {
				if (type.toString().contains("HOE")) {
					if (ent.getKey().contains(ItemTrait.LIFESTEAL.name)) {
						goOn = true;
						break;
					}
				}
				if (type.toString().contains("HOE") || type.toString().contains("SWORD")) {
					if (ent.getKey().contains(ItemTrait.MAX_HEALTH.name)
							|| ent.getKey().contains(ItemTrait.MAX_MANA.name)) {
						goOn = true;
						break;
					}
				}
			}
			if (goOn)
				continue;
			return map;
		}
	}

	public static int doesCrit(LivingEntity ent, double chance) {
		int num = 0;
		while (chance > 0) {
			if (Math.random() * 100 < chance) {
//				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(SpellProcess.plugin, new Runnable() {
//					public void run() {
//						ent.getWorld().playSound(ent.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_SHOOT, 0.5F, 2.0F);
//					}
//				}, num);
				num++;
			}
			chance -= 100;
		}

		return num;
	}

	public static double getSpecificTrait(HashMap<ItemTrait, Double> map, ItemTrait trait) {
		if (map.get(trait) == null) {
			return 0;
		}
		return map.get(trait);
	}

	public static double cdrCalculation(double input) {
		return Math.floor(Math.round((1 - (1 / (1.0 + (input / 60.0)))) * 10000)) / 100.0;
	}

	public static double getTrait(LivingEntity ent, ItemTrait trait) {
		Double val = getAllTraits(ent).get(trait);
		if (val != null)
			return val;
		return 0;
	}

	public static HashMap<ItemTrait, Double> getAllTraits(LivingEntity ent) {
		HashMap<ItemTrait, Double> traits = new HashMap<ItemTrait, Double>();
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		for (ItemStack item : ent.getEquipment().getArmorContents()) {
			if (item != null) {
				items.add(item);
			}
		}
		if (ent.getEquipment().getItemInMainHand() != null) {
			items.add(ent.getEquipment().getItemInMainHand());
		}
		for (ItemStack item : items) {
			if (item != null && item.hasItemMeta()) {
				for (Map.Entry<ItemTrait, Double> map : getItemTraits(item).entrySet()) {
					if (traits.get(map.getKey()) != null) {
						traits.put(map.getKey(), map.getValue() + traits.get(map.getKey()));
					} else
						traits.put(map.getKey(), map.getValue());
				}
			}
		}
		return traits;
	}

	public static HashMap<ItemTrait, Double> getItemTraits(ItemStack item) {
		HashMap<ItemTrait, Double> map = new HashMap<ItemTrait, Double>();
		try {
			String localName = item.getItemMeta().getLocalizedName();
			int lastIndex = 0;
			ItemTrait lastTrait = null;
			for (int i = 0; i < localName.length(); i++) {
				if (localName.charAt(i) == '[') {
					lastIndex = i;
				} else if (localName.charAt(i) == ']') {
					String s = localName.substring(lastIndex + 1, i);

					for (ItemTrait t : ItemTrait.values()) {
						if (t.name.equals(s)) {
							lastTrait = t;
						}
					}

					if (lastTrait != null) {
						try {
							double stat = Double.parseDouble(s);

							if (map.get(lastTrait) != null) {
								map.put(lastTrait, map.get(lastTrait) + stat);
							} else
								map.put(lastTrait, stat);
							lastTrait = null;
						} catch (Exception er) {
							// This should fire when the first string in brackets is NOT a trait
						}
					}
				}
			}

		} catch (Exception er) {
			er.printStackTrace();
		}
		return map;
	}
}
