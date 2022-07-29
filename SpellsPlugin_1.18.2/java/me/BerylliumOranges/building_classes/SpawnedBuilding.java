package me.BerylliumOranges.building_classes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.BerylliumOranges.main.SpellPluginMain;
import me.BerylliumOranges.misc.Directories;
import me.BerylliumOranges.spellevent.entity_information.EntityStats;
import me.BerylliumOranges.spellevent.entity_information.entityprefabs.AbstractEntityPrefab;
import me.BerylliumOranges.spellevent.entity_information.entityprefabs.bosses.AbstractBoss;

public class SpawnedBuilding implements Serializable {
	private static final long serialVersionUID = -1140465146623041565L;

	public transient HashMap<Player, Integer> warnings = new HashMap<>();

	public static ArrayList<SpawnedBuilding> spawnedBuildings = new ArrayList<>();
	private BuildingType buildingType;
	private transient ArrayList<LivingEntity> spawnedEntities = new ArrayList<>();
	private ArrayList<EntityPrefabInformation> dungeonEntities = new ArrayList<>();
	private String uniqueID;
	private double x, y, z;
	private String world;
	public Chunk chunk;
	private transient AbstractBoss bossType = null;
	private transient LivingEntity boss = null;

	public SpawnedBuilding(BuildingType buildingType, ArrayList<EntityPrefabInformation> dungeonEntities, Location location) {
		this.dungeonEntities = dungeonEntities;
		this.buildingType = buildingType;
		uniqueID = UUID.randomUUID().toString();

		world = location.getWorld().getName();
		x = location.getX();
		y = location.getY();
		z = location.getZ();
		chunk = location.getChunk();
		spawnedBuildings.add(this);
	}

	public static void tick() {
		for (SpawnedBuilding spawn : spawnedBuildings) {
			spawn.tickSelf();
		}
	}

	public void spawnEntities() {
		Bukkit.broadcastMessage("Spawning entities: " + dungeonEntities.size());
		for (EntityPrefabInformation a : dungeonEntities) {
			if (a.getPrefab() != null) {
				LivingEntity liv = a.getPrefab().spawnEntity(a.getLocation());
				spawnedEntities.add(liv);
			} else {
				Entity ent = a.getLocation().getWorld().spawnEntity(a.getLocation().clone().add(0.5, 0.2, 0.5), a.getType());
				if (ent instanceof LivingEntity)
					spawnedEntities.add((LivingEntity) ent);
			}
		}
	}

	public void removeEntities() {
		Bukkit.broadcastMessage("removing entities: " + spawnedEntities.size());
		for (LivingEntity ent : spawnedEntities) {
			ent.remove();
		}
		spawnedEntities.clear();
	}

	public boolean spawned = true;

	public void tickSelf() {
		boolean found = false;
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			if (p.getLocation().distanceSquared(getLocation()) < 10000) {
				found = true;
				break;
			}
		}
		if (!found && spawned) {
			spawned = false;
			removeEntities();
		} else if (found && !spawned) {
			spawned = true;
			spawnEntities();
		}

