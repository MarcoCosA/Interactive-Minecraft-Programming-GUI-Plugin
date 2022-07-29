package me.BerylliumOranges.spellevent.entity_information.entityprefabs.bosses;

import java.awt.Color;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Spider;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.BerylliumOranges.spellevent.entity_information.EntityStats;
import me.BerylliumOranges.spellevent.entity_information.EntityStats.SpellThread;
import me.BerylliumOranges.spellevent.entity_information.spellprefabs.wands.FlashbangWand;
import me.BerylliumOranges.spellevent.entity_information.spellprefabs.wands.LichManaDrain;
import me.BerylliumOranges.spellevent.entity_information.spellprefabs.wands.SpitCobweb;
import net.md_5.bungee.api.ChatColor;

public class Lobster extends AbstractBoss {
	public static final String NAME = "Lobster";
	public static final String BOSS_NAME = ChatColor.of(new Color(255, 105, 180)) + NAME;
	public static final String BOSS_DISPLAY = ChatColor.GRAY + "Experiment 13: " + ChatColor.of(new Color(255, 105, 180)) + NAME;
	public static final ItemStack ITEM = loadSpawnEgg();
	public static final double MAX_HEALTH = 40;

	public static final ArrayList<ItemStack> SPELLS = new ArrayList<>();

	private static ItemStack loadSpawnEgg() {

		ItemStack item = new ItemStack(Material.SPIDER_SPAWN_EGG);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(BOSS_NAME);
		meta.setLocalizedName(ENTITY_PREFAB_TAG + "[" + NAME + "]");
		// ChatColor.GRAY + "Subject data as follows:"
		meta.setLore(Arrays.asList(BOSS_DISPLAY, ChatColor.GRAY + "Threat: " + ChatColor.GREEN + "LOW",
				ChatColor.GRAY + "Destruction: " + ChatColor.GREEN + "LOW", "", ChatColor.GRAY + "Information: ",
				ChatColor.WHITE + "  Several technicians experienced", ChatColor.WHITE + "ocular and auditory trauma"));

		item.setItemMeta(meta);
		return item;
	}

	public LivingEntity spawnEntity(Location loc) {
		Spider liv = loc.getWorld().spawn(loc, Spider.class);
		liv.setMaxHealth(MAX_HEALTH);
		liv.setHealth(MAX_HEALTH);
		liv.setCustomName(ITEM.getItemMeta().getDisplayName());
		liv.setPersistent(true);
		entity = liv;

		EntityStats stat = EntityStats.getEntityStats(liv);
		stat.setEntityPrefab(this);
		stat.makeBossBar(BarColor.GREEN, BOSS_DISPLAY, false);
		stat.getSpellThreads().add(new SpellThread());
		stat.getSpellThreads().get(0).getSpells().add(new AbstractMap.SimpleEntry<ItemStack, Integer>(SpitCobweb.getContainer(), 80));
		return liv;
	}

	public static final ArrayList<DamageCause> UNACCEPTABLE_CAUSES = new ArrayList<>(
			Arrays.asList(DamageCause.FIRE, DamageCause.FALL, DamageCause.FIRE_TICK));

	public void triggerDamageEvent(EntityDamageEvent event) {
		if (!UNACCEPTABLE_CAUSES.contains(event.getCause()))
			if (entity.getHealth() - event.getDamage() > 0 && Math.random() > 0.85) {
				EntityStats stat = EntityStats.getEntityStats(entity);
				stat.castSpell(FlashbangWand.getContainer(), true);
			}
	}

	@Override
	public int getTier() {
		return 0;
	}
}
