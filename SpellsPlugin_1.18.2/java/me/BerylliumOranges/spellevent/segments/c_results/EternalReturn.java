package me.BerylliumOranges.spellevent.segments.c_results;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.BerylliumOranges.main.SpellPluginMain;
import me.BerylliumOranges.spellevent.entity_information.EntityStats;
import me.BerylliumOranges.spellevent.other.Guideline;
import me.BerylliumOranges.spellevent.other.custom_events.SpellCastEvent;
import me.BerylliumOranges.spellevent.processes.SegmentProcess;
import me.BerylliumOranges.spellevent.processes.SpellProcess;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;
import net.md_5.bungee.api.ChatColor;

public class EternalReturn extends EventSegment {
	public static final String THIS_SEGMENT_TYPE = SEGMENT_TYPE_RESULT;
	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 150;
	public static final int DURATION = 7;
	public static final int RARITY = 4;

	private static ChatColor[] colors = { ChatColor.GREEN, ChatColor.YELLOW, ChatColor.RED };

	public static Guideline processSegment(SegmentProcess proc) {
		int[] upgrades = getUpgrades(proc);
		if (proc.subtractMana(MANA_COST) < 1)
			return new Guideline(proc, true, true);
		Record rec = new Record(proc);
		final int time = (int) (getDuration(upgrades) * 20);
		BukkitTask task = new BukkitRunnable() {
			public void run() {
				rec.tick(time);
				if (rec.currentTime > time) {
					rec.rewindEvents(proc);
					this.cancel();
				}
			}
		}.runTaskTimer(SpellPluginMain.getInstance(), 0, 0);
		proc.getSpellSlot().getTasks().add(task);
		return new Guideline(proc, true, false);
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "Eternal Return");
		meta.setLore(Arrays.asList(ChatColor.WHITE + "The actions of each " + ChatColor.YELLOW + "target " + ChatColor.WHITE + "",
				ChatColor.WHITE + "in the next " + ChatColor.GREEN + getDuration(upgrades) + ChatColor.WHITE + " seconds are ",
				ChatColor.GREEN + "repeated " + ChatColor.WHITE + "until the spell is", ChatColor.GREEN + "cancelled", "", getCostText(MANA_COST)));

		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_RESULT);
		item.setItemMeta(meta);
		return item;
	}

	public static double getDuration(int upgrades[]) {
		return Math.max(2, (DURATION - (upgrades[0] + upgrades[1]) / 4.0));
	}

	public static class Record implements Listener {
		public static ArrayList<Record> allRecords = new ArrayList<Record>();
		int currentTime = 0;
		public SegmentProcess proc;
		public boolean rewinding = false;
		public boolean playingForward = false;
		public static int rewindMultiplier = 4;
		private boolean cancelled = false;
		private ArrayList<LivingEntity> initialTargets = new ArrayList<LivingEntity>();

		public Record(SegmentProcess proc) {
			this.proc = proc;
			initialTargets = proc.getTargetsCopy();
			// RegisteredListener registeredListener = new
			// RegisteredListener(this, (listener, event) -> onEvent(event),
			// EventPriority.LOW,
			// SpellPluginMain.getInstance(), false);
			// for (HandlerList handler : HandlerList.getHandlerLists())
			// handler.register(registeredListener);
			SpellPluginMain.getInstance().getServer().getPluginManager().registerEvents(this, SpellPluginMain.getInstance());

		}

		public ArrayList<LivingEntity> getTargets() {
			ArrayList<LivingEntity> targets = new ArrayList<LivingEntity>();
			for (LivingEntity target : initialTargets) {
				if (proc.getTargetsCopy().contains(target)) {
					targets.add(target);
				}
			}
			return targets;
		}

		public void playbackEvents(SegmentProcess proc) {
			if (cancelled)
				return;
			playingForward = true;
			for (int i = currentTime - 1; i >= 0; i--) {
				ArrayList<Event> events = recordedEvents.get(i);
				if (events == null)
					continue;
				for (Event event : events) {
					new BukkitRunnable() {
						public void run() {
							if (event instanceof EntityRegainHealthEvent) {
								EntityRegainHealthEvent e = (EntityRegainHealthEvent) event;
								if (e.getEntity() instanceof LivingEntity) {
									LivingEntity liv = (LivingEntity) e.getEntity();
									liv.setHealth(Math.max(0, Math.min(liv.getMaxHealth(), liv.getHealth() + e.getAmount())));
								}
							} else if (event instanceof EntityDamageEvent) {
								EntityDamageEvent e = (EntityDamageEvent) event;
								if (e.getEntity() instanceof LivingEntity) {
									LivingEntity liv = (LivingEntity) e.getEntity();
									liv.setHealth(Math.max(0, Math.min(liv.getMaxHealth(), liv.getHealth() - e.getFinalDamage())));
								}
							} else if (event instanceof FoodLevelChangeEventRecord) {
								FoodLevelChangeEventRecord e = (FoodLevelChangeEventRecord) event;
								e.getEntity().setFoodLevel(e.getFoodLevel());
							} else if (event instanceof BlockBreakRecord) {
								BlockBreakRecord e = (BlockBreakRecord) event;
								if (e.originalEvent.getBlock().getType().equals(e.from)) {
									e.originalEvent.getBlock().setType(Material.AIR);
								} else {
									e.setBlockExists(false);
								}
							} else if (event instanceof BlockPlaceRecord) {
								BlockPlaceRecord e = (BlockPlaceRecord) event;
								if (e.isBlockExists()) {
									e.getPlayer().getInventory().removeItem(new ItemStack(e.getFrom()));
									e.originalEvent.getBlock().setType(e.getFrom());
								}
							} else if (event instanceof EntityChangeBlockEvent) {
								EntityChangeBlockEvent e = (EntityChangeBlockEvent) event;
								e.getBlock().setType(e.getTo());
							} else if (event instanceof PlayerMoveEvent) {
								PlayerMoveEvent e = (PlayerMoveEvent) event;
								e.getPlayer().teleport(e.getTo());
								e.getPlayer().setFallDistance(0);
							} else if (event instanceof EntityMoveEvent) {
								EntityMoveEvent e = (EntityMoveEvent) event;
								e.getEntity().teleport(e.getTo());
								e.getEntity().setFallDistance(0);
							} else if (event instanceof PlayerDropItemEvent) {
								PlayerDropItemEvent e = (PlayerDropItemEvent) event;
								if (!e.getItemDrop().isDead()) {
									e.getPlayer().getInventory().removeItem(e.getItemDrop().getItemStack());
									e.getItemDrop().teleport(e.getItemDrop().getLocation().clone().subtract(0, 100000, 0));
									e.getItemDrop().setGravity(true);
								}
							} else if (event instanceof PlayerItemHeldEvent) {
								PlayerItemHeldEvent e = (PlayerItemHeldEvent) event;
								e.getPlayer().getInventory().setHeldItemSlot(e.getNewSlot());
							} else if (event instanceof EntityShootBowEvent) {
								EntityShootBowEvent e = (EntityShootBowEvent) event;
								Arrow arrow = e.getEntity().launchProjectile(Arrow.class);
								arrow.setVelocity(arrow.getVelocity().multiply(e.getForce()));
							} else if (event instanceof SpellCastEvent) {
								SpellCastEvent e = (SpellCastEvent) event;
								try {
									SpellProcess spell = (SpellProcess) e.getSpellProcess().clone();
									spell.setAutoCasted(true);
									spell.process(spell.getUserSegments(), spell.getUserContainers(), spell.getUser());
								} catch (CloneNotSupportedException e1) {
									e1.printStackTrace();
								}
							}
						}
					}.runTaskLater(SpellPluginMain.getInstance(), i);
				}
			}
			new BukkitRunnable() {
				public void run() {
					if (cancelled)
						return;
					playingForward = false;
					rewindEvents(proc);
				}
			}.runTaskLater(SpellPluginMain.getInstance(), currentTime - 1);

		}

		public void rewindEvents(SegmentProcess proc) {
			if (cancelled)
				return;
			rewinding = true;

			for (int i = currentTime - 1; i >= 0; i--) {
				ArrayList<Event> events = recordedEvents.get(i);
				if (events == null)
					continue;
				for (Event event : events) {
					new BukkitRunnable() {
						public void run() {
							if (event instanceof EntityRegainHealthEvent) {
								EntityRegainHealthEvent e = (EntityRegainHealthEvent) event;
								if (e.getEntity() instanceof LivingEntity) {
									LivingEntity liv = (LivingEntity) e.getEntity();
									liv.setHealth(Math.max(0, Math.min(liv.getMaxHealth(), liv.getHealth() - e.getAmount())));
								}
							} else if (event instanceof EntityDamageEvent) {
								EntityDamageEvent e = (EntityDamageEvent) event;
								if (e.getEntity() instanceof LivingEntity) {
									LivingEntity liv = (LivingEntity) e.getEntity();
									liv.setHealth(Math.max(0, Math.min(liv.getMaxHealth(), liv.getHealth() + e.getFinalDamage())));
								}
							} else if (event instanceof FoodLevelChangeEventRecord) {
								FoodLevelChangeEventRecord e = (FoodLevelChangeEventRecord) event;
								e.getEntity().setFoodLevel(e.oldFoodLevel);
							} else if (event instanceof BlockBreakRecord) {
								BlockBreakRecord e = (BlockBreakRecord) event;
								if (e.isBlockExists())
									e.originalEvent.getBlock().setType(e.getFrom());
							} else if (event instanceof BlockPlaceRecord) {
								BlockPlaceRecord e = (BlockPlaceRecord) event;
								if (e.originalEvent.getBlock().getType().equals(e.getFrom())) {
									e.getPlayer().getInventory().addItem(new ItemStack(e.getFrom()));
									e.originalEvent.getBlock().setType(Material.AIR);
								} else {
									e.setBlockExists(false);
								}
							} else if (event instanceof EntityChangeBlockEvent) {
								EntityChangeBlockEvent e = (EntityChangeBlockEvent) event;
								e.getBlock().setType(Material.AIR);
							} else if (event instanceof EntityMoveEvent) {
								EntityMoveEvent e = (EntityMoveEvent) event;
								e.getEntity().teleport(e.getFrom());
								e.getEntity().setFallDistance(0);
							} else if (event instanceof PlayerDropItemEvent) {
								PlayerDropItemEvent e = (PlayerDropItemEvent) event;
								if (!e.getItemDrop().isDead()) {
									e.getPlayer().getInventory().addItem(e.getItemDrop().getItemStack());
									e.getItemDrop().teleport(e.getItemDrop().getLocation().clone().add(0, 100000, 0));
									e.getItemDrop().setGravity(false);
									e.getItemDrop().setVelocity(new Vector(0, 0, 0));
									e.getItemDrop().setPickupDelay(currentTime + 10);
								}
							} else if (event instanceof PlayerItemHeldEvent) {
								PlayerItemHeldEvent e = (PlayerItemHeldEvent) event;
								e.getPlayer().getInventory().setHeldItemSlot(e.getPreviousSlot());
							} else if (event instanceof EntityShootBowEvent) {
								EntityShootBowEvent e = (EntityShootBowEvent) event;
								e.getProjectile().remove();
							} else if (event instanceof SpellCastEvent) {
								SpellCastEvent e = (SpellCastEvent) event;
								if (e.getProcess() != null && e.getProcess().getSpellSlot() != null)
									e.getProcess().getStats().addMana((int) e.getProcess().getSpellSlot().getManaUsed());
							}

							// if (tickTime == 0) {
							// for (Entry<InventoryHolder, ItemStack[]> entry :
							// inventories.entrySet()) {
							// Bukkit.broadcastMessage("setting inventory");
							// entry.getKey().getInventory().setContents(entry.getValue());
							// if (entry.getKey() instanceof Player)
							// ((Player) entry.getKey()).updateInventory();
							// }
							// for (Entry<Player, HashMap<Integer, ItemStack>>
							// playerEntry : playerInventories.entrySet()) {
							// for (Entry<Integer, ItemStack> itemsEntry :
							// playerEntry.getValue().entrySet()) {
							// Bukkit.broadcastMessage("setting item " +
							// itemsEntry.getKey() + ", " +
							// itemsEntry.getValue());
							// playerEntry.getKey().getInventory().setItem(itemsEntry.getKey(),
							// itemsEntry.getValue());
							// }
							// playerEntry.getKey().updateInventory();
							// }
							//
							// }
						}
					}.runTaskLater(SpellPluginMain.getInstance(), (currentTime - i) / rewindMultiplier);
				}
			}
			new BukkitRunnable() {
				int count = 0;

				public void run() {
					if (count > (currentTime / rewindMultiplier))
						this.cancel();
					count++;
					for (LivingEntity ent : getTargets()) {
						for (PotionEffect ef : ent.getActivePotionEffects()) {
							ent.removePotionEffect(ef.getType());
							int mod = 1 + rewindMultiplier;
							PotionEffect pot = new PotionEffect(ef.getType(), ef.getDuration() + mod, ef.getAmplifier(), ef.isAmbient(),
									ef.hasParticles());
							pot.apply(ent);
						}
						EntityStats stats = EntityStats.getEntityStats(ent);
						stats.subtractMana(stats.getManaRegen() * rewindMultiplier);
					}
				}
			}.runTaskTimer(SpellPluginMain.getInstance(), 0, 1);
			new BukkitRunnable() {
				public void run() {
					if (cancelled)
						return;
					rewinding = false;
					playbackEvents(proc);
				}
			}.runTaskLater(SpellPluginMain.getInstance(), currentTime / rewindMultiplier);

		}

		public Record getInstance() {
			return this;
		}

		public HashMap<Integer, ArrayList<Event>> recordedEvents = new HashMap<>();

		public void checkIfCancelled() {
			if (getTargets().isEmpty() | proc.getSpellSlot().isStopped()) {
				cancelled = true;
				HandlerList.unregisterAll(getInstance());
			}
		}

		public void addEntry(Event e) {
			checkIfCancelled();
			if (e instanceof InventoryEvent) {
				boolean found = false;
				for (LivingEntity ent : getTargets()) {
					for (Entity p : ((InventoryEvent) e).getViewers()) {
						if (p.equals(ent)) {
							found = true;
						}
					}
				}
				if (!found)
					return;
			} else if (e instanceof EntityEvent) {
				if (!getTargets().contains(((EntityEvent) e).getEntity())) {
					return;
				}
			} else if (e instanceof PlayerEvent) {
				if (!getTargets().contains(((PlayerEvent) e).getPlayer())) {
					return;
				} else if (e instanceof PlayerQuitEvent) {
					PlayerQuitEvent p = (PlayerQuitEvent) e;
					p.getPlayer().setHealth(0);
					initialTargets.remove(p.getPlayer());
					checkIfCancelled();
				}
			} else {
				Bukkit.getLogger().info(ChatColor.RED + "Error, event is not entity or player event: " + e.getEventName());
				return;
			}

			if ((rewinding || playingForward) && e instanceof Cancellable) {
				Cancellable c = (Cancellable) e;
				c.setCancelled(true);
			} else {
				if (recordedEvents.get(currentTime) == null) {
					ArrayList<Event> list = new ArrayList<>();
					list.add(e);
					recordedEvents.put(currentTime, list);
				} else {
					recordedEvents.get(currentTime).add(e);
				}
			}
		}

		public HashMap<Entity, Location> moves = new HashMap<>();

		// public HashMap<InventoryHolder, ItemStack[]> inventories = new
		// HashMap<>();
		// public HashMap<Player, HashMap<Integer, ItemStack>> playerInventories
		// = new HashMap<>();

		public void tick(int time) {
			for (LivingEntity ent : getTargets()) {
				if ((currentTime) % 20 == 0 && currentTime < time) {
					if ((time - currentTime) / 20 == 1) {
						EventSegment.alertTarget(ent, proc.getCaster(), ITEM, "Loop is starting...", true);
					} else {
						EventSegment.alertTarget(ent, proc.getCaster(), ITEM, "Loop starts in "
								+ /*
									 * colors[(int) ((rec.currentTime / (double)
									 * time) * colors.length)]
									 */ChatColor.RED + (((time - currentTime) / 20)), true);
					}
				}
				// if (ent instanceof InventoryHolder) {
				// if (currentTime == 0) {
				// if (ent instanceof Player) {
				// Player p = (Player) ent;
				// HashMap<Integer, ItemStack> contents = new HashMap<>();
				// ItemStack[] invs = p.getInventory().getContents().clone();
				// for (int j = 0; j < invs.length; j++) {
				// if (invs[j] == null) {
				// contents.put(j, new ItemStack(Material.AIR));
				// } else {
				// contents.put(j, invs[j]);
				// }
				// }
				// playerInventories.put(p, contents);
				// } else {
				// InventoryHolder i = (InventoryHolder) ent;
				// ItemStack[] contents =
				// i.getInventory().getContents().clone();
				// for (int j = 0; j < contents.length; j++) {
				// if (contents[j] == null) {
				// contents[j] = new ItemStack(Material.AIR);
				// }
				// }
				// inventories.put(i, contents);
				// Bukkit.broadcastMessage("contents are " +
				// i.getInventory().getContents());
				// }
				// }
				// }
				Location loc = moves.get(ent);
				if (loc != null) {
					addEntry(new EntityMoveEvent(ent.getLocation(), loc, ent));
				}
				moves.put(ent, ent.getLocation());
			}
			currentTime++;
		}

		@EventHandler(priority = EventPriority.LOWEST)
		public void heal(FoodLevelChangeEvent e) {
			addEntry(new FoodLevelChangeEventRecord(e.getEntity(), e.getFoodLevel(), e.getItem()));
		}

		@EventHandler(priority = EventPriority.LOWEST)
		public void heal(EntityRegainHealthEvent e) {
			addEntry(e);
		}

		@EventHandler(priority = EventPriority.LOWEST)
		public void damage(EntityDamageEvent e) {
			addEntry(e);
		}

		@EventHandler(priority = EventPriority.LOWEST)
		public void blockBreak(BlockBreakEvent e) {
			addEntry(new BlockBreakRecord(e.getBlock(), e.getPlayer(), e));
			e.setDropItems(false);
		}

		@EventHandler(priority = EventPriority.LOWEST)
		public void placeBlock(BlockPlaceEvent e) {
			addEntry(new BlockPlaceRecord(e.getBlock(), e.getPlayer(), e));
		}

		@EventHandler(priority = EventPriority.LOWEST)
		public void place(PlayerDropItemEvent e) {
			addEntry(e);
		}

		@EventHandler(priority = EventPriority.LOWEST)
		public void spellCast(SpellCastEvent e) {
			addEntry(e);
		}

		@EventHandler(priority = EventPriority.LOWEST)
		public void logout(PlayerQuitEvent e) {
			addEntry(e);
		}

		@EventHandler(priority = EventPriority.LOWEST)
		public void openInv(InventoryOpenEvent e) {
			addEntry(e);
		}

		@EventHandler(priority = EventPriority.LOWEST)
		public void clickInv(InventoryClickEvent e) {
			addEntry(e);
		}

		@EventHandler(priority = EventPriority.LOWEST)
		public void holdItem(PlayerItemHeldEvent e) {
			addEntry(e);
		}

		@EventHandler(priority = EventPriority.LOWEST)
		public void shootBow(EntityShootBowEvent e) {
			addEntry(e);
		}
	}

	public static class BlockBreakRecord extends EntityEvent implements Cancellable {
		private Material from;
		private BlockBreakEvent originalEvent;
		private boolean blockExists = true;

		public BlockBreakRecord(@NotNull Block theBlock, @NotNull Player player, BlockBreakEvent e) {
			super(player);
			from = theBlock.getType();
			originalEvent = e;
		}

		public Material getFrom() {
			return from;
		}

		public void setFrom(Material from) {
			this.from = from;
		}

		@Override
		public void setCancelled(boolean c) {
			originalEvent.setCancelled(true);
		}

		@Override
		public @NotNull HandlerList getHandlers() {
			return null;
		}

		@Override
		public boolean isCancelled() {
			return originalEvent.isCancelled();
		}

		public boolean isBlockExists() {
			return blockExists;
		}

		public void setBlockExists(boolean blockExists) {
			this.blockExists = blockExists;
		}
	}

	public static class BlockPlaceRecord extends EntityEvent implements Cancellable {
		private Material from;
		public BlockPlaceEvent originalEvent;
		private Player player;
		private boolean blockExists = true;

		public BlockPlaceRecord(@NotNull Block theBlock, @NotNull Player player, BlockPlaceEvent e) {
			super(player);
			from = theBlock.getType();
			originalEvent = e;
			this.player = player;
		}

		public Material getFrom() {
			return from;
		}

		public void setFrom(Material from) {
			this.from = from;
		}

		@Override
		public void setCancelled(boolean c) {
			originalEvent.setCancelled(true);
		}

		@Override
		public @NotNull HandlerList getHandlers() {
			return null;
		}

		@Override
		public boolean isCancelled() {
			return originalEvent.isCancelled();
		}

		public Player getPlayer() {
			return player;
		}

		public void setPlayer(Player player) {
			this.player = player;
		}

		public boolean isBlockExists() {
			return blockExists;
		}

		public void setBlockExists(boolean blockExists) {
			this.blockExists = blockExists;
		}
	}

	public static class FoodLevelChangeEventRecord extends FoodLevelChangeEvent {
		int oldFoodLevel;

		public FoodLevelChangeEventRecord(@NotNull HumanEntity what, int level, @Nullable ItemStack item) {
			super(what, level, item);
			oldFoodLevel = what.getFoodLevel();
		}

	}

	public static class EntityMoveEvent extends EntityEvent {
		private Location from;
		private Location to;
		private Entity entity;

		public EntityMoveEvent(@NotNull Location from, @NotNull Location to, @NotNull Entity entity) {
			super(entity);
			this.from = from;
			this.to = to;
			this.entity = entity;
		}

		@Override
		public @NotNull HandlerList getHandlers() {
			return null;
		}

		public Location getFrom() {
			return from;
		}

		public void setFrom(Location from) {
			this.from = from;
		}

		public Location getTo() {
			return to;
		}

		public void setTo(Location to) {
			this.to = to;
		}

		public Entity getEntity() {
			return entity;
		}

		public void setEntity(Entity entity) {
			this.entity = entity;
		}

	}

}
