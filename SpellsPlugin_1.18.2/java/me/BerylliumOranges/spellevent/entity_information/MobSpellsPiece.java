package me.BerylliumOranges.spellevent.entity_information;

import java.io.Serializable;
import java.util.HashMap;

import org.bukkit.inventory.ItemStack;

public class MobSpellsPiece implements Serializable {

	private static final long serialVersionUID = 8028907765831975572L;
	public final HashMap<Integer, ItemStack> itemSnapshot;
	public EntityStats stats = null;

	public MobSpellsPiece(HashMap<Integer, ItemStack> itemSnapshot) {
		this.itemSnapshot = itemSnapshot;
	}

	// Used for loading
	public MobSpellsPiece(MobSpellsPiece loadedData) {
		this.itemSnapshot = loadedData.itemSnapshot;
	}
}
