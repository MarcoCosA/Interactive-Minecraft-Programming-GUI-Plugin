package me.BerylliumOranges.listeners;

import java.util.ArrayList;

import org.bukkit.Effect;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.BerylliumOranges.main.SpellPluginMain;
import me.BerylliumOranges.misc.GachaItems;

public class PlayerInventoryClickListener implements Listener {
	public SpellPluginMain plugin;

	public PlayerInventoryClickListener(SpellPluginMain plugin) {
		this.plugin = plugin;
	}

	public static ArrayList<Item> rouletteItems = new ArrayList<Item>();

	@EventHandler
	public void onDrop(PlayerDropItemEvent e) {
		ItemStack item = e.getItemDrop().getItemStack();
		if (item != null && item.hasItemMeta()) {
			if (item.getItemMeta().getLocalizedName().startsWith(GachaItems.RANDOM_TAG)) {
				ItemStack drop = item.clone();
				ItemMeta meta = drop.getItemMeta();
				meta.setLocalizedName(meta.getLocalizedName() + "[" + e.getPlayer().getName() + "]");
				drop.setItemMeta(meta);
				drop.setAmount(1);
				for (int i = 0; i < item.getAmount(); i++) {
					Item copied = e.getPlayer().getWorld().dropItem(e.getItemDrop().getLocation(), drop);
					copied.setVelocity(e.getItemDrop().getVelocity());
					copied.setPickupDelay(e.getItemDrop().getPickupDelay());
					rouletteItems.add(copied);
				}
				e.getItemDrop().remove();
			}
		}
	}

	@EventHandler
	public void itemCombine(ItemMergeEvent e) {
		if (rouletteItems.contains(e.getEntity())) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPickup(PlayerPickupItemEvent e) {
		ItemStack item = e.getItem().getItemStack();
		if (item != null && item.hasItemMeta()) {
			if (item.getItemMeta().getLocalizedName().startsWith(GachaItems.RANDOM_TAG)) {
				rouletteItems.remove(e.getItem());
				try {
					String name = SpellPluginMain.getStringInBrackets(item.getItemMeta().getLocalizedName(), 0);
					if (!name.equals(e.getPlayer().getName())) {
						e.setCancelled(true);
					}
					int rarity = GachaItems.randomRarityInt();
					boolean found = false;
					if (item.getItemMeta().getLocalizedName().startsWith(GachaItems.RANDOM_ARMOR_TAG)) {
						e.getItem().setItemStack(GachaItems.getRandomArmor(rarity));
						found = true;
					} else if (item.getItemMeta().getLocalizedName().startsWith(GachaItems.RANDOM_TOOL_TAG)) {
						e.getItem().setItemStack(GachaItems.getRandomTool(rarity));
						found = true;
					} else if (item.getItemMeta().getLocalizedName().startsWith(GachaItems.RANDOM_SEGMENT_TAG)) {
						e.getItem().setItemStack(GachaItems.getRandomSegment(rarity));
						found = true;
					}
					if (found) {
						e.getPlayer().playEffect(e.getItem().getLocation(), Effect.FIREWORK_SHOOT, null);
					}
				} catch (Exception er) {

				}
			}
		}
	}
}
