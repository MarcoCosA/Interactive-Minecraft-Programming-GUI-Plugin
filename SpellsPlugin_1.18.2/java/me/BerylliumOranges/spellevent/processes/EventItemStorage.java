package me.BerylliumOranges.spellevent.processes;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import me.BerylliumOranges.main.SpellPluginMain;
import me.BerylliumOranges.misc.Directories;
import me.BerylliumOranges.misc.GachaItems;
import me.BerylliumOranges.misc.GachaItems.Rarity;
import me.BerylliumOranges.spellevent.entity_information.spellprefabs.AbstractSpellItem;
import me.BerylliumOranges.spellevent.other.OtherItems;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;

public class EventItemStorage implements Serializable {
	// This class just stores all the segments in the swords/wands to a file
	private static final long serialVersionUID = 1024870292135908480L;
	public final HashMap<Integer, ItemStack> itemSnapshot;
	public final static String separator = System.lineSeparator();
	public final static String MOB_INVENTORY_TAG = "MobInventory";
	public final static String MOB_ITEM_TAG = "MobItem";
	public static final String WAND_INVENTORY_TITLE = ChatColor.GRAY
			+ "Procedure                                                                                                          ";

	// Used for saving
	public EventItemStorage(HashMap<Integer, ItemStack> itemSnapshot) {
		this.itemSnapshot = itemSnapshot;
	}

	// Used for loading
	public EventItemStorage(EventItemStorage loadedData) {
		this.itemSnapshot = loadedData.itemSnapshot;
	}

