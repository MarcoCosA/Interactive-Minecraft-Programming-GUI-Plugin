package me.BerylliumOranges.building_classes;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.plugin.Plugin;

import me.BerylliumOranges.building_classes.SpawnedBuilding.EntityPrefabInformation;
import me.BerylliumOranges.misc.ConfigInfo;
import net.md_5.bungee.api.ChatColor;

public class SpawningListener implements Listener {
	public static final int MAX_CHUNKS_TILL_SPAWN = 100;
	public static int chunkCounter = MAX_CHUNKS_TILL_SPAWN / 2;
	// public static int chunkCounter = 0;

	public SpawningListener(Plugin plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	// @EventHandler
	// public void onClick(ChunkUnloadEvent e) {
	// for (SpawnedBuilding b : SpawnedBuilding.spawnedBuildings) {
	// if (b.getChunk().equals(e.getChunk())) {
	// b.removeEntities();
	// }
	// }
	// }

	@EventHandler
	public void onChunkLoad(ChunkLoadEvent e) {

		for (SpawnedBuilding b : SpawnedBuilding.spawnedBuildings) {
			if (b.getChunk().equals(e.getChunk())) {
				b.spawnEntities();
			}
		}

		// Bukkit.broadcastMessage("Im running " +
		// ConfigInfo.isSpawnDungeons());
		if (ConfigInfo.isSpawnDungeons()) {
			// Bukkit.broadcastMessage("is true yea " +
			// BuildingType.buildings.size() + ", " + chunkCounter);
			if (e.isNewChunk()) {
				if (chunkCounter >= MAX_CHUNKS_TILL_SPAWN) {
					Block b = e.getWorld().getHighestBlockAt(e.getChunk().getBlock(8, 8, 8).getLocation());
					ArrayList<Biome> biomes = new ArrayList<Biome>();

					types_loop: for (BuildingType t : BuildingType.buildings) {
						Bukkit.broadcastMessage("Checking " + t.getBuildingName());
						if (t.getDisallowedBiomes().contains(Biome.OCEAN) && b.getBiome().toString().toLowerCase().contains("ocean")) {
							Bukkit.broadcastMessage("continued on ocean check");
							continue;
						}
						if (!t.getWorlds().contains(b.getWorld())
								|| b.getLocation().distanceSquared(b.getWorld().getSpawnLocation()) < t.getMinimumSpawnDistanceSquared()) {
							Bukkit.broadcastMessage("continued on spawn check " + t.getWorlds());
							continue;
						}

						Location temp = b.getLocation().clone();
						temp.setY(100);
						temp.getBlock().setType(Material.GOLD_BLOCK);

						Bukkit.broadcastMessage("interval " + (360 / Math.sqrt(t.getSize())));
						chunkCounter -= 5 + Math.sqrt(t.getSize());
						int total = (int) Math.sqrt(t.getSize()) * 2;
						int maxY = Integer.MIN_VALUE;
						double[] ys = new double[total];
						for (int x = 0; x < total; x++) {

							double angle = 2 * Math.PI * (x / (double) total);
							Bukkit.broadcastMessage("angle is " + angle + ", max is " + total);
							// try {
							Bukkit.broadcastMessage("Block loc: " + (Math.cos(angle) * t.getSize() / 2));
							Location loc = b.getLocation().clone().add(Math.cos(angle) * t.getSize() / 2, 0, Math.sin(angle) * t.getSize() / 2);
							Block block = b.getWorld().getHighestBlockAt(loc);

							while (loc.getY() > 0 && (block.getType().toString().contains("LEAVE") || block.getType().toString().contains("LOG")
									|| block.getType().toString().contains("MUSHROOM"))) {
								loc.subtract(0, 1, 0);
								block = loc.getBlock();
							}
							if (loc.getY() == 0)
								continue;

							if (t.getAllowedBiomes().isEmpty()) {
								if (t.getDisallowedBiomes().contains(block.getBiome()) || block.getType().equals(Material.WATER)) {
									Bukkit.broadcastMessage("continued biome check");
									continue types_loop;
								}
							} else {
								if (!t.getAllowedBiomes().contains(block.getBiome())) {
									Bukkit.broadcastMessage("continued on allowed biome check");
									continue types_loop;
								}
							}
							ys[x] = block.getY();
							if (block.getY() > maxY)
								maxY = block.getY();

							loc.setY(100);
							loc.getBlock().setType(Material.REDSTONE_BLOCK);
						}
						Location spawnLoc = b.getLocation().clone();
						if (t.isGrounded()) {
							Bukkit.broadcastMessage("Standard Deviation is " + calculateSD(ys) + ", max is " + t.getStandardDeviation());
							if (calculateSD(ys) > t.getStandardDeviation()) {
								Bukkit.broadcastMessage("continued on terrain check ");
								continue types_loop;
							}

							spawnLoc.setY(maxY - t.getSpawnOffset() + 1);

							if (spawnLoc.getY() > t.getMaximumY() || spawnLoc.getY() < t.getMinimumY()) {
								Bukkit.broadcastMessage("continued y check " + spawnLoc.getY());
								continue types_loop;
							}
						} else {
							int rnd = (int) (Math.random() * (t.getMaximumY() - t.getMinimumY()));
							rnd += t.getSpawnOffset();
							rnd = Math.min(Math.max(0, rnd), b.getWorld().getMaxHeight() - 20);
							spawnLoc.setY(rnd);
						}
						// }
						// } catch (Exception er) {
						// er.printStackTrace();
						// }
						// }

						// if (t.isGrounded()) {
						// if (num < t.getMaximumY() || num > t.getMaximumY()) {
						// Bukkit.broadcastMessage("continued y check " + num);
						// continue types_loop;
						// }
						// } else {
						// int rnd = (int) (Math.random() * (t.getMaximumY() -
						// t.getMinimumY()));
						// rnd += t.getSpawnOffset();
						// rnd = Math.min(Math.max(0, rnd),
						// b.getWorld().getMaxHeight() - 20);
						// spawnLoc.setY(rnd);
						// }
						Bukkit.broadcastMessage(ChatColor.GREEN + "i'm SPAWNING " + t.getBuildingName());
						spawnLoc.add(t.getSize() / 2.0, 0, t.getSize() / 2.0);
						BuildingData.loadChunk(t.getBuildingName(), spawnLoc, (int) (Math.random() * 361), t.getSpawnOffset());
						chunkCounter = 0;
						b.getLocation().clone().add(0, 10, 0).getBlock().setType(Material.DIAMOND_BLOCK);
						break types_loop;
					}
				}
				if (chunkCounter < MAX_CHUNKS_TILL_SPAWN)
					chunkCounter++;
			}
		}

	}

	public static double calculateSD(double numArray[]) {
		double sum = 0.0, standardDeviation = 0.0;
		int length = numArray.length;
		String s = "";

		for (double num : numArray) {
			s += num + ", ";
			sum += num;
		}
		Bukkit.broadcastMessage("adding num " + s);

		double mean = sum / length;

		for (double num : numArray) {
			standardDeviation += Math.pow(num - mean, 2);
		}

		return Math.sqrt(standardDeviation / length);
	}
}
