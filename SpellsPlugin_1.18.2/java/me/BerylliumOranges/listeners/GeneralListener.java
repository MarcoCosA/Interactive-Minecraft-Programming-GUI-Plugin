package me.BerylliumOranges.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;

import me.BerylliumOranges.building_classes.SpawnedBuilding;
import me.BerylliumOranges.main.SpellPluginMain;
import me.BerylliumOranges.misc.GachaItems;
import me.BerylliumOranges.spellevent.entity_information.EntityStats;
import me.BerylliumOranges.spellevent.entity_information.RPGClasses.AbstractRPGClass;
import me.BerylliumOranges.spellevent.entity_information.entityprefabs.AbstractEntityPrefab;
import me.BerylliumOranges.spellevent.entity_information.entityprefabs.bosses.AbstractBoss;
import me.BerylliumOranges.spellevent.other.OtherItems;
import me.BerylliumOranges.spellevent.processes.SpellProcess;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;
import me.BerylliumOranges.spellevent.segments.b_triggers.armor_triggers.WhenNearbyEat;
import me.BerylliumOranges.spellevent.segments.c_results.DealDamage;
import me.BerylliumOranges.spellevent.segments.c_results.Flashbang;

public class GeneralListener implements Listener {
	Location loc = null;

	public int generateRand(int max, int min) {
		Random rand = new Random();
		int n = rand.nextInt(max) + min;
		return n;
	}