	public boolean saveData(String filePath) {
		try {
			BukkitObjectOutputStream out = new BukkitObjectOutputStream(new GZIPOutputStream(new FileOutputStream(filePath)));
			out.writeObject(this);
			out.close();
			loadedData.clear();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public static final String IS_ARMOR_TAG = "isArmorTag";

	public static int getRarityOfItem(ItemStack item) {

		if (item == null || !item.hasItemMeta())
			return -1;

		return getRarityOfItem(item.getItemMeta().getLocalizedName());
	}

	public static int getRarityOfItem(String localName) {
		for (int i = 0; i < AbstractSpellItem.ItemRarity.values().length; i++) {
			if (localName.startsWith(AbstractSpellItem.ItemRarity.values()[i].getText())) {
				return i;
			}
		}
		return -1;

	}

	public static int getSizeOfLockedPortion(String localName) {
		if (localName.contains(IS_ARMOR_TAG)) {
			return 5;
		} else if (localName.startsWith(AbstractSpellItem.ItemRarity.COMMON.getText())) {
			return 3;
		} else if (localName.startsWith(AbstractSpellItem.ItemRarity.RARE.getText())) {
			return 2;
		} else if (localName.startsWith(AbstractSpellItem.ItemRarity.EPIC.getText())) {
			return 1;
		} else if (localName.startsWith(AbstractSpellItem.ItemRarity.LEGENDARY.getText())
				|| localName.startsWith(AbstractSpellItem.ItemRarity.UNIQUE.getText())) {
			return 0;
		}

		return -1;
	}

	public static ArrayList<EventItemStorage> loadedData = new ArrayList<EventItemStorage>();
	public String localName;

	/**
	 * @param localName
	 * @param tried
	 *            whether or not loadData has already been attemped, should
	 *            always be false
	 * @return
	 */
	public static EventItemStorage loadData(String localName, boolean tried) {
		for (EventItemStorage l : loadedData) {
			if (l.localName.equals(localName)) {
				return l;
			}
		}

		try {
			BukkitObjectInputStream in = new BukkitObjectInputStream(
					new GZIPInputStream(new FileInputStream(Directories.ITEMS_PATH + ChatColor.stripColor(localName) + ".data")));
			EventItemStorage data = (EventItemStorage) in.readObject();
			in.close();

			data.localName = localName;
			loadedData.add(data);

			return data;
		} catch (ClassNotFoundException | IOException e) {
			if (!tried) {
				Inventory inv = Bukkit.createInventory(null, 54, WAND_INVENTORY_TITLE + localName);
				int iSize = getSizeOfLockedPortion(localName);

				Bukkit.broadcastMessage("local name " + localName + ", size " + iSize);
				for (int i = 0; i < iSize; i++) {
					for (int y = 0; y < 6; y++) {
						inv.setItem(i + (y * 9), OtherItems.lockedIndicator());
						inv.setItem((9 - iSize) + i + (y * 9), OtherItems.lockedIndicator());
					}
				}
				String name = ChatColor.stripColor(SpellPluginMain.getStringInBrackets(localName, 0));

				if (!name.equals("null")) {
					try {
						Class<AbstractSpellItem> spell = AbstractSpellItem.getSpellItemClass(localName);
						// Bukkit.broadcastMessage(
						// "Checking " + ChatColor.stripColor("" +
						// spell.getField("NAME").get(null)) + " vs "
						// +
						// ChatColor.stripColor(SpellPluginMain.getStringInBrackets(localName,
						// 0)));
						if (ChatColor.stripColor("" + spell.getField("NAME").get(null)).equals(name)) {
							ItemStack[][] items = (ItemStack[][]) spell.getMethod("getSegments").invoke(null);
							for (int y = 0; y < items.length; y++) {
								for (int x = 0; x < items[0].length; x++) {
									if (items[y][x] != null) {
										ItemStack item = items[y][x].clone();
										ItemMeta meta = item.getItemMeta();
										if (meta != null) {
											String editOption = EventSegment.EditOption.FULLY.getText();
											if (localName.startsWith(AbstractSpellItem.ItemRarity.EPIC.getText())
													|| localName.startsWith(AbstractSpellItem.ItemRarity.LEGENDARY.getText())) {
												editOption = EventSegment.EditOption.ROTATABLE.getText();
											}
											if (localName.startsWith(AbstractSpellItem.ItemRarity.COMMON.getText())
													|| localName.startsWith(AbstractSpellItem.ItemRarity.RARE.getText())
													|| localName.contains(IS_ARMOR_TAG)) {
												editOption = EventSegment.EditOption.NOTHING.getText();
											}
											meta.setLocalizedName(meta.getLocalizedName() + "[" + editOption + "]");
											ArrayList<String> lore = new ArrayList<String>();
											if (item.getType().equals(Material.POTION) || item.getType().equals(Material.SPLASH_POTION))
												lore.add("");
											if (meta.getLore() != null) {
												lore.addAll(meta.getLore());
											}
											lore.addAll(EventSegment.getExtraSegmentLore(meta));
											meta.setLore(lore);
											item.setItemMeta(meta);
										}
										inv.setItem(x + (y * 9), item);
									}
								}
							}
						}
					} catch (Exception er) {
						er.printStackTrace();
						Bukkit.getLogger().info("The segment name was " + localName);
					}
				}
				EventItemStorage.writeStorage(inv, ChatColor.stripColor(localName));
				return loadData(localName, true);
			}
			return null;
		}

	}

	public static boolean isArmor(ItemStack item) {
		if (item == null)
			return false;
		String type = item.getType().toString().toLowerCase();
		if (type.contains("helmet"))
			return true;
		if (type.contains("chestplate"))
			return true;
		if (type.contains("leggings"))
			return true;
		if (type.contains("boots"))
			return true;
		return false;
	}

	public static Inventory getItemInventory(ItemStack item) {
		if (item != null && item.hasItemMeta())
			return getItemInventory(item.getItemMeta().getLocalizedName(), null);
		return Bukkit.createInventory(null, 54, WAND_INVENTORY_TITLE + item.getItemMeta().getLocalizedName());
	}

	public static Inventory getItemInventory(String localName, String title) {
		if (title == null || title.isEmpty()) {
			title = WAND_INVENTORY_TITLE + localName;
		}
		Inventory inv = Bukkit.createInventory(null, 54, title);
		try {
			HashMap<Integer, ItemStack> info = EventItemStorage.loadData(localName, false).getItemSnapshot();
			for (Map.Entry<Integer, ItemStack> entry : info.entrySet()) {
				inv.setItem(entry.getKey(), entry.getValue());
			}
		} catch (Exception error) {
		}
		return inv;
	}

	public static void writeStorage(Inventory inv, String localName) {
		HashMap<Integer, ItemStack> itemSnapshot = new HashMap<Integer, ItemStack>();

		for (int i = 0; i < inv.getSize(); i++) {
			ItemStack item = inv.getItem(i);
			if (item != null) {

				itemSnapshot.put(i, item);
			}
		}

		new EventItemStorage(itemSnapshot).saveData(Directories.ITEMS_PATH + ChatColor.stripColor(localName) + ".data");
	}

	public static void writeStorage(HashMap<Integer, ItemStack> snapshot, String localName) {
		new EventItemStorage(snapshot).saveData(Directories.ITEMS_PATH + ChatColor.stripColor(localName) + ".data");
	}

	public HashMap<Integer, ItemStack> getItemSnapshot() {
		return itemSnapshot;
	}

}
