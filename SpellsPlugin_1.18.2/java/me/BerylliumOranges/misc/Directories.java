package me.BerylliumOranges.misc;

import java.io.File;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;

public class Directories {
	public static final String MAIN_PATH = "plugins/CustomSpellsPlugin";
	public static final File MAIN_FILE = new File(MAIN_PATH);
	public static final String CONFIG_PATH = MAIN_PATH + "/config.properties";
	public static final File CONFIG_FILE = new File(CONFIG_PATH);
	public static final String PLAYERS_PATH = MAIN_PATH + "/Players/";
	public static final File PLAYERS_FILE = new File(PLAYERS_PATH);
	public static final String ITEMS_PATH = MAIN_PATH + "/Items/";
	public static final File ITEMS_FILE = new File(ITEMS_PATH);
	public static final String BUILDINGS_PATH = MAIN_PATH + "/Buildings/";
	public static final File BUILDINGS_FILE = new File(BUILDINGS_PATH);
	public static final String SPAWNED_BUILDINGS_PATH = MAIN_PATH + "/SpawnedBuildings/";
	public static final File SPAWNED_BUILDINGS_FILE = new File(SPAWNED_BUILDINGS_PATH);
//	public static final String BOSS_BARS_PATH = MAIN_PATH + "/BossBars/";
//	public static final File BOSS_BARS_FILE = new File(BOSS_BARS_PATH);

	public static void makeDirectories() {
		MAIN_FILE.mkdir();
		PLAYERS_FILE.mkdir();
		ITEMS_FILE.mkdir();
		BUILDINGS_FILE.mkdir();
		SPAWNED_BUILDINGS_FILE.mkdir();
//		BOSS_BARS_FILE.mkdir();
		if (CONFIG_FILE.exists()) {
			ConfigInfo.readConfig();
		} else
			ConfigInfo.writeConfig();
	}

	public static void clearFolder(File folder) {
		for (File f : folder.listFiles()) {
			f.delete();
		}
	}

	public static ArrayList<Class<?>> getClasses(String pathToPackage) {
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		CodeSource src = EventSegment.class.getProtectionDomain().getCodeSource();
		if (src != null) {
			URL jar = src.getLocation();
			try {
				ZipInputStream zip = new ZipInputStream(jar.openStream());
				while (true) {
					ZipEntry e = zip.getNextEntry();
					if (e == null)
						break;
					String name = e.getName();
					if (name.startsWith(pathToPackage.replace('.', '/'))) {
						name = name.replace('/', '.').substring(0, name.length() - ".class".length());
						classes.add(Class.forName(name));
					}
				}
			} catch (Exception er) {
				er.printStackTrace();
			}
		}
		return classes;
	}

}
