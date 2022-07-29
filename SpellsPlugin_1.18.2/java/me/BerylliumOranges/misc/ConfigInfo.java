package me.BerylliumOranges.misc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class ConfigInfo {
	private static final String SPAWN_DUNGEONS = "spawn_dungeons_and_buildings";
	private static boolean spawnDungeons = true;
	private static final String MODIFY_PROPERTY = "allow-spell-modification";
	private static boolean modify = true;
	private static final String DEFAULT_MAX_MANA = "default-max-mana";
	private static double defaultMaxMana = 500;
	private static final String DEFAULT_MANA_REGEN = "default-mana-regen";
	private static double defaultManaRegen = 1 / 2.0;

	public static void writeConfig() {
		Properties prop = new Properties();prop.setProperty(SPAWN_DUNGEONS, spawnDungeons + "");
		prop.setProperty(MODIFY_PROPERTY, modify + "");
		prop.setProperty(DEFAULT_MAX_MANA, defaultMaxMana + "");
		prop.setProperty(DEFAULT_MANA_REGEN, defaultManaRegen + "");
		try {
			FileWriter writer = new FileWriter(Directories.CONFIG_FILE);

			prop.store(writer,
					"Custom Spells Plugin configuration. Delete me to reset. Use /resetmana <player> to update their mana regen and maximum mana");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void readConfig() {
		Properties prop = new Properties();
		try {
			FileInputStream ip = new FileInputStream(Directories.CONFIG_FILE);
			prop.load(ip);
			try {
				spawnDungeons = Boolean.parseBoolean(prop.getProperty(SPAWN_DUNGEONS));
			} catch (Exception er) {
			}
			try {
				modify = Boolean.parseBoolean(prop.getProperty(MODIFY_PROPERTY));
			} catch (Exception er) {
			}
			try {
				defaultMaxMana = Double.parseDouble(prop.getProperty(DEFAULT_MAX_MANA));
			} catch (Exception er) {
				er.printStackTrace();
			}
			try {
				defaultManaRegen = Double.parseDouble(prop.getProperty(DEFAULT_MANA_REGEN));
			} catch (Exception er) {
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean isModify() {
		readConfig();
		return modify;
	}
//
//	public static void setModify(boolean modify) {
//		ConfigInfo.modify = modify;
////		writeConfig();
//	}

	public static double getDefaultMaxMana() {
		readConfig();
		return ConfigInfo.defaultMaxMana;
	}
//
//	public static void setDefaultMaxMana(int defaultMaxMana) {
//		ConfigInfo.defaultMaxMana = defaultMaxMana;
////		writeConfig();
//	}

	public static double getDefaultManaRegen() {
		readConfig();
		return ConfigInfo.defaultManaRegen;
	}

	public static boolean isSpawnDungeons() {
		return spawnDungeons;
	}

//	public static void setDefaultManaRegen(double defaultManaRegen) {
//		readConfig();
//		ConfigInfo.defaultManaRegen = defaultManaRegen;
////		writeConfig();
//	}

}
