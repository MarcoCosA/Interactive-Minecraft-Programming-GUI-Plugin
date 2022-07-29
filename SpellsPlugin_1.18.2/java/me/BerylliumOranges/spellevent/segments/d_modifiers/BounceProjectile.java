package me.BerylliumOranges.spellevent.segments.d_modifiers;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.BerylliumOranges.spellevent.other.Guideline;
import me.BerylliumOranges.spellevent.processes.SegmentProcess;
import me.BerylliumOranges.spellevent.processes.SpellActions;
import me.BerylliumOranges.spellevent.projectiles.SpellProjectile;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSpellModifier;

public class BounceProjectile extends EventSpellModifier {

	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 2;

	public static final int RARITY = 1;

	public static Guideline processSegment(SegmentProcess proc) {
		if (proc.subtractMana(MANA_COST) < 1)
			return new Guideline(proc, true, true);
		proc.getProjectileModifiers().add(new BounceProjectile(proc));
		return findAndCallNext(proc);
	}

	@Override
	public boolean applyProjectileModifier(SpellProjectile proj, boolean finalCall) {
		proj.setBounceOffBlocks(true);
		return true;
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "Projectile Bounce");
		meta.setLore(Arrays.asList(ChatColor.WHITE + "Your next projectile " + ChatColor.AQUA + "bounces ",
				ChatColor.WHITE + "off of blocks", "",
				getCostText(MANA_COST) + " " + SpellActions.getArrowModelFromInt(dir)));
		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_MODIFIER);
		item.setItemMeta(meta);
		return item;
	}

	public BounceProjectile(SegmentProcess proc) {
		super(proc);
	}
}
