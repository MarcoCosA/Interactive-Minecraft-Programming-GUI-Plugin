package me.BerylliumOranges.spellevent.processes;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityAirChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.entity.VillagerCareerChangeEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import io.papermc.paper.event.entity.EntityMoveEvent;
import me.BerylliumOranges.main.SpellPluginMain;
import me.BerylliumOranges.misc.ConfigInfo;
import me.BerylliumOranges.misc.GachaItems;
import me.BerylliumOranges.spellevent.entity_information.EntityStats;
import me.BerylliumOranges.spellevent.other.OtherItems;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment.EditOption;
import me.BerylliumOranges.spellevent.segments.c_results.Sacrifice;
import net.md_5.bungee.api.ChatColor;

public class SpellActions implements Listener {
	public SpellPluginMain plugin;

	public SpellActions(SpellPluginMain plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onVillagerGetJob(VillagerCareerChangeEvent e) {
		Villager v = e.getEntity();
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(SpellPluginMain.getInstance(), new Runnable() {
			public void run() {
				try {
					ArrayList<MerchantRecipe> list = getTradesFromProfession(e.getProfession());
					list.addAll(v.getRecipes());
					v.setRecipes(list);
				} catch (Exception er) {
					er.printStackTrace();
				}
			}
		}, 20);
	}

	public static ArrayList<MerchantRecipe> getTradesFromProfession(Profession prof) {
		int mod = (int) (Math.random() * 3);
		if (prof.equals(Profession.CLERIC)) {
			return getClericTrades(0);
		} else if (prof.equals(Profession.ARMORER)) {
			return getArmorerTrades(mod);
		} else if (prof.equals(Profession.TOOLSMITH)) {
			return getToolsmithTrades(mod);
		}
		return new ArrayList<MerchantRecipe>();
	}

	public static ArrayList<MerchantRecipe> getArmorerTrades(int mod) {
		ArrayList<MerchantRecipe> list = new ArrayList<MerchantRecipe>();
		MerchantRecipe randArmor = new MerchantRecipe(GachaItems.getRandomArmorIndicator(), 99);
		randArmor.setIngredients(Arrays.asList(GachaItems.getCurrency(7)));
		list.add(randArmor);
		for (int i = 0; i < mod; i++) {
			int rarity = GachaItems.randomRarityInt();
			rarity = Math.min(rarity, mod);
			ItemStack armor = GachaItems.getRandomArmor(rarity);
			armor = GachaItems.enchantItem(armor, Math.max(3 - mod, 0));
			MerchantRecipe recipe = new MerchantRecipe(armor, 1);
			recipe.setIngredients(GachaItems.getCurrencyAsList((int) ((rarity * 22) + Math.max(5, (Math.random() * 10)))));
			list.add(recipe);
		}
		return list;
	}

	public static ArrayList<MerchantRecipe> getClericTrades(int mod) {
		ArrayList<MerchantRecipe> list = new ArrayList<MerchantRecipe>();
		MerchantRecipe randArmor = new MerchantRecipe(GachaItems.getRandomSegmentIndicator(), 99);
		randArmor.setIngredients(Arrays.asList(GachaItems.getCurrency(7)));
		list.add(randArmor);
		for (int i = 0; i < mod; i++) {
			int rarity = GachaItems.randomRarityInt();
			rarity = Math.min(rarity, mod);
			ItemStack armor = GachaItems.getRandomSegment(rarity);
			MerchantRecipe recipe = new MerchantRecipe(armor, 1);
			recipe.setIngredients(GachaItems.getCurrencyAsList((int) ((rarity * 22) + Math.max(5, (Math.random() * 10)))));
			list.add(recipe);
		}
		return list;
	}

	public static ArrayList<MerchantRecipe> getToolsmithTrades(int mod) {
		ArrayList<MerchantRecipe> list = new ArrayList<MerchantRecipe>();
		MerchantRecipe randArmor = new MerchantRecipe(GachaItems.getRandomToolIndicator(), 99);
		randArmor.setIngredients(Arrays.asList(GachaItems.getCurrency(7)));
		list.add(randArmor);
		for (int i = 0; i < mod; i++) {
			int rarity = GachaItems.randomRarityInt();
			rarity = Math.min(rarity, mod);
			ItemStack armor = GachaItems.getRandomTool(rarity);
			armor = GachaItems.enchantItem(armor, Math.max(3 - mod, 0));
			MerchantRecipe recipe = new MerchantRecipe(armor, 1);
			recipe.setIngredients(GachaItems.getCurrencyAsList((int) ((rarity * 25) + Math.max(5, (Math.random() * 10)))));
			list.add(recipe);
		}
		return list;
	}

	@EventHandler
	public void onDeath(EntityDeathEvent e) {
		double health = e.getEntity().getMaxHealth();
		double damage = 0;
		try {
			damage = e.getEntity().getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue();
			int totalDropped = (int) ((health + damage * 4));
			if (totalDropped > e.getDroppedExp())
				e.setDroppedExp(totalDropped);
		} catch (Exception er) {
		}

		ArrayList<LivingEntity> targets = new ArrayList<LivingEntity>();
		targets.add(e.getEntity());

		EntityStats stat = EntityStats.getEntityStats(e.getEntity());
		stat.triggerDeathRattle(e);

		for (Entity ent : SpellPluginMain.getNearbyEntities(e.getEntity().getLocation(), 15)) {
			if (ent instanceof LivingEntity && !ent.equals(e.getEntity())) {
				SpellProcess process = new SpellProcess((LivingEntity) ent, targets);
				Location loc = e.getEntity().getEyeLocation().clone();
				loc.setDirection(loc.clone().subtract(((LivingEntity) ent).getEyeLocation()).toVector().normalize());
				process.setSpellLocation(loc);
				process.getUserContainers().addAll(SpellProcess.getArmorContainers((LivingEntity) ent));
				process.setProcessTags(Arrays.asList(EventSegment.NEARBY_DEATH_TRIGGER_TAG));
				process.setEvent(e);
				process.start();
			}
		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof LivingEntity) {
			EntityStats stats = EntityStats.getEntityStats((LivingEntity) e.getEntity());
			if (stats.getEntityPrefab() != null) {
				stats.getEntityPrefab().triggerDamageEvent(e);
			}

			ArrayList<LivingEntity> targets = new ArrayList<LivingEntity>();
			targets.add((LivingEntity) e.getEntity());

			SpellProcess process = new SpellProcess((LivingEntity) e.getEntity(), targets);
			process.getUserContainers().addAll(SpellProcess.getArmorContainers((LivingEntity) e.getEntity()));
			process.setProcessTags(Arrays.asList(EventSegment.TAKE_DAMAGE_TRIGGER_TAG));
			process.setEvent(e);
			process.start();
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		if (e.getEntity().getScoreboardTags().contains(Sacrifice.SACRIFICE_TAG)) {
			e.setDeathMessage(e.getEntity().getName() + " was sacrificed. Next time, try counterspell.");
			e.getEntity().removeScoreboardTag(Sacrifice.SACRIFICE_TAG);
		}
	}

	@EventHandler
	public void onArrowHit(ProjectileHitEvent e) {
		if (e.getHitEntity() != null && e.getEntity().getType().equals(EntityType.ARROW)) {
			Arrow a = (Arrow) e.getEntity();
			if (a.getShooter() instanceof LivingEntity) {
				ArrayList<LivingEntity> targets = new ArrayList<LivingEntity>();
				if (e.getHitEntity() instanceof LivingEntity)
					targets.add((LivingEntity) e.getHitEntity());
				SpellProcess process = new SpellProcess((LivingEntity) a.getShooter(), targets);
				ArrayList<ItemStack> damagerContainers = process.getUserContainers();
				for (String s : a.getScoreboardTags()) {
					if (s.startsWith("EventItem")) {
						damagerContainers.set(0, SpellProcess.findItemByLocalName((LivingEntity) a.getShooter(), s));
						process.setUserContainers(damagerContainers);
						process.setSpellLocation(a.getLocation());
						break;
					}
				}
				process.start();
			}
		}
	}

	@EventHandler
	public void onProjectileLaunch(ProjectileLaunchEvent e) {
		if (e.getEntityType().equals(EntityType.ARROW)) {
			if (e.getEntity().getShooter() instanceof LivingEntity) {
				LivingEntity ent = (LivingEntity) e.getEntity().getShooter();
				ItemStack item = ent.getEquipment().getItemInMainHand();
				if (item != null && item.hasItemMeta()) {
					if (item.getItemMeta().getLocalizedName().startsWith("EventItem")) {
						e.getEntity().addScoreboardTag(item.getItemMeta().getLocalizedName());
					}
				}
			}
		}
	}

	@EventHandler
	public void onHit(EntityDamageByEntityEvent e) {

	}

	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		if (!EventItemStorage.isArmor(e.getItem()))
			tryAndOpenEventItemInventory(e.getPlayer(), e.getAction(), e.getHand(), e.getItem());
	}

	@EventHandler
	public void onShift(PlayerToggleSneakEvent e) {
		if (e.getPlayer().getEquipment().getItemInMainHand().hasItemMeta()) {
			tryAndOpenEventItemInventory(e.getPlayer(), Action.PHYSICAL, EquipmentSlot.HAND, e.getPlayer().getEquipment().getItemInMainHand());
		}
	}

	// returns true if it successfully opened inventory
	public boolean tryAndOpenEventItemInventory(Player p, Action a, EquipmentSlot slot, ItemStack item) {
		boolean cancelled = false;
		if (item != null && item.hasItemMeta()) {
			ItemMeta meta = item.getItemMeta();
			if ((slot.equals(EquipmentSlot.HAND) && ((a.equals(Action.RIGHT_CLICK_AIR) || a.equals(Action.RIGHT_CLICK_BLOCK)) && p.isSneaking())
					|| (EventItemStorage.isArmor(item) && !p.isSneaking()))) {
				if (meta.getLocalizedName().startsWith("EventItem") && (ConfigInfo.isModify() || p.isOp())) {
					if (!ConfigInfo.isModify()) {
						p.sendMessage("Modifying spells is disabled, but you are op");
					}

					cancelled = true;
					p.openInventory(EventItemStorage.getItemInventory(item));
				} else if (meta.getLocalizedName().startsWith(EventItemStorage.MOB_INVENTORY_TAG)) {
					Inventory inv = Bukkit.createInventory(null, 54, ChatColor.GRAY + meta.getLocalizedName());
					try {
						HashMap<Integer, ItemStack> info = EventItemStorage
								.loadData(ChatColor.stripColor(item.getItemMeta().getLocalizedName()), false).getItemSnapshot();

						for (Map.Entry<Integer, ItemStack> entry : info.entrySet()) {
							inv.setItem(entry.getKey(), entry.getValue());
						}
					} catch (Exception error) {
						Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Made new mob inventory: " + meta.getLocalizedName() + error);
					}
					cancelled = true;
					p.openInventory(inv);
				}
			} else if ((a.equals(Action.RIGHT_CLICK_AIR) || a.equals(Action.RIGHT_CLICK_BLOCK)) && meta.getLocalizedName().startsWith("EventItem")) {
				SpellProcess process = new SpellProcess((LivingEntity) p, null);
				process.start();
				cancelled = true;
			}
		}
		return cancelled;
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Player p = (Player) e.getView().getPlayer();
		ItemStack item = e.getCurrentItem();
		Inventory inv = e.getView().getTopInventory();
		if (OtherItems.isProcedureInventory(e.getView())) {
			if (item != null && item.hasItemMeta()) {
				ItemMeta meta = item.getItemMeta();

				// String name =
				// SpellPluginMain.getStringInBrackets(e.getView().getTitle(),
				// 0);
				// boolean hijacked = Hijack.HIJACK_TAG.equals(name);

				if (meta.getLocalizedName().startsWith(EventSegment.LOCKED_INDICATOR) || meta.getLocalizedName().startsWith(EventSegment.INDICATOR)) {
					if (!e.getCursor().getType().equals(Material.AIR) && meta.getLocalizedName().startsWith(EventSegment.INDICATOR)) {
						e.setCurrentItem(null);
					} else
						e.setCancelled(true);

				} else if (meta.getLocalizedName().startsWith(EventSegment.SEGMENT_TYPE_LOGIC)
						|| meta.getLocalizedName().startsWith(EventSegment.SEGMENT_TYPE_TRIGGER)) {

					int[] upgrades = new int[2];
					for (int r = 0; r < EventSegment.ALL_SEGMENT_ITEMS.size(); r++) {
						if (EventSegment.ALL_SEGMENT_ITEMS.get(r).getItemMeta().getLocalizedName().equals(item.getItemMeta().getLocalizedName())) {
							try {
								Method m = EventSegment.ALL_SEGMENTS.get(r).getMethod("getUpdatedItem", int[].class, int.class);

								int model = meta.getCustomModelData();
								ItemStack updatedItem = (ItemStack) m.invoke(null, upgrades, model);
								updatedItem.setAmount(item.getAmount());
								meta = updatedItem.getItemMeta();
								meta.setCustomModelData(model);
								item.setItemMeta(meta);
							} catch (Exception er) {
								er.printStackTrace();
							}
							break;
						}
					}

					if (e.getAction().equals(InventoryAction.PICKUP_HALF)) {
						if (EventSegment.getEditOption(meta).equals(EditOption.ROTATABLE)
								|| EventSegment.getEditOption(meta).equals(EditOption.FULLY)) {
							int model = meta.getCustomModelData() + 1;
							if ((model - 2) % 4 == 3) {
								model -= 4;
							}
							meta.setCustomModelData(model);
							if (meta.hasLore()) {
								List<String> lore = meta.getLore();
								String replacement = getArrowModelFromInt((model - 1) % 4);
								for (int i = 0; i < lore.size(); i++) {
									for (int y = 0; y < 4; y++) {
										String str = lore.get(i);
										if (lore.get(i).indexOf(getArrowModelFromInt(y)) > -1) {
											str = str.substring(0, str.indexOf(getArrowModelFromInt(y))) + replacement
													+ str.substring(str.indexOf(getArrowModelFromInt(y)) + 1);
											lore.set(i, str);
											break;
										}
									}
								}
								meta.setLore(lore);
							}
							item.setItemMeta(meta);
							e.setCancelled(true);
						}
					}
				} else {
					if (meta.getLocalizedName().equals(OtherItems.getRealTitle(e.getView())))
						e.setCancelled(true);
				}
				if (!EventSegment.getEditOption(meta).equals(EditOption.FULLY)) {
					e.setCancelled(true);
				}
			}
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					formatProcedure(inv);
					p.updateInventory();
				}
			}, 10);

