package me.BerylliumOranges.misc;

import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;

public class DirectoryTools {

	/**
	 * Gets classes in package by name using Class.forName()
	 * 
	 * @param pathtoPackage
	 *            Ex: me/BerylliumOranges/event/segments
	 */
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
