package me.BerylliumOranges.spellevent.entity_information.spellprefabs;

import java.awt.Color;
import java.util.ArrayList;

import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import me.BerylliumOranges.main.SpellPluginMain;
import me.BerylliumOranges.misc.DirectoryTools;
import me.BerylliumOranges.misc.GachaItems;
import net.md_5.bungee.api.ChatColor;

public abstract class AbstractSpellItem {
	public static final String NAME = "Spell Item";
	public static final int RARITY = 0;
	public static final boolean PLAYER_OBTAINABLE = true;
	// I know these booleans are stupid, don't judge me
	public static final boolean SWORD = false;
	public static final boolean ARMOR = false;
	public static final boolean SCROLL = false;
	public static final String EVENT_ITEM_TEXT = "EventItem";

	public static enum ItemRarity {
		COMMON(EVENT_ITEM_TEXT + GachaItems.Rarity.COMMON.text, GachaItems.Rarity.COMMON),
		RARE(EVENT_ITEM_TEXT + GachaItems.Rarity.RARE.text, GachaItems.Rarity.RARE),
		EPIC(EVENT_ITEM_TEXT + GachaItems.Rarity.EPIC.text, GachaItems.Rarity.EPIC),
		LEGENDARY(EVENT_ITEM_TEXT + GachaItems.Rarity.LEGENDARY.text, GachaItems.Rarity.LEGENDARY),
		UNIQUE(EVENT_ITEM_TEXT + GachaItems.Rarity.UNIQUE.text, GachaItems.Rarity.UNIQUE);

		private String text;
		private GachaItems.Rarity rarity;

		private ItemRarity(String s, GachaItems.Rarity rarity) {
			text = s;
			this.rarity = rarity;
		}

		public String getText() {
			return text;
		}

		public GachaItems.Rarity getRarity() {
			return rarity;
		}

		public void setRarity(GachaItems.Rarity rarity) {
			this.rarity = rarity;
		}

	}

	public static String colorTextFade(String text, Color[] colors) {
		if (colors.length == 0)
			return text;
		String temp = "";
		for (int i = 0; i < text.length(); i++) {
			int index = ((i * (colors.length - 1)) / text.length());
			Color color1 = colors[index % colors.length];
			Color color2 = colors[(index + 1) % colors.length];
			double charsAllowed;

			if (colors.length == 1)
				charsAllowed = text.length();
			else
				charsAllowed = (text.length() / (colors.length - 1));

			if (text.length() % 2 == 1)
				charsAllowed += 1;

			double am = (i % (charsAllowed)) / (charsAllowed - 1);
			temp += ChatColor.of(
					new Color((int) ((am * color2.getRed()) + ((1 - am) * color1.getRed())),
					((int) ((am * color2.getGreen()) + ((1 - am) * color1.getGreen()))),
					((int) ((am * color2.getBlue()) + ((1 - am) * color1.getBlue()))))) + "" + text.charAt(i);
		}
		return temp;
	}

	public static Class<AbstractSpellItem> getSpellItemClass(ItemStack item) {
		if (item == null || !item.hasItemMeta())
			return null;
		return getSpellItemClass(item.getItemMeta().getLocalizedName());
	}

	public static Class<AbstractSpellItem> getSpellItemClass(String localName) {
		String name = ChatColor.stripColor(SpellPluginMain.getStringInBrackets(localName, 0));
		for (Class<AbstractSpellItem> spell : AbstractSpellItem.ALL_SPELL_PREFABS) {
			try {
				if (ChatColor.stripColor("" + spell.getField("NAME").get(null))
						.equals(name)) {
					return spell;
				}
			} catch (Exception er) {
				er.printStackTrace();
			}
		}
		return null;
	}

	public static boolean meetsRequirements(LivingEntity caster) {
		return true;
	}

	/*
	 * 
	 * public static String colorTextFade(String text, Color[] colors) { if
	 * (colors.length == 0) return text;
	 * Bukkit.broadcastMessage("new armor, text is " + text); String temp = ""; int
	 * t = 0; for (int i = 0; i < text.length(); i++) { int index = ((i *
	 * (colors.length - 1)) / text.length()); Color color1 = colors[index %
	 * colors.length]; Color color2 = colors[(index + 1) % colors.length];
	 * 
	 * Bukkit.broadcastMessage( "color1 is " + ((index) % colors.length) +
	 * ", color2 is " + ((index + 1) % colors.length));
	 * 
	 * int n = index % 2 == 0 ? 1 : -1; t += n; double am = Math.min(1, (t *
	 * (colors.length - 1)) / (double) text.length()); temp += ChatColor .of(new
	 * Color(color1.getRed() + (int) (am * (color2.getRed() - color1.getRed())),
	 * color1.getGreen() + (int) (am * (color2.getGreen() - color1.getGreen())),
	 * color1.getBlue() + (int) (am * (color2.getBlue() - color1.getBlue())))) + ""
	 * + text.charAt(i); if (i % (text.length() / colors.length) == 0) t = 0; }
	 * return temp; }
	 * 
	 */

	public final static ArrayList<Class<AbstractSpellItem>> ALL_SPELL_PREFABS = loadSpellPrefabClasses();
//	public final static ArrayList<ItemStack> ALL_SPELL_PREFABS_CONTAINERS = loadSpellPrefabItems();

	public static ItemStack getContainer() {
		return null;
	}

	public static ItemStack[][] getSegments() {
		return null;
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<Class<AbstractSpellItem>> loadSpellPrefabClasses() {
		ArrayList<Class<AbstractSpellItem>> classesList = new ArrayList<Class<AbstractSpellItem>>();

		String path = "me.BerylliumOranges.spellevent.entity_information.spellprefabs";
		for (Class<?> clazz : DirectoryTools.getClasses(path)) {
			if (AbstractSpellItem.class.isAssignableFrom(clazz))
				classesList.add((Class<AbstractSpellItem>) clazz);
		}
		return classesList;
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<ItemStack> loadSpellPrefabItems() {
		ArrayList<ItemStack> classesList = new ArrayList<ItemStack>();
		String path = "me.BerylliumOranges.spellevent.entity_information.spellprefabs";
		for (Class<?> clazz : DirectoryTools.getClasses(path)) {
			if (AbstractSpellItem.class.isAssignableFrom(clazz)) {
				try {
					classesList.add((ItemStack) clazz.getMethod("getContainer").invoke(null));
				} catch (Exception er) {

				}
			}
		}
		return classesList;
	}

}
