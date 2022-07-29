package me.BerylliumOranges.spellevent.segments.d_modifiers;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import me.BerylliumOranges.main.SpellPluginMain;
import me.BerylliumOranges.spellevent.other.Guideline;
import me.BerylliumOranges.spellevent.processes.SegmentProcess;
import me.BerylliumOranges.spellevent.processes.SpellActions;
import me.BerylliumOranges.spellevent.processes.SpellProcess;
import me.BerylliumOranges.spellevent.projectiles.SpellProjectile;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSpellModifier;

public class BlockFallingPath extends EventSpellModifier {

	public static final ArrayList<Material> BLOCK_TYPES = new ArrayList<Material>(Arrays.asList(Material.OBSIDIAN, Material.FIRE, Material.COBWEB));
	public static final ArrayList<Material> BLOCK_TYPES_TO_DELETE = new ArrayList<Material>(Arrays.asList(Material.COBWEB, Material.OBSIDIAN));

	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST_MULTIPLIER = 1;
	public static final double MANA_COST = 30;
	public static final int SIZE = 0;
	public static final int RARITY = 4;
	public static final double COST_PER_BLOCK = SpellProjectile.BASE_COST * 2;

	public Material mat = null;
	public static final String BLOCK_DELETE_TAG = ChatColor.RED + "[FallingBlockPath]";

	public static boolean registered = false;

	public static Guideline processSegment(SegmentProcess proc) {
		if (proc.getY() < proc.getTimesProced().length) {
			BlockFallingPath path = new BlockFallingPath(proc);
			path.setMaterial(BLOCK_TYPES.get(Math.min(getSize(getUpgrades(proc)) - (proc.getCrit()), BLOCK_TYPES.size() - 1)));
			proc.incrementGeneration();

			if (proc.subtractMana(MANA_COST) < 1)
				return new Guideline(proc, true, true);
			proc.getProjectileModifiers().add(path);
		}
		return findAndCallNext(proc);
	}

	@Override
	public boolean applyProjectileModifier(SpellProjectile proj, boolean finalCall) {
		if (proj.getTicksLived() == 0)
			proj.setCost(proj.getCost() + COST_PER_BLOCK);

		for (Entity ent : SpellPluginMain.getNearbyEntities(proj.getLocation(), 1.3)) {
			if (ent instanceof FallingBlock)
				return false;
		}
		if (proj.getProcess().getY() < proj.getProcess().getTimesProced().length) {
			Block b = proj.getLocation().clone().add(0, 1, 0).getBlock();
			if (b.getType().isAir()) {
				FallingBlock fall = b.getWorld().spawnFallingBlock(proj.getLocation().clone().add(0, -1, 0), mat, (byte) 0);
				fall.setDropItem(false);
				if (mat == Material.OBSIDIAN)
					fall.setVelocity(new Vector(0, -1, 0));
				// fall.setHurtEntities(true);
				if (BLOCK_TYPES_TO_DELETE.contains(mat)) {
					fall.addScoreboardTag(BLOCK_DELETE_TAG);
				}
			}
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getUpdatedItem(int upgrades[], int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(getColorFromRarity(RARITY) + "Falling Block Path");
		ArrayList<String> lore = new ArrayList<String>(Arrays.asList(ChatColor.WHITE + "Your next projectile drops",
				ChatColor.WHITE + "blocks of " + ChatColor.RED + BLOCK_TYPES.get(Math.min(getSize(upgrades), BLOCK_TYPES.size() - 1)), "",
				getCostText(MANA_COST) + " " + ChatColor.GRAY + SpellActions.getArrowModelFromInt(dir)));
		lore.addAll(getMultiplierLore(MANA_COST_MULTIPLIER));
		meta.setLore(lore);
		meta.setLocalizedName(THIS_SEGMENT_TYPE + "[" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_MODIFIER);
		item.setItemMeta(meta);
		return item;
	}

	public BlockFallingPath(SegmentProcess proc) {
		super(proc);
		if (!registered) {
			registered = true;
			Bukkit.getPluginManager().registerEvents(new FallingBlockEvents(), SpellPluginMain.getInstance());
		}
	}

	public static int getSize(int upgrades[]) {

		return (int) (SIZE + (upgrades[0] + upgrades[1]));
	}

	public Material getMaterial() {
		return mat;
	}

	public void setMaterial(Material mat) {
		this.mat = mat;
	}

	public static class FallingBlockEvents implements Listener {
		public FallingBlockEvents() {

		}

		@EventHandler
		public void onFallingBlockLand(EntityChangeBlockEvent e) {
			e.getBlock().getState().update();
			if (e.getEntity().getScoreboardTags().contains(BLOCK_DELETE_TAG)) {
				Material type = e.getTo();
				if (type.isSolid())
					for (Entity ent : SpellPluginMain.getNearbyEntities(e.getBlock().getLocation().clone().add(0.5, 0.5, 0.5), 1)) {
						if (ent instanceof LivingEntity) {
							ent.teleport(ent.getLocation().add(0, 1, 0));
						}
					}

				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(SpellProcess.plugin, new Runnable() {
					public void run() {
						if (e.getBlock().getType().equals(type)) {
							e.getBlock().getLocation().getBlock().setType(Material.AIR);
						}
					}
				}, 200L);
			}
		}
	}
}
