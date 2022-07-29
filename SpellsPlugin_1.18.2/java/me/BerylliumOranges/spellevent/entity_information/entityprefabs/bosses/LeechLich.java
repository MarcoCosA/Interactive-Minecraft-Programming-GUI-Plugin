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
import org.bukkit.entity.Stray;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.BerylliumOranges.spellevent.entity_information.EntityStats;
import me.BerylliumOranges.spellevent.entity_information.EntityStats.SpellThread;
import me.BerylliumOranges.spellevent.entity_information.spellprefabs.wands.LichManaDrain;
import net.md_5.bungee.api.ChatColor;

public class LeechLich extends AbstractBoss {
	public static final String NAME = "Leech Lich";
	public static final String BOSS_NAME = ChatColor.of(new Color(230, 181, 255)) + NAME;
	public static final String BOSS_DISPLAY = ChatColor.GRAY + "Product 78: " + ChatColor.of(new Color(211, 181, 255)) + NAME;
	public static final ItemStack ITEM = loadSpawnEgg();
	public static final double MAX_HEALTH = 200;

	public static final ArrayList<ItemStack> SPELLS = new ArrayList<>();

	private static ItemStack loadSpawnEgg() {

		ItemStack item = new ItemStack(Material.STRAY_SPAWN_EGG);
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
		Stray liv = loc.getWorld().spawn(loc, Stray.class);
		liv.setMaxHealth(MAX_HEALTH);
		liv.setHealth(MAX_HEALTH);
		liv.setCustomName(ITEM.getItemMeta().getDisplayName());
		liv.setPersistent(true);
		// The prismarine brick stairs look cool too
		liv.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_SWORD));

		entity = liv;

		EntityStats stat = EntityStats.getEntityStats(liv);
		stat.setEntityPrefab(this);
		stat.makeBossBar(BarColor.BLUE, BOSS_DISPLAY, true);
		stat.getSpellThreads().add(new SpellThread());
		stat.getSpellThreads().get(0).getSpells().add(new AbstractMap.SimpleEntry<ItemStack, Integer>(LichManaDrain.getContainer(), 200));
		stat.setManaRegen(15);
		return liv;
	}

	public static final ArrayList<DamageCause> UNACCEPTABLE_CAUSES = new ArrayList<>(
			Arrays.asList(DamageCause.FIRE, DamageCause.FALL, DamageCause.FIRE_TICK));

	public void triggerDamageEvent(EntityDamageEvent event) {
	}

	@Override
	public int getTier() {
		return 0;
	}
}