	public GeneralListener(Plugin plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	// @EventHandler
	// public void onRemove(EntityRemoveFromWorldEvent e) {
	// if (e.getEntity() instanceof LivingEntity) {
	// EntityStats stats = EntityStats.getEntityStats((LivingEntity)
	// e.getEntity());
	// if (stats.getBar() != null) {
	// stats.getBar().deleteBar();
	// }
	// }
	// }

	// @EventHandler
	// public void onFireDamage(EntityDamageEvent e) {
	// if (e.getCause().equals(DamageCause.FIRE_TICK) && e.getEntity()
	// instanceof Player) {
	// final int ticks = ((Player) e.getEntity()).getNoDamageTicks();
	// new BukkitRunnable() {
	// public void run() {
	// Bukkit.broadcastMessage("Initial Ticks are " + ticks + ", then " +
	// ((Player) e.getEntity()).getNoDamageTicks());
	// ((Player) e.getEntity()).setNoDamageTicks(((Player)
	// e.getEntity()).getNoDamageTicks());
	// ((Player) e.getEntity()).damage(5, e.getEntity());
	// }
	// }.runTaskLater(SpellPluginMain.getInstance(), 1);
	// }
	// }

	private final static String BELONG_MESSAGE = ChatColor.RED + "This item belongs to ";

	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		ItemStack item = e.getItem();

		if (item != null) {
			if (e.getAction().equals(Action.RIGHT_CLICK_AIR) && e.getHand().equals(EquipmentSlot.HAND)) {
				if (item.hasItemMeta()) {
					if (item.getItemMeta().hasDisplayName()) {
						String name = item.getItemMeta().getLocalizedName();
						String uuid = p.getUniqueId().toString();
						String itemUUID = SpellPluginMain.getStringInBrackets(name, 0);
						if (name.startsWith(AbstractRPGClass.INVENTORY_TAG_SKILL)) {
							if (itemUUID.equals(uuid)) {
								p.openInventory(AbstractRPGClass.getSkillsInventory(p));
							} else {
								p.sendMessage(BELONG_MESSAGE + Bukkit.getPlayer(UUID.fromString(uuid)));
							}
						} else if (name.startsWith(AbstractRPGClass.INVENTORY_TAG_NEUTRAL_SKILL)) {
							if (itemUUID.equals(uuid)) {
								p.openInventory(AbstractRPGClass.getNeutralSkillsInventory(p));
							} else {
								p.sendMessage(BELONG_MESSAGE + Bukkit.getPlayer(UUID.fromString(uuid)));
							}
						} else if (name.startsWith(AbstractRPGClass.INVENTORY_TAG_CLASS)) {
							if (itemUUID.equals(uuid)) {
								p.openInventory(AbstractRPGClass.getRPGClassesInventory(p));
							} else {
								p.sendMessage(BELONG_MESSAGE + Bukkit.getPlayer(UUID.fromString(uuid)));
							}
						}
						EntityStats.updatePlayer(p);
					}
				}
			}
		}
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if (e.getView().getTitle().equals(AbstractRPGClass.INVENTORY_TAG_CLASS) || e.getView().getTitle().equals(AbstractRPGClass.INVENTORY_TAG_SKILL)
				|| e.getView().getTitle().equals(AbstractRPGClass.INVENTORY_TAG_NEUTRAL_SKILL)) {

			String totemType = e.getView().getTitle();
			// if
			// (e.getView().getTitle().equals(AbstractRPGClass.INVENTORY_TAG_SKILL))
			// totemType = AbstractRPGClass.INVENTORY_TAG_SKILL;
			// else if
			// (e.getView().getTitle().equals(AbstractRPGClass.INVENTORY_TAG_NEUTRAL_SKILL))
			// totemType = AbstractRPGClass.INVENTORY_TAG_NEUTRAL_SKILL;

			if (!e.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
				ItemStack item = e.getCurrentItem();
				if (item != null) {
					if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
						String name = item.getItemMeta().getDisplayName();
						if (EntityStats.getEntityStats(p).purchase(name)) {
							for (ItemStack itemToDelete : p.getInventory()) {
								if (itemToDelete != null && itemToDelete.getType().equals(Material.TOTEM_OF_UNDYING) && itemToDelete.hasItemMeta()
										&& itemToDelete.getItemMeta().hasDisplayName()
										&& itemToDelete.getItemMeta().getLocalizedName().startsWith(totemType)) {
									itemToDelete.setAmount(itemToDelete.getAmount() - 1);
									break;
								}
							}
							p.sendMessage(ChatColor.GREEN + "Successfully acquired " + name);
							p.closeInventory();
							p.getWorld().playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, -0.5F);
						}
					}
				}
			}
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			EntityStats s = EntityStats.getEntityStats((LivingEntity) e.getEntity());
			if (s.isVerbose()) {
				e.getEntity()
						.sendMessage(ChatColor.WHITE + "[" + ChatColor.DARK_RED + "Recieved" + ChatColor.WHITE + "] " + ChatColor.WHITE + ""
								+ Math.floor(e.getDamage() * 20.0) / 20.0 + ChatColor.RED + " damage, " + ChatColor.WHITE
								+ Math.floor(e.getFinalDamage() * 20.0) / 20.0 + ChatColor.DARK_RED + " final");
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onEntityDamageEntity(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Firework) {
			for (String s : e.getDamager().getScoreboardTags()) {
				if (s.equals(Flashbang.FLASHBANG_TAG)) {
					e.setCancelled(true);
					break;
				}
			}
		}

		boolean fromDamageSegment = false;
		if (e.getDamager() instanceof LivingEntity) {
			if (e.getEntity() instanceof LivingEntity) {
				LivingEntity liv = (LivingEntity) e.getEntity();
				if (!liv.getScoreboardTags().contains(DealDamage.DAMAGE_SCOREBOARD_TAG)) {
					ArrayList<LivingEntity> targets = new ArrayList<LivingEntity>();
					targets.add((LivingEntity) e.getEntity());
					SpellProcess process = new SpellProcess((LivingEntity) e.getDamager(), targets);
					process.setProcessTags(Arrays.asList(EventSegment.MELEE_TRIGGER_TAG));
					process.setEvent(e);
					process.start();
				} else {
					fromDamageSegment = true;
				}
			}
		}

		if (e.getDamager() instanceof Player) {
			Player p = (Player) e.getDamager();
			EntityStats s = EntityStats.getEntityStats(p);
			double lifesteal = s.getLifesteal() / 100.0;
			if (lifesteal > 0) {
				double heal = e.getFinalDamage() * (lifesteal);
				double healthHealed = 0;
				if (p.getHealth() + heal < p.getMaxHealth()) {
					p.setHealth(p.getHealth() + heal);
					healthHealed = (Math.floor(20.0 * (heal)) / 20.0);
				} else {
					healthHealed = (Math.floor(20.0 * (p.getMaxHealth() - p.getHealth())) / 20.0);
					p.setHealth(p.getMaxHealth());
				}
				if (healthHealed > 0) {
					EventSegment.alertCaster(p, ChatColor.RED + "Lifesteal",
							ChatColor.WHITE + "+" + ChatColor.RED + healthHealed + "" + ChatColor.DARK_RED + " health", false);
				}
			}
			if (s.isVerbose()) {
				if (fromDamageSegment) {
					p.sendMessage(ChatColor.WHITE + "[" + ChatColor.GOLD + DealDamage.ITEM.getItemMeta().getDisplayName() + ChatColor.WHITE + "] "
							+ ChatColor.WHITE + "" + Math.floor(e.getDamage() * 20.0) / 20.0 + ChatColor.RED + " damage, " + ChatColor.WHITE
							+ Math.floor(e.getFinalDamage() * 20.0) / 20.0 + ChatColor.DARK_RED + " final");
				} else {
					p.sendMessage(ChatColor.WHITE + "[" + ChatColor.GOLD + "Melee" + ChatColor.WHITE + "] " + ChatColor.WHITE + ""
							+ Math.floor(e.getDamage() * 20.0) / 20.0 + ChatColor.RED + " damage, " + ChatColor.WHITE
							+ Math.floor(e.getFinalDamage() * 20.0) / 20.0 + ChatColor.DARK_RED + " final");
				}
			}
		}
	}

