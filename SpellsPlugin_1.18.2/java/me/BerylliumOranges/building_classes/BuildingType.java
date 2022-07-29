package me.BerylliumOranges.building_classes;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.configuration.file.YamlConfiguration;

import me.BerylliumOranges.misc.Directories;

public class BuildingType implements Serializable {
	private static final long serialVersionUID = -4703141250044069429L;

	private String buildingName;
	private ArrayList<World> worlds;
	private int spawnFrequency;
	private ArrayList<Biome> disallowedBiomes;
	private ArrayList<Biome> allowedBiomes;
	private int size;
	private int spawnOffset;
	private int maximumY;
	private int minimumY;
	private int minimumSpawnDistance;
	private String title;
	private String subtitle;
	private int approachWarningDistance;
	private boolean regernate;
	private boolean grounded;
	private double standardDeviation;

	public static ArrayList<BuildingType> buildings = loadBuildings();

	public static void loadClass() {
		buildings.isEmpty();
	}

	public static BuildingType buildingTypeFromName(String name) {
		for (BuildingType b : buildings) {
			if (b.getBuildingName().equals(name)) {
				return b;
			}
		}
		return null;
	}

	public static ArrayList<BuildingType> loadBuildings() {
		Bukkit.getLogger().info("Loading Buildings");
		ArrayList<BuildingType> buildings = new ArrayList<>();
		buildings.clear();

		File[] listOfFiles = (new File(Directories.BUILDINGS_PATH)).listFiles();
		if (listOfFiles != null) {
			for (File f : listOfFiles) {
				if (f.isDirectory()) {

					try {
						File info = new File(f.getPath() + "/" + BuildingData.SPAWN_INFO_FILE_NAME);
						if (info.canRead()) {
							BuildingType b = new BuildingType();
							YamlConfiguration c = YamlConfiguration.loadConfiguration(info);
							b.setBuildingName(f.getName());
							ArrayList<String> ws = new ArrayList<>(c.getStringList(BuildingData.PATH_WORLD));
							ArrayList<World> worlds = new ArrayList<World>();
							for (String s : ws) {
								try {
									Bukkit.getLogger().info("HEEEEEEEEERE " + s + ", size " + Bukkit.getServer().getWorlds().size());
									Bukkit.getLogger().info("worlds " + Bukkit.getServer().getWorlds());
									for (World w : Bukkit.getServer().getWorlds()) {
										Bukkit.getLogger().info("World name " + w.getName());
										if (w.getName().toLowerCase().equals(s.toLowerCase())) {
											worlds.add(w);
											break;
										}
									}
								} catch (Exception er) {
									er.printStackTrace();
									Bukkit.getLogger().info("Error reading worlds from " + f.getName());
								}
							}
							b.setWorlds(worlds);
							b.setSpawnFrequency(c.getInt(BuildingData.PATH_SPAWN_FREQUENCY));

							{
								ArrayList<String> bs = new ArrayList<>(c.getStringList(BuildingData.PATH_BIOMES_DISALLOWED));
								ArrayList<Biome> biomes = new ArrayList<Biome>();
								for (String s : bs) {
									try {
										Biome w = Biome.valueOf(s);
										if (w == null) {
											throw new IllegalArgumentException(s);
										}
										biomes.add(w);
									} catch (Exception er) {
										er.printStackTrace();
										Bukkit.getLogger().info("Error reading disallowed biomes from " + f.getName());
									}
								}
								b.setDisallowedBiomes(biomes);
							}
							{
								ArrayList<String> bs = new ArrayList<>(c.getStringList(BuildingData.PATH_BIOMES_ALLOWED));
								ArrayList<Biome> biomes = new ArrayList<Biome>();
								for (String s : bs) {
									try {
										Biome w = Biome.valueOf(s);
										if (w == null) {
											throw new IllegalArgumentException(s);
										}
										biomes.add(w);
									} catch (Exception er) {
										er.printStackTrace();
										Bukkit.getLogger().info("Error reading allowed biomes from " + f.getName());
									}
								}
								b.setAllowedBiomes(biomes);
							}

							b.setSize(c.getInt(BuildingData.PATH_SIZE));
							b.setSpawnOffset(c.getInt(BuildingData.PATH_SPAWN_OFFSET));
							b.setMaximumY(c.getInt(BuildingData.PATH_MAXIMUM_Y));
							b.setMinimumY(c.getInt(BuildingData.PATH_MINIMUM_Y));
							b.setMinimumSpawnDistance(c.getInt(BuildingData.PATH_MINIMUM_SPAWN_DISTANCE));
							b.setTitle(c.getString(BuildingData.PATH_TITLE));
							b.setSubtitle(c.getString(BuildingData.PATH_SUBTITLE));
							b.setApproachWarningDistance(c.getInt(BuildingData.PATH_APPROACH_WARNING));
							b.setRegernate(c.getBoolean(BuildingData.PATH_REGENERATE));
							b.setGrounded(c.getBoolean(BuildingData.PATH_GROUNDED));
							b.setStandardDeviation(c.getDouble(BuildingData.PATH_STANDARD_DEVIATION));
							buildings.add(b);
						}
					} catch (Exception er) {
						er.printStackTrace();
						Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error reading " + f.getName());
					}
				}
			}
		}
		return buildings;
	}

