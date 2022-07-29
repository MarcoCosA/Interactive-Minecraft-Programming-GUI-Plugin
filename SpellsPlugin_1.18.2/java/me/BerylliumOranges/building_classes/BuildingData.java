package me.BerylliumOranges.building_classes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.logging.Level;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.bukkit.Axis;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.block.data.Orientable;
import org.bukkit.block.data.type.Wall;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.bukkit.util.Vector;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import me.BerylliumOranges.building_classes.SpawnedBuilding.EntityPrefabInformation;
import me.BerylliumOranges.misc.Directories;
import me.BerylliumOranges.spellevent.entity_information.entityprefabs.AbstractEntityPrefab;

public class BuildingData implements Serializable {
	private static transient final long serialVersionUID = -1681012206529286330L;

	public final HashMap<Location, String> blockSnapShot;
	public final static String separator = System.lineSeparator();
	public static final BlockFace[] ALL_BLOCK_FACES = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
	public String title = "";

	// Can be used for saving
	public BuildingData(HashMap<Location, String> blockSnapShot) {
		this.blockSnapShot = blockSnapShot;
	}

	// Can be used for loading
	public BuildingData(BuildingData loadedData) {
		this.blockSnapShot = loadedData.blockSnapShot;
	}

	public boolean saveData(String filePath) {
		try {
			BukkitObjectOutputStream out = new BukkitObjectOutputStream(new GZIPOutputStream(new FileOutputStream(filePath)));
			out.writeObject(this);

			out.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static BuildingData loadData(String path) {
		try {
			BukkitObjectInputStream in = new BukkitObjectInputStream(new GZIPInputStream(new FileInputStream(path)));
			BuildingData data = (BuildingData) in.readObject();
			in.close();
			return data;
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			Bukkit.broadcastMessage(ChatColor.RED + "Error: " + e.getLocalizedMessage());
			return null;
		}
	}

	public final static String PATH_WORLD = "world";
	public final static String PATH_SPAWN_FREQUENCY = "spawn-frequency-0-to-100";
	public final static String PATH_BIOMES_DISALLOWED = "disallowed-biomes";
	public final static String PATH_BIOMES_ALLOWED = "allowed-biomes";
	public final static String PATH_SIZE = "size";
	public final static String PATH_SPAWN_OFFSET = "y-spawn-offset";
	public final static String PATH_MAXIMUM_Y = "maximum-y-location";
	public final static String PATH_MINIMUM_Y = "minimum-y-location";
	public final static String PATH_MINIMUM_SPAWN_DISTANCE = "minimum-distance-from-spawn";
	public final static String PATH_TITLE = "title";
	public final static String PATH_SUBTITLE = "subtitle";
	public final static String PATH_APPROACH_WARNING = "warning-distance";
	public final static String PATH_REGENERATE = "regenerate";
	public final static String PATH_GROUNDED = "grounded";
	public final static String PATH_STANDARD_DEVIATION = "maximum-standard-deviation-for-level-differences";

	public final static String SPAWN_INFO_FILE_NAME = "buiding_spawn_info.yml";

	public final static ArrayList<String> DEFAULT_DISALLOWED_BIOMES = loadDisallowedBiomes();

	public static ArrayList<String> loadDisallowedBiomes() {
		ArrayList<String> list = new ArrayList<>();
		List<Biome> biomes = Arrays.asList(Biome.OCEAN, Biome.RIVER, Biome.FROZEN_RIVER);
		for (Biome b : biomes) {
			list.add(b.toString());
		}
		return list;
	}

	public static void saveBuildingInfo(String path, int size) {

		File f = new File(path + "/" + SPAWN_INFO_FILE_NAME);
		YamlConfiguration c;
		if (f.exists()) {
			c = YamlConfiguration.loadConfiguration(f);
			c.set(PATH_SIZE, size);
		} else {
			c = new YamlConfiguration();

			ArrayList<String> worlds = new ArrayList<String>();
			for (World w : Bukkit.getServer().getWorlds()) {
				worlds.add(w.getName());
			}

			c.set(PATH_WORLD, worlds);
			c.set(PATH_SPAWN_FREQUENCY, 0);
			c.set(PATH_BIOMES_DISALLOWED, DEFAULT_DISALLOWED_BIOMES);
			c.set(PATH_BIOMES_ALLOWED, new ArrayList<String>());
			c.set(PATH_SIZE, size);
			c.set(PATH_SPAWN_OFFSET, 1);
			c.set(PATH_MAXIMUM_Y, 200);
			c.set(PATH_MINIMUM_Y, 50);
			c.set(PATH_MINIMUM_SPAWN_DISTANCE, 0);
			c.set(PATH_TITLE, "");
			c.set(PATH_SUBTITLE, "");
			c.set(PATH_APPROACH_WARNING, 40);
			c.set(PATH_REGENERATE, true);
			c.set(PATH_GROUNDED, true);
			c.set(PATH_STANDARD_DEVIATION, 1.0);
		}
		try {
			c.save(new File(path, SPAWN_INFO_FILE_NAME));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void saveInventory(String path, BlockInventoryHolder b, Location loc) {
		YamlConfiguration c = new YamlConfiguration();
		c.set("inventory", b.getInventory().getContents());
		try {
			c.save(new File(path, "inventory[" + loc.getBlockX() + "][" + loc.getBlockY() + "][" + loc.getBlockZ() + "].yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static List<ItemStack> loadInventory(String path, Location loc) {
		Bukkit.broadcastMessage("loading inv: " + "inventory[" + loc.getBlockX() + "][" + loc.getBlockY() + "][" + loc.getBlockZ() + "].yml");
		YamlConfiguration c = YamlConfiguration
				.loadConfiguration(new File(path, "inventory[" + loc.getBlockX() + "][" + loc.getBlockY() + "][" + loc.getBlockZ() + "].yml"));
		return (List<ItemStack>) c.get("inventory");
	}

	public static void writeChunk(String name, Location loc, int dim) {
		String path = Directories.BUILDINGS_PATH + name;
		HashMap<Location, String> blockSnapShot = new HashMap<Location, String>();

		File[] listOfFiles = (new File(path)).listFiles();
		if (listOfFiles != null)
			for (File f : listOfFiles) {
				if (f.getName().endsWith("].yml")) {
					f.delete();
				}
			}

		int yMod = dim * 2;
		Location temp;
		int x, y, z;
		int xLoc = loc.getBlockX();
		int yLoc = loc.getBlockY() + dim - 1 + yMod;
		int zLoc = loc.getBlockZ() - 2;
		boolean found = false;
		for (y = 0; y < dim + yMod; y++) {
			for (x = 0; x < dim; x++) {
				for (z = 0; z < dim; z++) {
					temp = new Location(loc.getWorld(), xLoc - x, yLoc - y, zLoc - z);
					if ((x == dim - 1 || z == dim - 1 || x == 0 || z == 0 || y == 0 || y == dim - 1)) {
						temp.getWorld().spawnParticle(Particle.REDSTONE, temp.getX() + 0.5, temp.getY() + 0.5, temp.getZ() + 0.5, 0, 0, 0, 0, 0,
								new DustOptions(Color.RED, 10F), true);
						Bukkit.broadcastMessage("im uuu partciel");
					}

					if (!found && !temp.getBlock().getType().equals(Material.AIR)) {
						found = true;
					}
					if (found) {
						if (temp.getBlock().getState() instanceof BlockInventoryHolder) {
							Bukkit.broadcastMessage("ITS AN INVENTORY HOLDER!!!!");
							saveInventory(path, (BlockInventoryHolder) temp.getBlock().getState(), temp.getBlock().getLocation());
						}
						blockSnapShot.put(temp, temp.getBlock().getBlockData().getAsString());
					}
				}
			}
		}

		new File(path).mkdir();
		BuildingData data = new BuildingData(blockSnapShot);
		data.saveData(path + "/" + name + ".data");
		saveBuildingInfo(path, dim);
		Bukkit.getServer().getLogger().log(Level.INFO, "Chunk \"" + name + "\" saved");
	}

	public static boolean loadChunk(String name, Location loc1, int rotation, int dontReplaceAirTillThisYOffset) {
		ArrayList<EntityPrefabInformation> prefabs = new ArrayList<EntityPrefabInformation>();

		BuildingType buildingType = BuildingType.buildingTypeFromName(name);
		String path = Directories.BUILDINGS_PATH + name;
		Location loc = loc1.clone();
		rotation = ((Math.abs(rotation) / 90) * 90) % 270;
		double rotationInRadians = rotation * (Math.PI / 180.0);
		int mod = 1;

		BuildingData data = new BuildingData(BuildingData.loadData(path + "/" + name + ".data"));
		int dim = buildingType.getSize();
		Bukkit.broadcastMessage("dim is " + buildingType.getSize());
		Iterator<Entry<Location, String>> findIterator = data.blockSnapShot.entrySet().iterator();
		Iterator<Entry<Location, String>> mainIterator = data.blockSnapShot.entrySet().iterator();

		if (rotation == 90) {
			loc.subtract(dim - mod, 0, 0);
		} else if (rotation == 180) {
			loc.subtract(dim - mod, 0, dim - mod);
		} else if (rotation == 270) {
			loc.subtract(0, 0, dim - mod);
		}

		int currentX, currentY, currentZ;
		int referenceX = Integer.MIN_VALUE;
		int referenceY = Integer.MAX_VALUE;
		int referenceZ = Integer.MIN_VALUE;
		Location fL = null;
		while (findIterator.hasNext()) {
			Map.Entry<Location, String> possLocations = (Map.Entry<Location, String>) findIterator.next();
			referenceX = Math.max(possLocations.getKey().getBlockX(), referenceX);
			referenceY = Math.min(possLocations.getKey().getBlockY(), referenceY);
			referenceZ = Math.max(possLocations.getKey().getBlockZ(), referenceZ);
		}
		ArrayList<BlockFace> faces = new ArrayList<BlockFace>();
		while (mainIterator.hasNext()) {
			Map.Entry<Location, String> mapElement = (Map.Entry<Location, String>) mainIterator.next();

			Block b1 = mapElement.getKey().getBlock();
			currentX = b1.getX();
			currentY = b1.getY();
			currentZ = b1.getZ();

			int newX = (int) (referenceX + (currentX - referenceX) * Math.cos(rotationInRadians)
					- (currentZ - referenceZ) * Math.sin(rotationInRadians));
			int newZ = (int) (referenceZ + (currentX - referenceX) * Math.sin(rotationInRadians)
					+ (currentZ - referenceZ) * Math.cos(rotationInRadians));

			fL = loc.clone().add(newX - referenceX, currentY - referenceY, newZ - referenceZ);

			BlockData blockData = Bukkit.getServer().createBlockData(mapElement.getValue());
			String beforeMat = fL.getBlock().getType().toString().toUpperCase();
			if (!blockData.getMaterial().isAir() || (beforeMat.contains("LOG") || beforeMat.contains("FLOWER") || beforeMat.contains("WOOD")
					|| beforeMat.contains("MUSHROOM") || beforeMat.contains("LEAVE")
					|| (beforeMat.contains("GRASS") && !fL.getBlock().getType().equals(Material.GRASS_BLOCK)))) {
				fL.getBlock().setBlockData(blockData);

				Block b = fL.getBlock();

				if (blockData instanceof Directional) {
					Directional directional = (Directional) blockData;
					directional.setFacing(getRotatedFace(rotation, directional.getFacing()));
					b.setBlockData(directional);
				} else if (blockData instanceof Orientable) {
					Orientable orientable = (Orientable) blockData;
					if (orientable.getAxis().equals(Axis.X) || orientable.getAxis().equals(Axis.Z)) {
						orientable.setAxis(getRotatedAxis(rotation, orientable.getAxis()));
						b.setBlockData(orientable);
					}
				}
				if (blockData instanceof Wall) {
					Material mat = b.getType();
					b.setType(Material.AIR);
					b.setType(mat);

				} else if (blockData instanceof MultipleFacing) {
					MultipleFacing attachable = (MultipleFacing) blockData;
					if (attachable.getFaces() != null && attachable.getFaces().size() > 0) {
						faces.clear();
						int rot = rotation;
						attachable.getFaces().forEach(face -> faces.add(getRotatedFace(rot, face)));
						for (BlockFace f : ALL_BLOCK_FACES) {
							attachable.setFace(f, faces.contains(f));
						}
						b.setBlockData(attachable);
					}
				}

				if (b.getState() instanceof BlockInventoryHolder) {
					BlockInventoryHolder c = (BlockInventoryHolder) b.getState();
					Bukkit.broadcastMessage("found inventory, path: " + path);
					List<ItemStack> items = loadInventory(path + "/", b1.getLocation());
					if (b.getType().equals(Material.CHEST)) {
						ArrayList<ItemStack> possibleSpawns = new ArrayList<ItemStack>();
						for (int i = 0; i < items.size(); i++) {
							ItemStack item = items.get(i);
							if (item != null && (i == 0 || possibleSpawns.size() > 0)) {
								Bukkit.broadcastMessage("im checking for EGG: " + item);
								if (item.getItemMeta() instanceof SpawnEggMeta) {
									possibleSpawns.add(item);
								}
							} else
								break;
						}
						if (possibleSpawns.size() > 0) {
							b.setType(Material.AIR);
							ItemStack item = possibleSpawns.get(generateRand(possibleSpawns.size(), 0));
							AbstractEntityPrefab prefab = AbstractEntityPrefab.getEntityPrefab(item);
							if (prefab != null) {
								prefabs.add(new EntityPrefabInformation(prefab, b.getLocation(), null));
							} else {
								EntityType type = EntityType.valueOf(item.getType().toString().toUpperCase()
										.substring(0, item.getType().toString().toUpperCase().indexOf("_SPAWN_EGG")).trim());
								prefabs.add(new EntityPrefabInformation(null, b.getLocation(), type));
							}

							b.getState().update();
						} else {
							for (int i = 0; i < items.size(); i++) {
								ItemStack item = items.get(i);
								if (item != null && i > 9) {
									if (generateRand(i - 8, 0) != 1) {
										items.set(i, null);
									}
								}
							}
							Collections.shuffle(items);
						}
						for (int i = 0; i < items.size(); i++) {
							ItemStack item = items.get(i);
							if (item != null)
								c.getInventory().setItem(i, item);
						}

					} else if (!b.getType().equals(Material.CHEST)) {
						for (int i = 0; i < items.size(); i++) {
							ItemStack item = items.get(i);
							if (item != null) {
								c.getInventory().setItem(i, item);
							}
						}
					}
				}
			}
		}
		// b.getState().setBlockData((BlockData) inv);

		// Chest chest = (Chest) blockData;
		// Chest c = (Chest) b.getState();
		// for (ItemStack item : chest.getBlockInventory()) {
		// c.getBlockInventory().addItem(item);
		// }

		if (buildingType != null) {
			Bukkit.broadcastMessage("DIMENSION IS " + dim + ", or " + buildingType.getSize());
			Vector v = new Vector(-dim / 2, dim / 4, -dim / 2);
			v.rotateAroundY(rotationInRadians);
			Location center = loc.clone();
			if (rotation == 90) {
				center.add(dim / 2, dim / 4, -dim / 2);
			} else {
				center.add(v);
			}
			SpawnedBuilding spawned = new SpawnedBuilding(buildingType, prefabs, center);
			// Bukkit.broadcastMessage("added " + (-dim *
			// Math.cos(rotationInRadians)) + " x and " + (-dim *
			// Math.cos(rotationInRadians)) + " y");
			loc1.getBlock().setType(Material.NETHERRACK);
			spawned.spawnEntities();
		}
		Bukkit.getServer().getLogger().log(Level.INFO, "Chunk \"" + name + "\" loaded");
		if (fL == null)
			return false;
		return true;
	}

	public static BlockFace getRotatedFace(int rotation, BlockFace face) {
		return intToFace((((rotation / 90) + BuildingData.faceToInt(face)) % 4));
	}

	public static Axis getRotatedAxis(int rotation, Axis axis) {
		return intToAxis((((rotation / 90) + BuildingData.axisToInt(axis)) % 2));
	}

	public static int faceToInt(BlockFace face) {
		switch (face) {
		case NORTH:
			return 0;
		case EAST:
			return 1;
		case SOUTH:
			return 2;
		case WEST:
			return 3;
		default:
			return 0;
		}
	}

	public static BlockFace intToFace(int faceAsInt) {
		switch (faceAsInt) {
		case 0:
			return BlockFace.NORTH;
		case 1:
			return BlockFace.EAST;
		case 2:
			return BlockFace.SOUTH;
		case 3:
			return BlockFace.WEST;
		default:
			return BlockFace.NORTH;
		}
	}

	public static Axis intToAxis(int axisAsInt) {
		if (axisAsInt == 0)
			return Axis.X;
		return Axis.Z;
	}

	public static int axisToInt(Axis axis) {
		if (axis.equals(Axis.X))
			return 0;
		return 1;
	}

	public static File[] getAllChunks() {
		File[] listOfFiles = (new File("plugins/CityBuilder/Chunks")).listFiles();
		return listOfFiles;
	}

	public static Inventory getAllChunksAsInventory() {
		File[] listOfFiles = (new File("plugins/CityBuilder/Chunks")).listFiles();
		int size = Math.max(9, ((listOfFiles.length + 8) / 9) * 9);
		if (size > 54)
			size = 54;
		Inventory i = Bukkit.createInventory(null, size, ChatColor.DARK_GREEN + "Chunks");
		ItemStack item = new ItemStack(Material.NETHERITE_SCRAP);
		ItemMeta meta = item.getItemMeta();
		for (File f : listOfFiles) {
			for (File data : (new File("plugins/CityBuilder/Chunks/" + f.getName())).listFiles()) {
				if (data.getName().indexOf(".data") > 0) {
					meta.setDisplayName(ChatColor.GREEN + "Chunk: " + ChatColor.YELLOW + "\""
							+ data.getName().substring(0, data.getName().indexOf(".data")) + "\"");
					item.setItemMeta(meta);
					i.addItem(item);
				}
			}
		}
		return i;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public static int generateRand(int max, int min) {
		return (new Random()).nextInt(max) + min;
	}
}
