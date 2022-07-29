package me.BerylliumOranges.spellevent.segments.e_logic;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import me.BerylliumOranges.main.SpellPluginMain;
import me.BerylliumOranges.spellevent.other.Guideline;
import me.BerylliumOranges.spellevent.processes.SegmentProcess;
import me.BerylliumOranges.spellevent.processes.SpellActions;
import me.BerylliumOranges.spellevent.processes.SpellProcess;
import me.BerylliumOranges.spellevent.projectiles.SpellProjectile;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;
import net.md_5.bungee.api.ChatColor;

public class ProximityActivation extends EventSegment {
	public static final String THIS_SEGMENT_TYPE = SEGMENT_TYPE_LOGIC;
	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 5;
	public static final double ORIGINAL_SIZE = 2.5;
	public static final int ACTIVATION_DELAY = 2;
	public static final int ORIGINAL_TIME = 20 + ACTIVATION_DELAY;
	public static final int RARITY = 2;

	public static Guideline processSegment(SegmentProcess proc) {
		int[] upgrades = getUpgrades(proc);
		if (proc.subtractMana(MANA_COST) < 1)
			return new Guideline(proc, true, true);
		proc.getSpellLocation().setDirection(new Vector(0, 1, 0));
		proc.getSpellLocation().add(0, proc.getSpellLocation().getBlock().getType().isSolid() ? 1 : 0, 0);
		BukkitTask task = new BukkitRunnable() {
			int time = 0;

			public void run() {
				time++;
				if (time > ACTIVATION_DELAY * 20) {
					for (Entity ent : SpellPluginMain.getNearbyEntities(proc.getSpellLocation(), ORIGINAL_SIZE)) {
						if (ent instanceof LivingEntity && !ent.isDead() && !ent.equals(proc.getCaster())) {
							proc.setTarget((LivingEntity) ent);
							findAndCallNext(proc);
							this.cancel();
							return;
						}
					}
					if (upgrades[0] + upgrades[1] < 8 && time % 4 == 0) {
						SpellProjectile.displayProjectileParticle(proc,
								proc.getSpellLocation().clone()
										.add(new Vector((Math.random()) * (Math.random() > 0.5 ? 1 : -1),
												(Math.random()) * (Math.random() > 0.5 ? 1 : -1),
												(Math.random()) * (Math.random() > 0.5 ? 1 : -1)).multiply(0.3)),
								SpellProjectile.BASE_HITBOX * 1.5);
					}
					if (time > getTime(upgrades) * 20) {
//					proc.getCaster().getWorld().playSound(proc.getCaster().getEyeLocation(),
//							Sound.ENTITY_ELDER_GUARDIAN_CURSE, 0.1F, 0);
						this.cancel();
					}
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
		meta.setDisplayName(getColorFromRarity(RARITY) + "Proximity Activation");
		meta.setLore(Arrays.asList(ChatColor.WHITE + "Spell stops until an entity gets",
				ChatColor.WHITE + "within " + ChatColor.GRAY + ORIGINAL_SIZE + ChatColor.WHITE + " blocks, "
						+ ChatColor.YELLOW + "targets " + ChatColor.WHITE + "the entity.",
				ChatColor.WHITE + "Activates after " + ChatColor.GRAY + ACTIVATION_DELAY + ChatColor.WHITE + " seconds",
				ChatColor.DARK_GRAY + "Cancels after " + (getTime(upgrades) - ACTIVATION_DELAY) + " seconds",
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
