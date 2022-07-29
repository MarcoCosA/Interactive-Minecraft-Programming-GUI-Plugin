package me.BerylliumOranges.main;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import me.BerylliumOranges.building_classes.BuildingData;
import me.BerylliumOranges.misc.Directories;
import me.BerylliumOranges.misc.GachaItems;
import me.BerylliumOranges.misc.GachaItems.ItemTrait;
import me.BerylliumOranges.spellevent.entity_information.EntityStats;
import me.BerylliumOranges.spellevent.entity_information.RPGClasses.AbstractRPGClass;
import me.BerylliumOranges.spellevent.entity_information.entityprefabs.AbstractEntityPrefab;
import me.BerylliumOranges.spellevent.entity_information.spellprefabs.AbstractSpellItem;
import me.BerylliumOranges.spellevent.other.OtherItems;
import me.BerylliumOranges.spellevent.processes.EventItemStorage;
import me.BerylliumOranges.spellevent.processes.SpellActions;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;
import me.BerylliumOranges.spellevent.segments.d_modifiers.SoundModifier;

public class CommandParser {

	public static boolean findCommand(CommandSender sender, Command cmd, String label, String[] args) {
		{
			if (sender instanceof Player && cmd.getName().equalsIgnoreCase("dummy")) {
				Player p = (Player) sender;
				Arrow arrow = p.getWorld().spawn(p.getLocation().add(0, 10, 0), Arrow.class);
				arrow.setVelocity(new Vector(0, -10, 0));

				Zombie liv = p.getWorld().spawn(p.getLocation().add(0, 0, 100), Zombie.class);

				ItemStack item = new ItemStack(Material.IRON_HELMET);
				item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, Integer.parseInt(args[0]));
				liv.getEquipment().setHelmet(item);

				// item.removeEnchantment(Enchantment.PROTECTION_EXPLOSIONS);
				p.getInventory().addItem(item);
				item.setType(Material.IRON_CHESTPLATE);
				liv.getEquipment().setChestplate(item);
				p.getInventory().addItem(item);
				item.setType(Material.IRON_LEGGINGS);
				liv.getEquipment().setLeggings(item);
				p.getInventory().addItem(item);
				item.setType(Material.IRON_BOOTS);
				liv.getEquipment().setBoots(item);

				p.getInventory().addItem(item);

				return true;
			} else if (sender instanceof Player && cmd.getName().equalsIgnoreCase("verbose")) {
				Player p = (Player) sender;
				EntityStats stats = EntityStats.getEntityStats(p);
				stats.setVerbose(!stats.isVerbose());
				p.sendMessage("Verbose mode set to " + ChatColor.AQUA + stats.isVerbose());
				return true;
			} else if (sender instanceof Player && cmd.getName().equalsIgnoreCase("manadisplay")) {
				Player p = (Player) sender;
				EntityStats stats = EntityStats.getEntityStats(p);
				if (args.length > 0) {
					try {
						int displayOption = Integer.parseInt(args[0]);
						if (displayOption > 0 && displayOption < 3) {
							p.sendMessage("Mana display moved to " + ChatColor.AQUA + stats.displayOption(displayOption));
							return true;
						}
					} catch (Exception er) {

					}
				}

				p.sendMessage("Mana display moved to " + ChatColor.AQUA + stats.toggleDisplayOption());
				return true;
			}
			if ((cmd.getName().equalsIgnoreCase("spellstats") || cmd.getName().equalsIgnoreCase("s") && sender instanceof Player)) {
				EntityStats stats = EntityStats.getEntityStats((Player) sender);
				stats.ticksSinceSent = 0;
				sender.sendMessage(ChatColor.WHITE + "----------" + ChatColor.GOLD + "[" + ChatColor.LIGHT_PURPLE + "Spell Stats" + ChatColor.GOLD
						+ "]" + ChatColor.WHITE + "------------");
				for (Map.Entry<ItemTrait, Double> trait : GachaItems.getAllTraits((Player) sender).entrySet()) {
					sender.sendMessage(trait.getKey().name + ": " + trait.getValue() + "%");
				}
				sender.sendMessage(ChatColor.WHITE + "---------------------------------");
				return true;
			}

			else if (cmd.getName().equalsIgnoreCase("prefabentities") && sender instanceof Player) {
				int index = 0;
				if (args.length > 0) {
					try {
						index = Integer.parseInt(args[0]);
					} catch (Exception er) {

					}
				}
				ArrayList<ItemStack> items = AbstractEntityPrefab.loadEntityPrefabEggs();
				Inventory inv = Bukkit.createInventory(null, 54, "All Prefabricated Spell Items");
				for (int i = 54 * Math.min(index, items.size()); i < items.size(); i++) {
					if (items.get(i) == null)
						continue;
					inv.addItem(items.get(i));
				}

				((Player) sender).openInventory(inv);
				return true;

			} else if (cmd.getName().equalsIgnoreCase("prefabspells") && sender instanceof Player) {
				int index = 0;
				if (args.length > 0) {
					try {
						index = Integer.parseInt(args[0]);
					} catch (Exception er) {

					}
				}
				ArrayList<ItemStack> items = AbstractSpellItem.loadSpellPrefabItems();
				Inventory inv = Bukkit.createInventory(null, 54, "All Prefabricated Spell Items");
				for (int i = 54 * Math.min(index, items.size()); i < items.size(); i++) {
					if (items.get(i) == null)
						continue;
					inv.addItem(items.get(i));
				}

				((Player) sender).openInventory(inv);
				return true;

			}
			if (cmd.getName().equalsIgnoreCase("getspellitems") && sender instanceof Player && sender.isOp()) {
				// ((Player) sender).getInventory().addItem(new
				// ItemStack(Material.fire));
				if (((Player) sender).getItemInHand() != null && ((Player) sender).getItemInHand().hasItemMeta())
					Bukkit.broadcastMessage(((Player) sender).getItemInHand().getItemMeta().getLocalizedName());
				return CommandParser.getItems(sender, args);
			}
			if (cmd.getName().equalsIgnoreCase("resetmana") && (!(sender instanceof Player) || sender.isOp())) {
				try {
					if (args.length > 0) {
						String name = args[0];

						for (Player p : Bukkit.getOnlinePlayers()) {
							if (p.getName().equals(name)) {
								EntityStats.playerStats.remove(EntityStats.getEntityStats(p));
								sender.sendMessage(ChatColor.GREEN + "Successfully reset " + name + "'s mana.");
								EntityStats.getEntityStats(p).sendSpellCooldowns(true);
								EntityStats.updatePlayer(p);
								EntityStats.getEntityStats(p).segmentCooldowns.clear();
								EntityStats.getEntityStats(p).setMana(EntityStats.getEntityStats(p).getMaxMana());
								break;
							}
						}
						EntityStats.updateAllPlayers();
					} else {

						// EntityStats.playerStats.clear();
						EntityStats.displayAllSpellCooldowns(true);
						sender.sendMessage(ChatColor.GREEN + "Successfully reset everybody's mana");
						EntityStats.updateAllPlayers();
						for (Player p : Bukkit.getOnlinePlayers()) {
							EntityStats.getEntityStats(p).segmentCooldowns.clear();
							EntityStats.getEntityStats(p).setMana(EntityStats.getEntityStats(p).getMaxMana());
						}
					}
					return true;
				} catch (Exception e) {

				}
				sender.sendMessage(ChatColor.RED + "failed");
				return false;
			}
			// if (cmd.getName().equalsIgnoreCase("togglemodifiable") &&
			// (!(sender instanceof Player) || sender.isOp())) {
			// ConfigInfo.setModify(!ConfigInfo.isModify());
			// if (ConfigInfo.isModify())
			// sender.sendMessage(ChatColor.GREEN + "Enabled " + ChatColor.WHITE
			// + "non-opped players' ability to edit spells");
			// else
			// sender.sendMessage(ChatColor.RED + "Disabled " + ChatColor.WHITE
			// + "non-opped players' ability to edit spells");
			// return true;
			// } else
			if (cmd.getName().equalsIgnoreCase("searchseg") && (!(sender instanceof Player) || sender.isOp())) {
				if (args.length == 0 || args.length > 2) {
					sender.sendMessage(ChatColor.RED + "only accepts 1 argument. /searchseg <key>");
					return true;
				}
				args[0] = args[0].toUpperCase();
				Inventory inv = Bukkit.createInventory(null, 54);

				int x = 0;
				for (int i = 0; i < EventSegment.ALL_SEGMENT_ITEMS.size(); i++) {
					if (EventSegment.ALL_SEGMENT_ITEMS.get(i).getItemMeta().getLocalizedName().toLowerCase().contains(args[0].toLowerCase())) {
						inv.addItem(EventSegment.ALL_SEGMENT_ITEMS.get(i));
						x++;
					}
					if (x >= inv.getSize())
						break;
				}

				for (int i = 0; i < SoundModifier.ALL_SOUNDS.size(); i++) {
					if (SoundModifier.ALL_SOUNDS.get(i).getItemMeta().getLore().get(SoundModifier.SOUND_NAME_INDEX_IN_LORE).contains(args[0])) {
						inv.addItem(SoundModifier.ALL_SOUNDS.get(i));
						x++;
					}
					if (x >= inv.getSize())
						break;
				}

				((Player) sender).openInventory(inv);
				return true;
			} else if (cmd.getName().equalsIgnoreCase("writebuilding") && sender.isOp() && sender instanceof Player) {
				// if (args.length == 0 || args.length > 2) {
				// sender.sendMessage(ChatColor.RED + "only accepts 1 argument.
				// /searchsound <key>");
				// return true;
				// }

				if (args.length < 2) {
					sender.sendMessage(ChatColor.RED + "writebuilding <name> <length> <title (optional)>");
					return true;
				}
				Player p = (Player) sender;
				args[0] = args[0].toLowerCase();

				BuildingData.writeChunk(args[0], p.getLocation(), Integer.parseInt(args[1]));
				return true;
			} else if (cmd.getName().equalsIgnoreCase("loadbuilding") && sender.isOp() && sender instanceof Player) {
				// if (args.length == 0 || args.length > 2) {
				// sender.sendMessage(ChatColor.RED + "only accepts 1 argument.
				// /searchsound <key>");
				// return true;
				// }

				if (args.length < 1) {
					sender.sendMessage(ChatColor.RED + "loadbuilding <name> <rotation in degrees>");
					return true;
				}
				Player p = (Player) sender;
				args[0] = args[0].toLowerCase();
				int rotation = 0;
				try {
					if (args.length > 1)
						rotation = Integer.parseInt(args[1]);
				} catch (Exception er) {
					sender.sendMessage("/loadbuilding <name> <rotaion is degrees e.g 90, 180>");
					return true;
				}
				BuildingData.loadChunk(args[0], p.getLocation(), rotation, 0);
				return true;
			} else if (cmd.getName().equalsIgnoreCase("readprefab") && sender.isOp() && sender instanceof Player) {
				Player p = (Player) sender;
				if (p.getEquipment().getItemInMainHand() != null && p.getEquipment().getItemInMainHand().hasItemMeta()) {
					String text = "\n{{";
					Inventory inv = EventItemStorage.getItemInventory(p.getEquipment().getItemInMainHand());
					for (int i = 0; i <= inv.getSize(); i++) {

						if (i % 9 == 0 && i > 0 && i <= inv.getSize() - 1) {
							text += "}, \n{";
						} else if (i == inv.getSize()) {
							text += "}}";
							break;
						} else if (i > 0) {
							text += ", ";
						}
						ItemStack item = inv.getItem(i);
						if (item != null && item.hasItemMeta()) {
							boolean found = false;
							for (Class<EventSegment> segment : EventSegment.ALL_SEGMENTS) {
								try {
									// Bukkit.broadcastMessage("err seg : " +
									// item.getItemMeta().getLocalizedName() +
									// ". vs "
									// +
									// segment.getField("LOCAL_NAME").get(null).toString());
									if (item.getItemMeta().getLocalizedName().startsWith(segment.getField("LOCAL_NAME").get(null).toString())) {
										text += segment.getSimpleName() + ".getUpdatedItem(new int[2], "
												+ SpellActions.getArrowDirectionFromCustomModel(item) + ")";

										found = true;
										break;
									}
								} catch (Exception er) {
									er.printStackTrace();
								}
							}
							if (!found) {
								if (item.getItemMeta().getLocalizedName().startsWith(OtherItems.singleUpgrade().getItemMeta().getLocalizedName())) {
									text += "OtherItems.singleUpgrade()";
								} else if (item.getItemMeta().getLocalizedName()
										.startsWith(OtherItems.doubleUpgrade().getItemMeta().getLocalizedName())) {
									text += "OtherItems.doubleUpgrade()";
								} else if (item.getItemMeta().getLocalizedName()
										.startsWith(OtherItems.quadrupleUpgrade().getItemMeta().getLocalizedName())) {
									text += "OtherItems.quadrupleUpgrade()";
								} else if (item.getItemMeta().getLocalizedName()
										.startsWith(OtherItems.octupleUpgrade().getItemMeta().getLocalizedName())) {
									text += "OtherItems.octupleUpgrade()";
								} else if (item.getItemMeta().getLocalizedName()
										.startsWith(OtherItems.lockedIndicator().getItemMeta().getLocalizedName())
										|| item.getItemMeta().getLocalizedName()
												.startsWith(OtherItems.triggerIndicator().getItemMeta().getLocalizedName())) {
									text += "null";
								} else
									text += "UNKNOWN";
							}
						} else {
							text += "null";
						}

					}
					try {
						FileWriter f = new FileWriter(Directories.MAIN_PATH + "/" + "latest_prefab.txt");
						f.write(text);
						f.close();
					} catch (Exception er) {
						er.printStackTrace();
					}
					// Bukkit.getLogger().info(text);
				}
				return true;
			}
		}
		return false;
	}

	public static boolean getItems(CommandSender sender, String[] args) {
		Player p = (Player) sender;
		Inventory inv = Bukkit.createInventory(p, 54, ChatColor.GREEN + "Plugin Items");
		int page = 0;
		if (args.length > 0) {
			try {
				page = Math.max(0, Integer.parseInt(args[0]) - 1);
			} catch (Exception er) {
				page = 0;
			}
		}
		sender.sendMessage(ChatColor.WHITE + "Showing page " + ChatColor.GOLD + (page + 1));
		if (page == 0) {
			inv.addItem(OtherItems.singleUpgrade());
			inv.addItem(OtherItems.doubleUpgrade());
			inv.addItem(OtherItems.quadrupleUpgrade());
			inv.addItem(OtherItems.octupleUpgrade());

			inv.addItem(OtherItems.manaPotato());
			inv.addItem(OtherItems.manaBerries());
			inv.addItem(AbstractRPGClass.getClassTotem(p));
			inv.addItem(AbstractRPGClass.getSkillTotem(p, 1));
			inv.addItem(AbstractRPGClass.getNeutralSkillTotem(p, 1));

			inv.addItem(GachaItems.getRandomArmorIndicator());
			inv.addItem(GachaItems.getRandomToolIndicator());
			inv.addItem(GachaItems.getRandomSegmentIndicator());
			for (int i = 0; i < 4; i++) {
				inv.addItem(GachaItems.getRandomArmor(i));
			}
			for (int i = 0; i < 4; i++) {
				inv.addItem(GachaItems.getRandomTool(i));
			}
		}
		ArrayList<ItemStack> allSegments = new ArrayList<ItemStack>();

		ArrayList<ItemStack> tempAllSegments = new ArrayList<ItemStack>();
		tempAllSegments.addAll(EventSegment.ALL_SEGMENT_ITEMS);
		tempAllSegments.addAll(SoundModifier.ALL_SOUNDS);

		int num = 0;
		if (page > 0)
			num = 35 + (page - 1) * 54;
		for (int i = num; i < tempAllSegments.size(); i++) {
			ItemStack item = tempAllSegments.get(i);
			if (!allSegments.contains(item))
				allSegments.add(item);
		}
		for (ItemStack i : allSegments) {
			if (i != null)
				inv.addItem(i);
		}
		p.openInventory(inv);
		return true;
	}

	public static boolean GM(CommandSender sender, String[] args) {
		if (args.length == 0)
			return false;
		Player pToSet = null;
		GameMode gm = null;
		if (args.length == 1)
			pToSet = (Player) sender;
		else {
			String name = args[1];
			for (Player p : Bukkit.getServer().getOnlinePlayers()) {
				if (p.getName().contains(name)) {
					pToSet = p;
					if (p.getName().equals(name))
						break;
				}
			}
		}

		if (args[0].contains("0")) {
			gm = GameMode.SURVIVAL;
		} else if (args[0].contains("1")) {
			gm = GameMode.CREATIVE;
		} else if (args[0].contains("2")) {
			gm = GameMode.ADVENTURE;
		} else if (args[0].contains("3")) {
			gm = GameMode.SPECTATOR;
		}
		if (gm == null || pToSet == null)
			return false;
		pToSet.setGameMode(gm);

		return true;
	}
}