	public String getBuildingName() {
		return buildingName;
	}

	public ArrayList<World> getWorlds() {
		return worlds;
	}

	public int getSpawnFrequency() {
		return spawnFrequency;
	}

	public ArrayList<Biome> getDisallowedBiomes() {
		return disallowedBiomes;
	}

	public ArrayList<Biome> getAllowedBiomes() {
		return allowedBiomes;
	}

	public int getSize() {
		return size;
	}

	public int getSpawnOffset() {
		return spawnOffset;
	}

	public int getMaximumY() {
		return maximumY;
	}

	public int getMinimumY() {
		return minimumY;
	}

	public int getMinimumSpawnDistance() {
		return minimumSpawnDistance;
	}

	public int getMinimumSpawnDistanceSquared() {
		return minimumSpawnDistance * minimumSpawnDistance;
	}

	public String getTitle() {
		return title;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public int getApproachWarningDistance() {
		return approachWarningDistance;
	}

	public boolean isRegernate() {
		return regernate;
	}

	public boolean isGrounded() {
		return grounded;
	}

	public double getStandardDeviation() {
		return standardDeviation;
	}

	public static ArrayList<BuildingType> getBuildings() {
		return buildings;
	}

	protected void setBuildingName(String buildingName) {
		this.buildingName = buildingName;
	}

	protected void setWorlds(ArrayList<World> worlds) {
		this.worlds = worlds;
	}

	protected void setSpawnFrequency(int spawnFrequency) {
		this.spawnFrequency = spawnFrequency;
	}

	protected void setDisallowedBiomes(ArrayList<Biome> disallowedBiomes) {
		this.disallowedBiomes = disallowedBiomes;
	}

	protected void setAllowedBiomes(ArrayList<Biome> allowedBiomes) {
		this.allowedBiomes = allowedBiomes;
	}

	protected void setSize(int size) {
		this.size = size;
	}

	protected void setSpawnOffset(int spawnOffset) {
		this.spawnOffset = spawnOffset;
	}

	protected void setMaximumY(int maximumY) {
		this.maximumY = maximumY;
	}

	protected void setMinimumY(int minimumY) {
		this.minimumY = minimumY;
	}

	protected void setMinimumSpawnDistance(int minimumSpawnDistance) {
		this.minimumSpawnDistance = minimumSpawnDistance;
	}

	protected void setTitle(String title) {
		this.title = title;
	}

	protected void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	protected void setApproachWarningDistance(int approachWarningDistance) {
		this.approachWarningDistance = approachWarningDistance;
	}

	protected void setRegernate(boolean regernate) {
		this.regernate = regernate;
	}

	protected void setGrounded(boolean grounded) {
		this.grounded = grounded;
	}

	protected void setStandardDeviation(double standardDeviation) {
		this.standardDeviation = standardDeviation;
	}

	protected static void setBuildings(ArrayList<BuildingType> buildings) {
		BuildingType.buildings = buildings;
	}
}