package me.BerylliumOranges.spellevent.segments.abstracts;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import me.BerylliumOranges.misc.DirectoryTools;

public abstract class EventTriggerSegment extends EventSegment {
	// public final static ItemStack ITEM = getUpdatedItem(new int[2]);

	public static final String THIS_SEGMENT_TYPE = SEGMENT_TYPE_TRIGGER;

	public final static ArrayList<Class<EventTriggerSegment>> ALL_TRIGGER_SEGMENTS = loadTriggerSegmentClasses();
	public final static ArrayList<ItemStack> ALL_TRIGGER_SEGMENT_ITEMS = loadTriggerSegmentItems();
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final int MANA_COST_MULTIPLIER = 1;


	public static int getCost(int[] upgrades, int originalCost, int nothing1, int nothing2) {
		return originalCost;
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<Class<EventTriggerSegment>> loadTriggerSegmentClasses() {
		ArrayList<Class<EventTriggerSegment>> classesList = new ArrayList<Class<EventTriggerSegment>>();

		String path = "me.BerylliumOranges.spellevent.segments";
		for (Class<?> clazz : DirectoryTools.getClasses(path)) {
			if (EventTriggerSegment.class.isAssignableFrom(clazz))
				classesList.add((Class<EventTriggerSegment>) clazz);
		}
		return classesList;
	}

	public static ArrayList<ItemStack> loadTriggerSegmentItems() {
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		for (Class<EventTriggerSegment> ev : EventTriggerSegment.ALL_TRIGGER_SEGMENTS) {
			try {

				Field f = ev.getField("ITEM");
				ItemStack m = (ItemStack) ev.getField("ITEM").get(null);
				// if (!m.equals(ITEM)) {
				items.add(m);
				// }
			} catch (Exception er) {
				er.printStackTrace();
			}
		}
		return items;
	}

	public static ChatColor getColorFromRarity(int rarity) {
		if (rarity == 5)
			return ChatColor.RED;
		return EventSegment.getColorFromRarity(rarity);
	}
}
