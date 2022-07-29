package me.BerylliumOranges.spellevent.segments.b_triggers.armor_triggers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.IronGolem;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.BerylliumOranges.spellevent.other.Guideline;
import me.BerylliumOranges.spellevent.other.custom_events.EntityGroundPoundEvent;
import me.BerylliumOranges.spellevent.processes.SegmentProcess;
import me.BerylliumOranges.spellevent.processes.SpellActions;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;
import me.BerylliumOranges.spellevent.segments.abstracts.EventTriggerSegment;

public final class WhenFall extends EventTriggerSegment {
	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 30;

	public static final int RARITY = 4;

	public static final List<String> ACCEPTED_TAGS = Arrays.asList(EventSegment.FALL_TRIGGER_TAG);

	public static Guideline processSegment(SegmentProcess proc) {
		if (!hasTag(proc, WhenFall.class) || (!(proc.getCaster() instanceof IronGolem) && proc.addSegmentCooldown(WhenFall.class, (int) MANA_COST))) {
			return new Guideline(proc, false, true);
		}
		proc.setManaMultiplier(0);
		proc.setMaxCasts(1);

		if (proc.getEvent() instanceof EntityGroundPoundEvent) {
			EntityGroundPoundEvent event = (EntityGroundPoundEvent) proc.getEvent();
			int upgrades = getUpgradesFromFall(event.getFallDistance());

			int blocks = (int) (event.getFallDistance() + 2.2);
			if (event.getFallDistance() < 11)
				blocks = (int) (event.getFallDistance() + 1.5);

			EventSegment.alertCaster(proc.getCaster(), ITEM, ChatColor.WHITE + "Fell " + ChatColor.YELLOW + blocks + ChatColor.WHITE
					+ " blocks, spell upgraded " + ChatColor.YELLOW + upgrades + ChatColor.WHITE + "x", false);
			proc.setCrit(proc.getCrit() + upgrades);
		}
		return findAndCallNext(proc);
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "When You Fall");
		ArrayList<String> lore = new ArrayList<String>(Arrays.asList(ChatColor.WHITE + "Casts the spell if you " + ChatColor.YELLOW + "fall ",
				ChatColor.WHITE + "more than " + ChatColor.YELLOW + "4 " + ChatColor.WHITE + "blocks. " + ChatColor.YELLOW + "Upgrades",
				ChatColor.WHITE + "the spell the further you fall.", ChatColor.DARK_GRAY + "Maximum 15 upgrades", getCooldownLore((int) MANA_COST),
				getCastText(), getFreeText() + " " + ChatColor.YELLOW + SpellActions.getArrowModelFromInt(dir)));
		meta.setLore(lore);
		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_TRIGGER);
		item.setItemMeta(meta);
		return item;
	}

	public static int getUpgradesFromFall(float fallDistance) {
		return (int) Math.min(15, Math.max(0, Math.pow(fallDistance - 10, 1 / 2.0)));
	}
}
