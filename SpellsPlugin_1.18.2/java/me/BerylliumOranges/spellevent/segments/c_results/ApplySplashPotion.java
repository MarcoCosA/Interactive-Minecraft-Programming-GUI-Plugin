package me.BerylliumOranges.spellevent.segments.c_results;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.BerylliumOranges.misc.GachaItems;
import me.BerylliumOranges.spellevent.other.Guideline;
import me.BerylliumOranges.spellevent.processes.EventItemStorage;
import me.BerylliumOranges.spellevent.processes.SegmentProcess;
import me.BerylliumOranges.spellevent.processes.SpellProcess;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;

public class ApplySplashPotion extends EventSegment {
	public static final String THIS_SEGMENT_TYPE = SEGMENT_TYPE_RESULT;
	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 50;
	public static final int ORIGINAL_AMPLIFICATION = 0;
	public static final int RARITY = 2;

	public static final boolean CONSUME_POT = false;

	public static Guideline processSegment(SegmentProcess proc) {
		int[] upgrades = getUpgrades(proc);
		if (proc.subtractMana(MANA_COST) < 1)
			return new Guideline(proc, true, true);
		if (proc.getY() < proc.getTimesProced().length) {
			ItemStack item = proc.getPresentSegments()[proc.getY() + 1][proc.getX()];
			if (item != null && item.getType().equals(Material.SPLASH_POTION)) {
				PotionMeta meta = (PotionMeta) item.getItemMeta();
				PotionData data = meta.clone().getBasePotionData();
				meta.clearCustomEffects();

				ItemStack copy = item.clone();

				ArrayList<PotionEffect> effectsToApply = new ArrayList<PotionEffect>();
				try {
					if (data.getType().getEffectType() != null) {
						int amp = (data.isUpgraded() ? 1 : 0) + getAmplification(upgrades);
						effectsToApply.add(new PotionEffect(data.getType().getEffectType(),
								Math.max(1, 20 * getPotionDuration(data.getType().getEffectType(), data.isExtended(), (data.isUpgraded() ? 2 : 1))),
								amp));
					}
				} catch (Exception er) {
					er.printStackTrace();
				}

				for (PotionEffect pot : meta.getCustomEffects()) {
					effectsToApply.add(new PotionEffect(pot.getType(), pot.getDuration(), pot.getAmplifier() + getAmplification(upgrades)));
				}

				for (PotionEffect effect : effectsToApply) {
					meta.addCustomEffect(effect, true);
				}

				copy.setItemMeta(meta);

				ThrownPotion splash = (ThrownPotion) proc.getSpellLocation().getWorld().spawnEntity(proc.getSpellLocation(),
						EntityType.SPLASH_POTION);

				splash.setItem(copy);

				if (CONSUME_POT) {
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(SpellProcess.plugin, new Runnable() {
						final int x = proc.getX();
						final int y = proc.getY() + 1;
						final LivingEntity caster = proc.getCaster();
						final ItemStack wand = proc.getContainer();

						public void run() {
							if (caster instanceof Player) {
								Player p = (Player) caster;
								try {
									if (p.getOpenInventory().getTitle().endsWith(wand.getItemMeta().getLocalizedName())) {
										if (p.getOpenInventory().getItem(x).getType().equals(Material.SPLASH_POTION)) {
											p.getOpenInventory().getItem((y * 9) + x).setType(Material.AIR);
										}
										return;
									}
								} catch (Exception er) {
									p.closeInventory();
								}
							}
							if (proc.getPresentSegments()[y][x].getType().equals(Material.SPLASH_POTION)) {
								HashMap<Integer, ItemStack> info = EventItemStorage.loadData(wand.getItemMeta().getLocalizedName(), false)
										.getItemSnapshot();
								info.get((y * 9) + x).setType(Material.AIR);

								EventItemStorage.writeStorage(info, wand.getItemMeta().getLocalizedName());
							}
						}
					});
				}
			}
		}
		return new Guideline(proc, true, false);
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "Throw Splash Potion");
		ArrayList<String> lore = new ArrayList<String>(Arrays.asList(ChatColor.WHITE + "Spawns the splash potion " + ChatColor.GREEN + "under"));
		// if (getAmplification(upgrades) > 0) {
		lore.add(ChatColor.WHITE + "this. " + ChatColor.WHITE + "" + ChatColor.GREEN + "+" + getAmplification(upgrades) + ChatColor.WHITE
				+ " effect amplification");
		// }
		lore.addAll(
				Arrays.asList(ChatColor.DARK_GRAY + /* "Consumes the potion" */ "", getCostText(MANA_COST)));
		meta.setLore(lore);
		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_RESULT);
		item.setItemMeta(meta);
		return item;
	}

	public static int getAmplification(int upgrades[]) {
		return ORIGINAL_AMPLIFICATION + (upgrades[0] + upgrades[1]) / 2;
	}

	// Not my code thankfully
	public static int getPotionDuration(PotionEffectType effect, boolean isExtended, int level) {

		if (effect == PotionEffectType.REGENERATION) {
			if (level >= 2) {
				return 23;
			}
			if (isExtended) {
				return 90;
			}
			if (!isExtended) {
				return 45;
			}
		}

		if (effect == PotionEffectType.SPEED) {
			if (level >= 2) {
				return 90;
			}
			if (isExtended) {
				return 480;
			}
			if (!isExtended) {
				return 180;
			}
		}

		if (effect == PotionEffectType.FIRE_RESISTANCE) {
			if (isExtended) {
				return 480;
			}
			if (!isExtended) {
				return 180;
			}
		}

		if (effect == PotionEffectType.NIGHT_VISION) {
			if (isExtended) {
				return 480;
			}
			if (!isExtended) {
				return 180;
			}
		}

		if (effect == PotionEffectType.INCREASE_DAMAGE) {
			if (level >= 2) {
				return 90;
			}
			if (isExtended) {
				return 480;
			}
			if (!isExtended) {
				return 180;
			}
		}

		if (effect == PotionEffectType.JUMP) {
			if (level >= 2) {
				return 90;
			}
			if (isExtended) {
				return 480;
			}
			if (!isExtended) {
				return 180;
			}
		}

		if (effect == PotionEffectType.WATER_BREATHING) {
			if (isExtended) {
				return 480;
			}
			if (!isExtended) {
				return 180;
			}
		}

		if (effect == PotionEffectType.INVISIBILITY) {
			if (isExtended) {
				return 480;
			}
			if (!isExtended) {
				return 180;
			}
		}

		if (effect == PotionEffectType.POISON) {
			if (level >= 2) {
				return 22;
			}
			if (isExtended) {
				return 90;
			}
			if (!isExtended) {
				return 45;
			}
		}

		if (effect == PotionEffectType.WEAKNESS) {
			if (isExtended) {
				return 240;
			}
			if (!isExtended) {
				return 90;
			}
		}

		if (effect == PotionEffectType.SLOW) {
			if (isExtended) {
				return 240;
			}
			if (!isExtended) {
				return 90;
			}
		}
		if (effect == PotionEffectType.LUCK) {
			return 300;
		}
		if (effect == PotionEffectType.SLOW_FALLING) {
			if (isExtended) {
				return 240;
			}
			if (!isExtended) {
				return 90;
			}
		}

		return 0;
	}
}
