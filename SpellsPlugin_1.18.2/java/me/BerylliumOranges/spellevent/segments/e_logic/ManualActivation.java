package me.BerylliumOranges.spellevent.segments.e_logic;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import me.BerylliumOranges.spellevent.other.Guideline;
import me.BerylliumOranges.spellevent.processes.SegmentProcess;
import me.BerylliumOranges.spellevent.processes.SpellActions;
import me.BerylliumOranges.spellevent.processes.SpellProcess;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;
import net.md_5.bungee.api.ChatColor;

public class ManualActivation extends EventSegment {
	public static final String THIS_SEGMENT_TYPE = SEGMENT_TYPE_LOGIC;
	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 5;
	public static final int ORIGINAL_TIME = 30;
	public static final int RARITY = 2;

	public static Guideline processSegment(SegmentProcess proc) {
		int[] upgrades = getUpgrades(proc);
		if (proc.subtractMana(MANA_COST) < 1)
			return new Guideline(proc, true, true);
		proc.getSpellLocation().setDirection(new Vector(0, 1, 0));

		BukkitTask task = new BukkitRunnable() {
			int time = 0;

			public void run() {
				if (proc.getCaster() instanceof Player) {
					Player p = (Player) proc.getCaster();
					if (p.isSneaking()) {
						findAndCallNext(proc);
						this.cancel();
					}
				} else {
					findAndCallNext(proc);
					this.cancel();
				}
				if (upgrades[0] + upgrades[1] < 8 && time % 3 == 0)
					proc.getSpellLocation().getWorld().spawnParticle(Particle.CLOUD, proc.getSpellLocation(), 0);
				time++;
				if (time > getTime(upgrades) * 20) {
//					proc.getCaster().getWorld().playSound(proc.getCaster().getEyeLocation(),
//							Sound.ENTITY_ELDER_GUARDIAN_CURSE, 0.1F, 0);
					this.cancel();
				}
			}
		}.runTaskTimer(SpellProcess.plugin, 0, 0);
		proc.getSpellSlot().getTasks().add(task);

		return new Guideline(proc, false, true);
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "Manual Activation");
		meta.setLore(Arrays.asList(ChatColor.WHITE + "Spell stops until the caster " + ChatColor.GRAY + "sneaks",
				ChatColor.DARK_GRAY + "cancels after " + getTime(upgrades) + " seconds",
				getCostText(MANA_COST) + " " + SpellActions.getArrowModelFromInt(dir)));
		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_LOGIC);
		item.setItemMeta(meta);
		return item;
	}

	public static int getTime(int[] upgrades) {
		return ORIGINAL_TIME + (upgrades[0] + upgrades[1]) * 20;
	}

}
