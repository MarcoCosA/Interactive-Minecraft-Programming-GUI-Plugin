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
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import me.BerylliumOranges.spellevent.entity_information.EntityStats;
import me.BerylliumOranges.spellevent.entity_information.EntityStats.SpellThread;
import me.BerylliumOranges.spellevent.entity_information.spellprefabs.wands.FlashbangWand;
import me.BerylliumOranges.spellevent.entity_information.spellprefabs.wands.SpitCobweb;
import net.md_5.bungee.api.ChatColor;

public class Frost extends AbstractAds {
	public static final String NAME = "Frost Zombie";
	public static final String AD_NAME = ChatColor.of(new Color(180, 220, 255)) + NAME;
	public static final ItemStack ITEM = loadSpawnEgg();
	public static final double MAX_HEALTH = 45;

	public static final ArrayList<ItemStack> SPELLS = new ArrayList<>();

	private static ItemStack loadSpawnEgg() {

		ItemStack item = new ItemStack(Material.POLAR_BEAR_SPAWN_EGG);
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
		// liv.setCustomName(ITEM.getItemMeta().getDisplayName());
		liv.setCustomNameVisible(false);
		liv.setPersistent(true);
		ItemStack item = new ItemStack(Material.LEATHER_HELMET);
		LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
		meta.setColor((org.bukkit.Color.fromRGB(180, 220, 255)));
		item.setItemMeta(meta);
		// The prismarine brick stairs look cool too
		liv.getEquipment().setHelmet(new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS));
		item.setType(Material.LEATHER_CHESTPLATE);
		liv.getEquipment().setChestplate(item);
		item.setType(Material.LEATHER_LEGGINGS);
		liv.getEquipment().setLeggings(item);
		item.setType(Material.LEATHER_BOOTS);
		liv.getEquipment().setBoots(item);
		liv.setAdult();
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
		entity.getWorld().playSound(((LivingEntity) entity).getEyeLocation(), Sound.ENTITY_POLAR_BEAR_WARNING, 0.8F, 2F);
	}

	public void triggerDeathEvent(EntityDeathEvent event) {
		entity.getWorld().playSound(((LivingEntity) entity).getEyeLocation(), Sound.ENTITY_POLAR_BEAR_DEATH, 0.8F, 2F);
	}

	@Override
	public int getTier() {
		return 0;
	}

	@Override
	public boolean isDungeonMob() {
		return true;
	}
}