			if (e.getCurrentItem() != null
					&& (!e.getCurrentItem().hasItemMeta() || !e.getCurrentItem().getItemMeta().getLocalizedName().startsWith("Event"))
					&& !item.getType().equals(Material.POTION) && !item.getType().equals(Material.SPLASH_POTION)
					&& !item.getType().equals(Material.GLASS_BOTTLE)) {
				e.setCancelled(true);
			}
		}
	}

	public static void formatProcedure(Inventory inv) {
		removeIndicators(inv);
		addIndicatorsAndUpgrades(inv);
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		EntityStats.updatePlayer((Player) e.getPlayer());
		if (OtherItems.isProcedureInventory(e.getView())) {
			saveInvetory(e.getView().getTopInventory(), (Player) e.getPlayer(), OtherItems.getRealTitle(e.getView()));
		}
	}

	public static void saveInvetory(Inventory top, Player p, String title) {
		EventItemStorage.writeStorage(top, ChatColor.stripColor(title));
		p.updateInventory();
	}

	public static void removeIndicators(Inventory inv) {
		for (int i = 0; i < inv.getSize(); i++) {
			ItemStack item = inv.getItem(i);
			if (item != null && item.hasItemMeta()) {
				if (item.getItemMeta().getLocalizedName().startsWith(EventSegment.INDICATOR)) {
					inv.setItem(i, new ItemStack(Material.AIR));
				}
			}

		}
	}

	public static void addIndicatorsAndUpgrades(Inventory inv) {
		for (int i = 0; i < inv.getSize(); i++) {
			ItemStack item = inv.getItem(i);
			if (item != null && item.hasItemMeta()) {
				if (item.getItemMeta().getLocalizedName().startsWith(EventSegment.SEGMENT_TYPE_TRIGGER)
						|| item.getItemMeta().getLocalizedName().startsWith(EventSegment.SEGMENT_TYPE_LOGIC)
						|| item.getItemMeta().getLocalizedName().startsWith(EventSegment.SEGMENT_TYPE_RESULT)) {
					int arrowDir = getArrowDirectionFromCustomModel(item);
					int[] upgrades = new int[2];
					for (int w = 0; w < 4; w++) {
						int temp = getNext(w) + i;
						if (temp >= 0 && temp < inv.getSize())
							upgrades = EventSegment.getUpgradeFromItem(inv.getItem(temp), upgrades);
					}

					for (int r = 0; r < EventSegment.ALL_SEGMENT_ITEMS.size(); r++) {
						if (item.getItemMeta().getLocalizedName().contains(EventSegment.ALL_SEGMENT_ITEMS.get(r).getItemMeta().getLocalizedName())) {

							try {
								Method m = EventSegment.ALL_SEGMENTS.get(r).getMethod("getUpdatedItem", int[].class, int.class);
								Method modify = EventSegment.ALL_SEGMENTS.get(r).getMethod("modifyUpgradesInformation", ItemStack.class, int[].class);
								upgrades = (int[]) modify.invoke(null, item, upgrades);
								ItemStack updatedItem = (ItemStack) m.invoke(null, upgrades, arrowDir);
								ItemMeta meta = updatedItem.getItemMeta();
								meta.setLocalizedName(item.getItemMeta().getLocalizedName());
								ArrayList<String> lore = new ArrayList<String>(meta.getLore());
								lore.addAll(EventSegment.getExtraSegmentLore(meta));
								meta.setLore(lore);
								updatedItem.setItemMeta(meta);
								updatedItem.setAmount(item.getAmount());
								inv.setItem(i, updatedItem);
							} catch (Exception er) {
								er.printStackTrace();
							}
						}
					}
					if (item.getItemMeta().getLocalizedName().startsWith(EventSegment.SEGMENT_TYPE_TRIGGER)
							|| item.getItemMeta().getLocalizedName().startsWith(EventSegment.SEGMENT_TYPE_LOGIC)) {
						int loc = i;
						while (true) {
							int row = loc / 9;
							loc += getNext(arrowDir);
							if (loc < 0 || loc >= inv.getSize() || (Math.abs(getNext(arrowDir)) < 9 && row != loc / 9))
								break;
							if (inv.getItem(loc) != null && inv.getItem(loc).hasItemMeta()) {
								if (inv.getItem(loc).getItemMeta().getLocalizedName().startsWith(EventSegment.INDICATOR)
										|| inv.getItem(loc).getItemMeta().getLocalizedName().startsWith(EventSegment.SEGMENT_TYPE_UPGRADE)
										|| inv.getItem(loc).getItemMeta().getLocalizedName().startsWith(EventSegment.SEGMENT_TYPE_SOUND))
									continue;
								break;
							}
							inv.setItem(loc, getIndicator(item.getItemMeta().getLocalizedName()));
						}
					}
				}

			}
		}
		markTrigger(inv);
	}

	public static void markTrigger(Inventory input) {
		for (int i = 0; i < input.getSize(); i++) {
			if (input.getItem(i) != null && input.getItem(i).hasItemMeta()) {
				if (input.getItem(i).getItemMeta().getLocalizedName().startsWith(EventSegment.SEGMENT_TYPE_TRIGGER)) {
					ItemMeta meta = input.getItem(i).getItemMeta();
					// ArrayList<String> lore = new
					// ArrayList<String>(meta.getLore());
					// lore.add(ChatColor.GREEN + "Spell starts here");
					// meta.setLore(lore);
					meta.setDisplayName(meta.getDisplayName() + ChatColor.GREEN + " (Spell Start)");
					input.getItem(i).setItemMeta(meta);
					break;
				}
			}
		}
	}

	public static boolean checkPriority(ItemStack item1, ItemStack item2) {
		try {
			int prio1 = Integer.parseInt(
					SpellPluginMain.getStringInBrackets(getIndicator(item1.getItemMeta().getLocalizedName()).getItemMeta().getLocalizedName(), 1));
			int prio2 = Integer.parseInt(item2.getItemMeta().getLocalizedName(), 1);

			if (prio1 >= prio2) {
				return true;
			}

		} catch (Exception er) {
			er.printStackTrace();
		}
		return false;
	}

	public static ItemStack getIndicator(String localName) {
		if (localName.startsWith(EventSegment.SEGMENT_TYPE_SUMMON)) {
			return OtherItems.equipIndicator();
		}
		return OtherItems.triggerIndicator();
	}

	public static int getArrowDirectionFromCustomModel(ItemStack item) {
		return (item.getItemMeta().getCustomModelData() - 1) % 4;
	}

	public static int getNext(int arrowDirection) {
		switch (arrowDirection) {
		case 1:
			return +9;// down
		case 2:
			return -1;// <-
		case 3:
			return -9;// up
		default:
			return +1; // ->
		}
	}

	public static String getArrowModelFromInt(int model) {
		switch (model) {
		case 1:
			return "\u2193";// up
		case 2:
			return "\u2190";// <-
		case 3:
			return "\u2191";// down
		default:
			return "\u2192";// ->
		}
	}

	public static int getModelFromArrow(String model) {

		switch (model) {
		case "\u2193":
			return 1;// up
		case "\u2190":
			return 2;// <-
		case "\u2191":
			return 3;// down
		default:
			return 0;// ->
		}
	}
}