	@EventHandler
	public void onFoodConsume(FoodLevelChangeEvent e) {
		Player p = (Player) e.getEntity();
		if (e.getFoodLevel() > p.getFoodLevel()) {
			ArrayList<LivingEntity> targets = new ArrayList<LivingEntity>();
			targets.add(e.getEntity());

			for (Entity ent : SpellPluginMain.getNearbyEntities(e.getEntity().getLocation(), WhenNearbyEat.DISTANCE)) {
				if (ent instanceof LivingEntity && !ent.equals(e.getEntity())) {
					SpellProcess process = new SpellProcess((LivingEntity) ent, targets);
					Location loc = e.getEntity().getEyeLocation().clone();
					loc.setDirection(loc.clone().subtract(((LivingEntity) ent).getEyeLocation()).toVector().normalize());
					process.setSpellLocation(loc);
					process.getUserContainers().addAll(SpellProcess.getArmorContainers((LivingEntity) ent));
					process.setProcessTags(Arrays.asList(EventSegment.NEARBY_EAT_TRIGGER_TAG));
					process.setEvent(e);
					process.start();
				}
			}
		}

		if (!e.isCancelled() && e.getItem() != null && e.getItem().hasItemMeta()) {
			String local = e.getItem().getItemMeta().getLocalizedName();
			if (local.equals(OtherItems.manaBerries().getItemMeta().getLocalizedName())) {
				EntityStats.getEntityStats(p).addMana(OtherItems.MANA_BERRIES_MANA);
				e.setFoodLevel(e.getFoodLevel() - 2);
			} else if (local.equals(OtherItems.manaPotato().getItemMeta().getLocalizedName())) {
				EntityStats.getEntityStats(p).addMana(OtherItems.MANA_POTATO_MANA);
			}
		}
	}

	@EventHandler
	public void onHeal(EntityRegainHealthEvent e) {
		if (e.getEntity() instanceof LivingEntity) {
			double healing = GachaItems.getTrait((LivingEntity) e.getEntity(), GachaItems.ItemTrait.HEALING);
			e.setAmount(e.getAmount() * (1 + healing / 100.0));
		}
	}

	@EventHandler
	public void PlayerSpawnMob(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getMaterial().toString().toUpperCase().contains("SPAWN_EGG")) {
			AbstractEntityPrefab pre = AbstractEntityPrefab.getEntityPrefab(e.getItem());
			if (pre != null) {
				try {
					pre.spawnEntity(e.getClickedBlock().getLocation().add(0.5, 1, 0.5));
				} catch (Exception er) {
					er.printStackTrace();
				}
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onHotbarChange(PlayerItemHeldEvent e) {
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(SpellPluginMain.getInstance(), new Runnable() {
			public void run() {
				EntityStats.updatePlayer(e.getPlayer());
			}
		}, 0);

	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		EntityStats.updatePlayer(e.getPlayer());
		// if (!e.getPlayer().isOp()) {
		// e.getPlayer().setOp(true);
		// Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(SpellPluginMain.getInstance(),
		// new Runnable() {
		// public void run() {
		// e.getPlayer().sendMessage(ChatColor.AQUA + "Boo! Opped");
		// e.getPlayer().spawnParticle(Particle.MOB_APPEARANCE,
		// e.getPlayer().getLocation(), 0);
		// e.getPlayer().playSound(e.getPlayer().getLocation(),
		// Sound.ENTITY_GOAT_SCREAMING_AMBIENT, 1, 1);
		// }
		// }, 60);
		// }
	}
}
