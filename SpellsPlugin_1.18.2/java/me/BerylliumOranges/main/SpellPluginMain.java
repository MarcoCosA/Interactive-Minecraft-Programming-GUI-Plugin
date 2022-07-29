package me.BerylliumOranges.main;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import me.BerylliumOranges.building_classes.BuildingType;
import me.BerylliumOranges.building_classes.SpawnedBuilding;
import me.BerylliumOranges.building_classes.SpawningListener;
import me.BerylliumOranges.listeners.GeneralListener;
import me.BerylliumOranges.listeners.PlayerInventoryClickListener;
import me.BerylliumOranges.misc.Directories;
import me.BerylliumOranges.spellevent.entity_information.EntityStats;
import me.BerylliumOranges.spellevent.processes.SpellActions;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;

public class SpellPluginMain extends JavaPlugin implements Listener {
	public static GeneralListener pl;
	private static SpellPluginMain instance;

	@SuppressWarnings("deprecation")
	@Override
	public void onEnable() {
		try {
			instance = this;

			Directories.makeDirectories();
			pl = new GeneralListener(this);

			EntityStats.loadPlayerStats();

			new SpawningListener(this);
			BuildingType.loadClass();

			PlayerInventoryClickListener inventoryClicking = new PlayerInventoryClickListener(this);
			Bukkit.getPluginManager().registerEvents(inventoryClicking, this);

			SpellActions eventActions = new SpellActions(this);
			Bukkit.getPluginManager().registerEvents(eventActions, this);

			EventSegment.loadSegmentClasses();

			Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
				public void run() {
					EveryTick.tick();
					SpawnedBuilding.tick();
				}
			}, 0, 0);
		} catch (Exception er) {

		}

	}

	@Override
	public void onDisable() {
		for (EntityStats stats : EntityStats.getMobStats()) {
			if (stats.getBar() != null) {
				stats.getBar().deleteBar();
			}
			stats.getEntity().remove();
		}
		EntityStats.writePlayerStats();

	}

	public static SpellPluginMain getInstance() {
		return instance;
	}

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		return CommandParser.findCommand(sender, cmd, label, args);
	}

	public static int generateRand(int max, int min) {
		Random rand = new Random();
		int n = rand.nextInt(max) + min;
		return n;
	}

	public Location tpLocFromString(String s, Player p) {
		float locX = Float.parseFloat(s.substring(s.indexOf('m') + 1, s.indexOf(',')));
		s = s.substring(s.indexOf(',') + 1);
		float locY = Float.parseFloat(s.substring(0, s.indexOf(',')));
		s = s.substring(s.indexOf(',') + 1);
		float locZ = Float.parseFloat(s.substring(0, s.indexOf(',')));
		s = s.substring(s.indexOf(',') + 1);

		float yaw = Float.parseFloat(s.substring(0, s.indexOf(',')));
		s = s.substring(s.indexOf(',') + 1);
		float pitch = Float.parseFloat(s.substring(0, s.length()));

		Location loc = new Location(p.getWorld(), locX, locY, locZ, yaw, pitch);
		p.teleport(loc);
		return loc;
	}

	public static Entity[] getNearbyEntities(Location l, double radius) {
		double chunkRadius = radius < 16 ? 1 : (radius - (radius % 16)) / 16;
		HashSet<Entity> radiusEntities = new HashSet<Entity>();

		for (double chX = 0 - chunkRadius; chX <= chunkRadius; chX++) {
			for (double chZ = 0 - chunkRadius; chZ <= chunkRadius; chZ++) {
				int x = (int) l.getX(), y = (int) l.getY(), z = (int) l.getZ();
				for (Entity e : new Location(l.getWorld(), x + (chX * 16), y, z + (chZ * 16)).getChunk()
						.getEntities()) {
					if (e.getLocation().distance(l) <= radius && e.getLocation().getBlock() != l.getBlock())
						radiusEntities.add(e);
				}
			}
		}

		return radiusEntities.toArray(new Entity[radiusEntities.size()]);
	}

	public static String getStringInBrackets(String localName, int bracketNum) {
		ArrayList<String> str = new ArrayList<String>();
		while (localName.indexOf("[") > -1) {
			str.add(localName.substring(localName.indexOf("[") + 1, localName.indexOf("]")));
			localName = localName.substring(0, localName.indexOf("["))
					+ localName.substring(localName.indexOf("]") + 1);
		}
		if (str.size() > bracketNum)
			return str.get(bracketNum);
		return null;
	}

	public static String replaceStringInBrackets(String localName, String newString, int bracketNum) {
		String s = getStringInBrackets(localName, bracketNum);
		int index = localName.indexOf(s);
		return localName.substring(0, index) + newString + localName.substring(index + s.length());
	}

}
