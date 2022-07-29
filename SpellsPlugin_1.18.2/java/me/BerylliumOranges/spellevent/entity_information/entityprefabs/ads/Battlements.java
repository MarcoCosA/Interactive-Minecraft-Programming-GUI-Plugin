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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Endermite;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.BerylliumOranges.spellevent.entity_information.EntityStats;
import me.BerylliumOranges.spellevent.entity_information.EntityStats.SpellThread;
import me.BerylliumOranges.spellevent.entity_information.spellprefabs.armor.AntiBallisticMissiles;
import me.BerylliumOranges.spellevent.entity_information.spellprefabs.wands.ArtilleryCannon;
import me.BerylliumOranges.spellevent.entity_information.spellprefabs.wands.ArtillerySlowCannon;
import me.BerylliumOranges.spellevent.entity_information.spellprefabs.wands.FlashbangWand;
import me.BerylliumOranges.spellevent.entity_information.spellprefabs.wands.SpitCobweb;
import net.md_5.bungee.api.ChatColor;

public class Battlements extends AbstractAds {
	public static final String NAME = "Battlements";
	public static final String AD_NAME = ChatColor.of(new Color(168, 96, 50)) + NAME;
	public static final ItemStack ITEM = loadSpawnEgg();
	public static final double MAX_HEALTH = 120;

	public static final ArrayList<ItemStack> SPELLS = new ArrayList<>();

	private static ItemStack loadSpawnEgg() {

		ItemStack item = new ItemStack(Material.SKELETON_SPAWN_EGG);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(AD_NAME);
		meta.setLocalizedName(ENTITY_PREFAB_TAG + "[" + NAME + "]");
		item.setItemMeta(meta);
		return item;
	}

	public LivingEntity spawnEntity(Location loc) {
		Skeleton liv = loc.getWorld().spawn(loc, Skeleton.class);
		liv.setMaxHealth(MAX_HEALTH);
		liv.setSilent(true);
		liv.setHealth(MAX_HEALTH);
		// liv.setCustomName(ITEM.getItemMeta().getDisplayName());
		liv.setCustomNameVisible(false);
		liv.setPersistent(true);
		liv.setRemoveWhenFarAway(false);
		ItemStack bow = new ItemStack(Material.BOW);
		bow.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 5);
		bow.addUnsafeEnchantment(Enchantment.ARROW_KNOCKBACK, 3);

		liv.getEquipment().setItemInHand(bow);
		liv.getEquipment().setItemInOffHand(new ItemStack(Material.SHIELD));

		liv.getEquipment().setHelmet(AntiBallisticMissiles.getContainer());
		ItemStack item = new ItemStack(Material.IRON_HELMET);
		item.addUnsafeEnchantment(Enchantment.PROTECTION_EXPLOSIONS, getTier());

		item.setType(Material.IRON_CHESTPLATE);
		liv.getEquipment().setChestplate(item);
		item.setType(Material.IRON_LEGGINGS);
		liv.getEquipment().setLeggings(item);
		item.setType(Material.IRON_BOOTS);
		liv.getEquipment().setBoots(item);
		entity = liv;

		EntityStats stat = EntityStats.getEntityStats(liv);
		stat.setEntityPrefab(this);
		stat.getSpellThreads().add(new SpellThread());
		stat.getSpellThreads().get(0).getSpells().add(new AbstractMap.SimpleEntry<ItemStack, Integer>(ArtilleryCannon.getContainer(), 80));
		stat.getSpellThreads().add(new SpellThread());
		stat.getSpellThreads().get(1).getSpells().add(new AbstractMap.SimpleEntry<ItemStack, Integer>(ArtillerySlowCannon.getContainer(), 300));
		return liv;
	}

	public static final ArrayList<DamageCause> UNACCEPTABLE_CAUSES = new ArrayList<>(
			Arrays.asList(DamageCause.FIRE, DamageCause.FALL, DamageCause.FIRE_TICK));

	public void triggerDamageEvent(EntityDamageEvent event) {
		entity.getWorld().playSound(((LivingEntity) entity).getEyeLocation(), Sound.ENTITY_PLAYER_HURT, 1, 1);
	}

	@Override
	public int getTier() {
		return 2;
	}

}
