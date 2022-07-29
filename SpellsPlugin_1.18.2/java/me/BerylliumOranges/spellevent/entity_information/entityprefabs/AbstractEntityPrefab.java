package me.BerylliumOranges.spellevent.entity_information.entityprefabs;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import me.BerylliumOranges.main.SpellPluginMain;
import me.BerylliumOranges.misc.DirectoryTools;

public abstract class AbstractEntityPrefab {
	public static final ItemStack ITEM = loadSpawnEgg();
	public static final double MAX_HEALTH = 20;
	public static final String NAME = null;
	public static final String ENTITY_PREFAB_TAG = "EntityPrefab";

	public static final ArrayList<Class<AbstractEntityPrefab>> ENTITY_PREFAB_CLASSES = loadEntityPrefabClasses();
	public static final ArrayList<ItemStack> ENTITY_PREFAB_ITEMS = loadEntityPrefabEggs();

	public LivingEntity entity = null;

	public AbstractEntityPrefab() {

	}

	private static ItemStack loadSpawnEgg() {
		return null;
	}

	public LivingEntity spawnEntity(Location loc) {
		return null;
	}

	public void triggerDeathEvent(EntityDeathEvent event) {

	}

	public void triggerDamageEvent(EntityDamageEvent event) {

	}

	@SuppressWarnings("unchecked")
	public static ArrayList<Class<AbstractEntityPrefab>> loadEntityPrefabClasses() {
		ArrayList<Class<AbstractEntityPrefab>> classesList = new ArrayList<Class<AbstractEntityPrefab>>();

		String path = "me.BerylliumOranges.spellevent.entity_information.entityprefabs";
		for (Class<?> clazz : DirectoryTools.getClasses(path)) {
			if (AbstractEntityPrefab.class.isAssignableFrom(clazz))
				classesList.add((Class<AbstractEntityPrefab>) clazz);
		}
		return classesList;
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<ItemStack> loadEntityPrefabEggs() {
		ArrayList<ItemStack> classesList = new ArrayList<ItemStack>();
		String path = "me.BerylliumOranges.spellevent.entity_information.entityprefabs";
		for (Class<?> clazz : DirectoryTools.getClasses(path)) {
			if (AbstractEntityPrefab.class.isAssignableFrom(clazz)) {
				try {
					ItemStack item = (ItemStack) (((Class<AbstractEntityPrefab>) clazz).getField("ITEM").get(null));
					if (item != null)
						classesList.add(item);
				} catch (Exception er) {
				}
			}
		}
		return classesList;
	}

	public static AbstractEntityPrefab getEntityPrefab(ItemStack item) {
		for (Class<AbstractEntityPrefab> clazz : AbstractEntityPrefab.ENTITY_PREFAB_CLASSES) {
			try {
				String name = (String) clazz.getField("NAME").get(null);
				String localName = SpellPluginMain.getStringInBrackets(item.getItemMeta().getLocalizedName(), 0);
				if (name == null || localName == null)
					continue;
				if (name.equals(localName)) {
					return clazz.newInstance();
				}
			} catch (Exception er) {
				er.printStackTrace();
			}
		}
		return null;
	}

	public int getTier() {
		return 0;
	}

	public boolean isDungeonMob() {
		return false;
	}
}
