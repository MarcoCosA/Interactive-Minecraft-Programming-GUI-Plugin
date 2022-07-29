package me.BerylliumOranges.spellevent.segments.g_spell_focus;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import me.BerylliumOranges.main.SpellPluginMain;
import me.BerylliumOranges.misc.ConfigInfo;
import me.BerylliumOranges.misc.GachaItems;
import me.BerylliumOranges.spellevent.entity_information.EntityStats;
import me.BerylliumOranges.spellevent.other.Guideline;
import me.BerylliumOranges.spellevent.other.OtherItems;
import me.BerylliumOranges.spellevent.processes.EventItemStorage;
import me.BerylliumOranges.spellevent.processes.SegmentProcess;
import me.BerylliumOranges.spellevent.processes.SpellProcess;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;

public class Hijack extends EventSegment {
	public static final String THIS_SEGMENT_TYPE = SEGMENT_TYPE_RESULT;
	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 225;
	public static final int RARITY = 3;
	public static ArrayList<HijackedItem> hijackedSpells = new ArrayList<>();
	public static final String HIJACK_TAG = ChatColor.LIGHT_PURPLE + "Hijacked";

	public static Guideline processSegment(SegmentProcess proc) {
		int[] upgrades = EventSegment.getUpgrades(proc);

		if (!proc.getFocusedSpellsCopy().isEmpty() && proc.getFocusedSpellsCopy().get(0) != null && proc.getCaster() instanceof Player) {
			if (proc.subtractMana(MANA_COST) < 1)
				return new Guideline(proc, true, false);
			Player p = (Player) proc.getCaster();
			if (ConfigInfo.isModify() || p.isOp()) {

				ItemStack container = proc.getFocusedSpellsCopy().get(0).getContainer();
				ItemMeta conMeta = container.getItemMeta();
				Inventory temp = EventItemStorage.getItemInventory(container);

				String localName = SpellPluginMain.replaceStringInBrackets(conMeta.getLocalizedName(), HIJACK_TAG, 0);
				localName = SpellPluginMain.replaceStringInBrackets(localName, GachaItems.getRandom4Digits(), 1);
				Inventory inv = Bukkit.createInventory(p, temp.getSize(), EventItemStorage.WAND_INVENTORY_TITLE + localName);
				for (int i = 0; i < temp.getSize(); i++) {
					ItemStack item = temp.getItem(i);
					if (item != null && item.hasItemMeta()) {
						ItemMeta meta = item.getItemMeta();
						if (meta.getLocalizedName().startsWith(OtherItems.lockedIndicator().getItemMeta().getLocalizedName()))
							continue;
						meta.setLocalizedName(EventSegment.changeEditOption(meta.getLocalizedName(), EditOption.ROTATABLE));
						item.setItemMeta(meta);
						inv.setItem(i, item);
					}
				}

				InventoryView view = p.openInventory(inv);
				HijackedItem h = new HijackedItem(proc, container, localName, view.getTitle(), getDuration(upgrades) * 20);
				h.setMaxManaUse(getManaUse(upgrades));
			}
		}
		return new Guideline(proc, true, false);
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "Hijack");
		meta.setLore(Arrays.asList(ChatColor.WHITE + "Open the inventory of the first",
				ChatColor.LIGHT_PURPLE + "focused " + ChatColor.WHITE + "spell. Changes made last ",
				ChatColor.WHITE + "for " + ChatColor.LIGHT_PURPLE + getDuration(upgrades) + ChatColor.WHITE + " seconds. The new spell",
				ChatColor.WHITE + "can use a maximum of " + ChatColor.LIGHT_PURPLE + getManaUse(upgrades) + ChatColor.WHITE + " mana", "",
				getCostText(MANA_COST)));

		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_SPELL_FOCUS_RESULT);
		item.setItemMeta(meta);
		return item;
	}

	public static int getDuration(int[] upgrades) {
		return 15 + (upgrades[0] + upgrades[1]) * 2;
	}

	public static int getManaUse(int[] upgrades) {
		return 100 + (upgrades[0] + upgrades[1]) * 15;
	}

	public static class HijackedItem implements Listener {

		@EventHandler
		public void onCloseInventory(InventoryCloseEvent e) {
			for (int i = unregisteredHijackedSpells.size() - 1; i >= 0; i--) {
				HijackedItem spell = unregisteredHijackedSpells.get(i);
				if (e.getView().getTitle().equals(spell.getInventory())) {
					spell.setInventoryClosed(true);
					hijackedSpells.add(spell);
					unregisteredHijackedSpells.remove(spell);
					EventSegment
							.alertCaster(spell.getCaster(), ITEM,
									ChatColor.WHITE + "Hijacked \"" + getAlertName(spell.getItem()) + ChatColor.RESET + "\" for the next "
											+ EntityStats.calculateTime(spell.getHijackDuration() / 20, false, ChatColor.YELLOW, ChatColor.WHITE),
									false);
				}
			}
		}

		private ItemStack item;
		private String localName;
		private String inventory;
		private int maxManaUse = 100;
		private LivingEntity caster;
		private boolean closed = false;
		private int hijackDuration;
		private static boolean registered = false;
		public static ArrayList<HijackedItem> unregisteredHijackedSpells = new ArrayList<>();

		public HijackedItem(SegmentProcess proc, ItemStack container, String hijackedSpellName, String inventoryName, int duration) {
			item = container;
			localName = hijackedSpellName;
			inventory = inventoryName;
			caster = proc.getCaster();
			hijackDuration = duration;
			HijackedItem instance = this;
			unregisteredHijackedSpells.add(this);
			if (!registered) {
				SpellPluginMain.getInstance().getServer().getPluginManager().registerEvents(this, SpellPluginMain.getInstance());
				registered = true;
			}
			BukkitTask task = new BukkitRunnable() {
				int time = duration;

				public void run() {
					if (time > 0) {
						if (closed) {
							time--;
						}
					} else
						this.cancel();
				}

				@Override
				public void cancel() {
					if (proc.getCaster() instanceof Player) {
						Player p = (Player) proc.getCaster();
						Inventory inv = EventItemStorage.getItemInventory(localName, null);
						for (int i = 0; i < inv.getSize(); i++) {
							ItemStack item = inv.getItem(i);
							if (item != null && item.hasItemMeta()) {
								ItemMeta meta = item.getItemMeta();
								if (meta.getLocalizedName().startsWith(OtherItems.lockedIndicator().getItemMeta().getLocalizedName())
										|| meta.getLocalizedName().startsWith(OtherItems.equipIndicator().getItemMeta().getLocalizedName())
										|| meta.getLocalizedName().startsWith(OtherItems.triggerIndicator().getItemMeta().getLocalizedName()))
									continue;
								if (EventSegment.getEditOption(meta).equals(EditOption.FULLY)) {
									if (p.getInventory().first(item) >= 0 || p.getInventory().firstEmpty() >= 0) {
										p.getInventory().addItem(item);
									} else {
										p.getWorld().dropItem(p.getLocation(), item);
									}
								}
							}
						}
						EventSegment.alertCaster(p, ITEM,
								"hijack " + ChatColor.RED + "expired " + ChatColor.WHITE + "on \"" + getAlertName(item) + ChatColor.RESET + "\"",
								false);
						p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ARMOR_STAND_BREAK, 1F, 2F);
					}
					hijackedSpells.remove(instance);
					super.cancel();
				}

			}.runTaskTimer(SpellProcess.plugin, 0, 0);
			proc.getSpellSlot().getTasks().add(task);
		}

		public ItemStack getItem() {
			return item;
		}

		public void setItem(ItemStack item) {
			this.item = item;
		}

		public String getLocalName() {
			return localName;
		}

		public void setLocalName(String localName) {
			this.localName = localName;
		}

		public int getMaxManaUse() {
			return maxManaUse;
		}

		public void setMaxManaUse(int maxManaUse) {
			this.maxManaUse = maxManaUse;
		}

		public String getInventory() {
			return inventory;
		}

		public void setInventory(String inventory) {
			this.inventory = inventory;
		}

		public boolean isInventoryClosed() {
			return closed;
		}

		public void setInventoryClosed(boolean closed) {
			this.closed = closed;
		}

		public LivingEntity getCaster() {
			return caster;
		}

		public void setCaster(LivingEntity caster) {
			this.caster = caster;
		}

		public int getHijackDuration() {
			return hijackDuration;
		}

		public void setHijackDuration(int hijackDuration) {
			this.hijackDuration = hijackDuration;
		}
	}
}
