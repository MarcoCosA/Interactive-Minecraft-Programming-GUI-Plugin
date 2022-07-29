package me.BerylliumOranges.spellevent.entity_information.entityprefabs.ads;

import java.awt.Color;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.BerylliumOranges.misc.GachaItems;
import me.BerylliumOranges.spellevent.entity_information.EntityStats;
import me.BerylliumOranges.spellevent.entity_information.EntityStats.SpellThread;
import me.BerylliumOranges.spellevent.entity_information.spellprefabs.armor.LandWhale;
import me.BerylliumOranges.spellevent.entity_information.spellprefabs.wands.Jump;
import me.BerylliumOranges.spellevent.entity_information.spellprefabs.wands.TeleportAboveEnemy;
import net.md_5.bungee.api.ChatColor;

public class BankGolem extends AbstractAds {
	public static final String NAME = "Bank Security";
	public static final String AD_NAME = ChatColor.of(new Color(168, 127, 50)) + NAME;
	public static final ItemStack ITEM = loadSpawnEgg();
	public static final double MAX_HEALTH = 45;

	public static final ArrayList<ItemStack> SPELLS = new ArrayList<>();

	private static ItemStack loadSpawnEgg() {

		ItemStack item = new ItemStack(Material.GHAST_SPAWN_EGG);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(AD_NAME);
		meta.setLocalizedName(ENTITY_PREFAB_TAG + "[" + NAME + "]");
		item.setItemMeta(meta);
		return item;
	}

	public LivingEntity spawnEntity(Location loc) {
		IronGolem liv = loc.getWorld().spawn(loc, IronGolem.class);
		// liv.setMaxHealth(MAX_HEALTH);
		// liv.setSilent(true);
		// liv.setHealth(MAX_HEALTH);
		// liv.setCustomName(ITEM.getItemMeta().getDisplayName());
		liv.setCustomNameVisible(false);

		liv.getEquipment().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
		liv.getEquipment().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
		liv.getEquipment().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
		liv.getEquipment().setBoots(LandWhale.getContainer());

		liv.setPersistent(true);
		entity = liv;

		EntityStats stat = EntityStats.getEntityStats(liv);
		stat.setEntityPrefab(this);
		stat.getSpellThreads().add(new SpellThread());
		stat.getSpellThreads().get(0).getSpells().add(new AbstractMap.SimpleEntry<ItemStack, Integer>(Jump.getContainer(), 200));
		return liv;
	}

	public static final ArrayList<DamageCause> UNACCEPTABLE_CAUSES = new ArrayList<>(
			Arrays.asList(DamageCause.FIRE, DamageCause.FALL, DamageCause.FIRE_TICK));

	public void triggerDamageEvent(EntityDamageEvent event) {
	}

	public void triggerDeathEvent(EntityDeathEvent event) {
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
