package me.BerylliumOranges.main;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.BerylliumOranges.listeners.PlayerInventoryClickListener;
import me.BerylliumOranges.misc.GachaItems;
import me.BerylliumOranges.spellevent.entity_information.EntityStats;
import me.BerylliumOranges.spellevent.other.custom_events.EntityGroundPoundEvent;

public class EveryTick {
	public static int ticks = 0;
	public static final int TICKS_MAX = 1800;
	public static HashMap<Entity, Float> entityFallAmounts = new HashMap<Entity, Float>();

	public static ArrayList<Entity> allEntities = new ArrayList<Entity>();

	public static void tick() {
		allEntities.clear();

		for (World w : Bukkit.getWorlds()) {
			for (Entity ent : w.getEntities()) {
				allEntities.add(ent);
				if (ent instanceof LivingEntity) {
					LivingEntity liv = (LivingEntity) ent;
					Float f = entityFallAmounts.get(liv);
					if (liv.isOnGround()) {
						if (f != null && f - liv.getFallDistance() > 4) {
							EntityGroundPoundEvent event = new EntityGroundPoundEvent(liv, f - liv.getFallDistance());
							Bukkit.getServer().getPluginManager().callEvent(event);
						}
					}
					float fall = liv.getFallDistance();
					if (liv instanceof IronGolem)
						fall *= 1.8;
					entityFallAmounts.put(liv, fall);

					// EntityStats stats = EntityStats.getEntityStats(liv);
					// if (stats.getEntityPrefab() != null) {
					// liv.setTicksLived(1);
					// }

				}

			}
		}

		ticks++;
		if (ticks > TICKS_MAX)
			ticks = 0;

		if (ticks % 4 == 0) {
			for (int i = PlayerInventoryClickListener.rouletteItems.size() - 1; i >= 0; i--) {
				Item itemDrop = PlayerInventoryClickListener.rouletteItems.get(i);
				if (itemDrop == null || itemDrop.getItemStack() == null || itemDrop.getItemStack().getType().isAir()) {
					PlayerInventoryClickListener.rouletteItems.remove(i);
					continue;
				}
				ItemStack item = itemDrop.getItemStack();
				ItemStack toDrop = null;
				if (item.getItemMeta().getLocalizedName().startsWith(GachaItems.RANDOM_ARMOR_TAG)) {
					toDrop = GachaItems.getRandomArmor();
					ItemMeta meta = toDrop.getItemMeta();
					meta.setLocalizedName(item.getItemMeta().getLocalizedName());
					toDrop.setItemMeta(meta);
				} else if (item.getItemMeta().getLocalizedName().startsWith(GachaItems.RANDOM_TOOL_TAG)) {
					toDrop = GachaItems.getRandomTool();
					ItemMeta meta = toDrop.getItemMeta();
					meta.setLocalizedName(item.getItemMeta().getLocalizedName());
					toDrop.setItemMeta(meta);
				} else if (item.getItemMeta().getLocalizedName().startsWith(GachaItems.RANDOM_SEGMENT_TAG)) {
					toDrop = GachaItems.getRandomSegment();
					ItemMeta meta = toDrop.getItemMeta();
					meta.setLocalizedName(item.getItemMeta().getLocalizedName());
					toDrop.setItemMeta(meta);
				}
				itemDrop.setItemStack(toDrop);
				if (ticks % 20 == 0) {
					Location loc = itemDrop.getLocation();
					itemDrop.getWorld().spawnParticle(Particle.END_ROD, loc.getX(), loc.getY() + 0.3, loc.getZ(), 0, (Math.random() * 0.2) - 0.1,
							(Math.random() * 0.2), (Math.random() * 0.2) - 0.1);

				}
			}
		}

		EntityStats.tickEntityStats();
	}
}
