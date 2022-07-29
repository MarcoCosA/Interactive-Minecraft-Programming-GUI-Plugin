package me.BerylliumOranges.spellevent.entity_information.entityprefabs.ads;

import java.awt.Color;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Endermite;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.BerylliumOranges.spellevent.entity_information.EntityStats;
import me.BerylliumOranges.spellevent.entity_information.EntityStats.SpellThread;
import me.BerylliumOranges.spellevent.entity_information.spellprefabs.wands.FlashbangWand;
import me.BerylliumOranges.spellevent.entity_information.spellprefabs.wands.SpitCobweb;
import net.md_5.bungee.api.ChatColor;

public class Steve extends AbstractAds {
	public static final String NAME = "Steve";
	public static final String AD_NAME = ChatColor.of(new Color(220, 163, 59)) + NAME;
	public static final ItemStack ITEM = loadSpawnEgg();
	public static final double MAX_HEALTH = 20;

	public static final ArrayList<ItemStack> SPELLS = new ArrayList<>();

	private static ItemStack loadSpawnEgg() {

		ItemStack item = new ItemStack(Material.HORSE_SPAWN_EGG);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(AD_NAME);
		meta.setLocalizedName(ENTITY_PREFAB_TAG + "[" + NAME + "]");
		item.setItemMeta(meta);
		return item;
	}

	public LivingEntity spawnEntity(Location loc) {
		Zombie liv = loc.getWorld().spawn(loc, Zombie.class);
		liv.setMaxHealth(MAX_HEALTH);
		liv.setSilent(true);
		liv.setHealth(MAX_HEALTH);
		liv.setPersistent(true);
		// liv.setCustomName(ITEM.getItemMeta().getDisplayName());
		liv.setCustomNameVisible(false);
		liv.getEquipment().setHelmet(new ItemStack(Material.PLAYER_HEAD));
		liv.getEquipment().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
		liv.getEquipment().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
		liv.getEquipment().setBoots(new ItemStack(Material.LEATHER_BOOTS));
		entity = liv;

		EntityStats stat = EntityStats.getEntityStats(liv);
		stat.setEntityPrefab(this);
		stat.getSpellThreads().add(new SpellThread());
		stat.getSpellThreads().get(0).getSpells().add(new AbstractMap.SimpleEntry<ItemStack, Integer>(SpitCobweb.getContainer(), 80));
		return liv;
	}

	public static final ArrayList<DamageCause> UNACCEPTABLE_CAUSES = new ArrayList<>(
			Arrays.asList(DamageCause.FIRE, DamageCause.FALL, DamageCause.FIRE_TICK));

	public void triggerDamageEvent(EntityDamageEvent event) {
		entity.getWorld().playSound(((LivingEntity) entity).getEyeLocation(), Sound.ENTITY_PLAYER_HURT, 1, 1);
	}

	@Override
	public int getTier() {
		return 0;
	}
}