		if (!buildingType.getTitle().isEmpty()) {
			for (Player p : Bukkit.getServer().getOnlinePlayers()) {
				if (p.getWorld().equals(this.getLocation().getWorld())) {
					if (p.getLocation().distanceSquared(this.getLocation()) < buildingType.getApproachWarningDistance()
							* buildingType.getApproachWarningDistance()) {
						if (!warnings.containsKey(p)) {
							EntityStats stats = EntityStats.getEntityStats(p);
							stats.dontSendTitleTime = 80;
							p.sendTitle(buildingType.getTitle(), buildingType.getSubtitle(), 5, 30, 5);
						}
						warnings.put(p, 2400);
					}
				}
			}
		}
	}

	public BuildingType getBuildingType() {
		return buildingType;
	}

	public void setBuildingType(BuildingType buildingType) {
		this.buildingType = buildingType;
	}

	public ArrayList<LivingEntity> getSpawnedEntities() {
		return spawnedEntities;
	}

	public void setSpawnedEntities(ArrayList<LivingEntity> spawnedEntities) {
		this.spawnedEntities = spawnedEntities;
	}

	public static void writeSpawnedBuildings() {
		Directories.clearFolder(Directories.SPAWNED_BUILDINGS_FILE);
		for (int i = 0; i < spawnedBuildings.size(); i++) {
			spawnedBuildings.get(i).writeSpawnedBuilding();
		}
	}

	public void writeSpawnedBuilding() {
		try {
			FileOutputStream fileOut = new FileOutputStream(Directories.SPAWNED_BUILDINGS_FILE + getUniqueID());
			ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
			objectOut.writeObject(this);
			objectOut.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void loadSpawnedBuildings() {
		ObjectInputStream objectinputstream = null;
		try {
			for (File f : Directories.SPAWNED_BUILDINGS_FILE.listFiles()) {
				FileInputStream streamIn = new FileInputStream(f);
				objectinputstream = new ObjectInputStream(streamIn);
				SpawnedBuilding stat = (SpawnedBuilding) objectinputstream.readObject();
				spawnedBuildings.add(stat);
			}
		} catch (Exception e) {
			// e.printStackTrace();
		} finally {
			if (objectinputstream != null) {
				try {
					objectinputstream.close();
				} catch (IOException e) {
					// e.printStackTrace();
				}
			}
		}
	}

	public void destroyBuilding() {
		spawnedBuildings.remove(this);
		for (int i = spawnedEntities.size() - 1; i >= 0; i--) {
			spawnedEntities.get(i).damage(1);
			spawnedEntities.get(i).setHealth(0);
		}
		dungeonEntities.clear();
		new BukkitRunnable() {
			int count = 0;
			int lastExplosion = 0;
			int lastBigExplosion = 0;
			final int bigExplosionMax = 600;

			public void run() {
				count++;
				lastExplosion--;
				lastBigExplosion--;
				if (count > 800) {
					this.cancel();
				} else if (count > bigExplosionMax) {
					if (lastBigExplosion <= 0) {
						randomExplosion(2.5F);
						lastBigExplosion += Math.random() * 40 * (bigExplosionMax / (count * 2));
					}
				}
				if (count > 100 && count < 300) {
					if (lastExplosion <= 0) {
						randomExplosion(0.5F);
						lastExplosion += Math.random() * 20;
					}
				}
			}
		}.runTaskTimer(SpellPluginMain.getInstance(), 0, 0);
	}

	private void randomExplosion(float size) {
		double x = Math.random() * buildingType.getSize() / (Math.random() > 0.5 ? -2.0 : 2.0);
		double y = Math.random() * buildingType.getSize() / (Math.random() > 0.5 ? -2.0 : 2.0);
		double z = Math.random() * buildingType.getSize() / (Math.random() > 0.5 ? -2.0 : 2.0);
		getLocation().getWorld().createExplosion(getLocation().add(x, y, z), size);
	}

	public void unloadSelf() {
		removeEntities();
		// for (int i = spawnedEntities.size() - 1; i >= 0; i--) {
		// spawnedEntities.get(i).remove();
		// }
	}

	public void loadSelf() {
		for (EntityPrefabInformation prefab : dungeonEntities) {
			LivingEntity liv = prefab.getPrefab().spawnEntity(prefab.getLocation());
			if (prefab.getPrefab().equals(bossType)) {

			}
			spawnedEntities.add(liv);
		}
	}

	public static void loadChunkWithBuildings(Chunk chunk) {
		// for (SpawnedBuilding s : spawnedBuildings) {
		// if (s.getChunk().equals(chunk)) {
		// s.loadSelf();
		// }
		// }
	}

	public static ArrayList<SpawnedBuilding> getSpawnedBuildings() {
		return spawnedBuildings;
	}

	public static void setSpawnedBuildings(ArrayList<SpawnedBuilding> spawnedBuildings) {
		SpawnedBuilding.spawnedBuildings = spawnedBuildings;
	}

	public String getUniqueID() {
		return uniqueID;
	}

	public void setUniqueID(String uniqueID) {
		this.uniqueID = uniqueID;
	}

	public Location getLocation() {
		new Location(Bukkit.getWorld(world), x, y, z).getBlock().setType(Material.GOLD_ORE);
		return new Location(Bukkit.getWorld(world), x, y, z);
	}

	public Chunk getChunk() {
		return chunk;
	}

	public ArrayList<EntityPrefabInformation> getDungeonEntities() {
		return dungeonEntities;
	}

	public void setDungeonEntities(ArrayList<EntityPrefabInformation> dungeonEntities) {
		this.dungeonEntities = dungeonEntities;
	}

	public LivingEntity getBoss() {
		return boss;
	}

	public void setBoss(LivingEntity boss) {
		this.boss = boss;
	}

	public AbstractBoss getBossType() {
		return bossType;
	}

	public void setBossType(AbstractBoss bossType) {
		this.bossType = bossType;
	}

	public static class EntityPrefabInformation implements Serializable {
		private static final long serialVersionUID = -9058382712813598613L;

		private double x, y, z;
		public String world;

		public EntityType type = null;
		public AbstractEntityPrefab prefab;

		public EntityPrefabInformation(AbstractEntityPrefab prefab, Location loc, EntityType type) {
			world = loc.getWorld().getName();
			x = loc.getX();
			y = loc.getY();
			z = loc.getZ();

			this.prefab = prefab;
			this.type = type;
		}

		public AbstractEntityPrefab getPrefab() {
			return prefab;
		}

		public void setPrefab(AbstractEntityPrefab prefab) {
			this.prefab = prefab;
		}

		public Location getLocation() {
			return new Location(Bukkit.getWorld(world), x, y, z);
		}

		public EntityType getType() {
			return type;
		}

		public void setType(EntityType type) {
			this.type = type;
		}
	}
}