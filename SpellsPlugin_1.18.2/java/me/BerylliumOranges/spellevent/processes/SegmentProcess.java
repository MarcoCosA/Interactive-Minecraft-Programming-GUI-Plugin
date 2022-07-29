package me.BerylliumOranges.spellevent.processes;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.inventory.ItemStack;

import me.BerylliumOranges.main.SpellPluginMain;
import me.BerylliumOranges.misc.GachaItems;
import me.BerylliumOranges.misc.GachaItems.ItemTrait;
import me.BerylliumOranges.spellevent.entity_information.EntityStats;
import me.BerylliumOranges.spellevent.entity_information.SpellSlot;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSpellModifier;

public class SegmentProcess implements Cloneable {
	private LivingEntity caster;
	private ItemStack container;
	private EntityStats stats;
	// These are cloned
	private ItemStack[][] presentSegments;
	private int[][] procs;
	private Location spellLocation;
	private ArrayList<LivingEntity> targets;
	private ArrayList<SegmentProcess> focusedSpells = new ArrayList<>();
	private ArrayList<EventSpellModifier> projectileModifiers = new ArrayList<EventSpellModifier>();

	private SpellProcess mySpellProcess;
	public int timesUpgradedBySnipersDream = 0;
	private String lastCast = null;
	private long seed;
	public int ticksNotToReflect = 0;
	private int maxCasts = 3;
	private HashMap<ItemTrait, Double> traits;
	private int crit = 0;
	private int pureCrit = 0;

	private boolean endProcess = false;
	private UUID id = UUID.randomUUID();
	private int y;
	private int x;
	private int dir;
	private SpellSlot spellSlot;
	private boolean deleteWhenSpellSlotIsNotEmpty = false;
	private int generation = 0;
	private int timesCloned = 1;
	private boolean triggersAllowed = true;
	private int lastDir = 0;

	private double manaMultiplier = 1;
	private double minManaMultiplier = 0;

	private double containerCDR = 0;

	private List<String> processTags = Arrays.asList(EventSegment.GENERAL_TRIGGER_TAG);

	private EntityEvent event = null;

	public SegmentProcess(ItemStack[][] input, ItemStack container, int y, int x, int dir, LivingEntity caster,
			ArrayList<LivingEntity> targets, Location spellLocation, EntityStats stats, SpellProcess mySpellProcess) {
		generateNewSeed();
		this.presentSegments = input;
		this.container = container;
		this.y = y;
		this.x = x;
		this.dir = dir;
		this.caster = caster;
		this.mySpellProcess = mySpellProcess;
		if (targets == null) {
			this.targets = new ArrayList<LivingEntity>();
		} else {
			this.targets = targets;
		}
		this.spellLocation = spellLocation;
		this.stats = stats;
		HashMap<ItemTrait, Double> itemTraits = GachaItems.getItemTraits(container);
		containerCDR = GachaItems.getSpecificTrait(itemTraits, ItemTrait.ITEM_MANA_COST);

		procs = new int[input.length][input[0].length];
	}

	// I don't think this is ever used...
	public double processSegments() {
		ItemStack item = presentSegments[y][x];
		for (Class<EventSegment> ev : EventSegment.ALL_SEGMENTS) {
			try {
				if (ev.getField("LOCAL_NAME").get(null).equals(item.getItemMeta().getLocalizedName())) {

					procs[y][x]++;
					if (procs[y][x] < 4) {
						dir = SpellActions.getArrowDirectionFromCustomModel(item);
						Method m = ev.getMethod("processSegment", SegmentProcess.class);
						m.invoke(null, this);
					}

					break;
				}
			} catch (Exception er) {
				er.printStackTrace();
			}
		}

		this.setX(x + getXMod(dir));
		this.setY(y + getYMod(dir));
		if ((y >= presentSegments.length || y < 0) || (x >= presentSegments[y].length || x < 0))
			return 0;

		return processSegments();
	}

	public static int getXMod(int dir) {
		switch (dir) {
		case 0:
			return 1;
		case 2:
			return -1;
		default:
			return 0;
		}
	}

	public static int getYMod(int dir) {
		switch (dir) {
		case 1:
			return 1;
		case 3:
			return -1;
		default:
			return 0;
		}
	}

	public ItemStack[][] getPresentSegments() {
		return presentSegments;
	}

	public void setPresentSegments(ItemStack[][] input) {
		this.presentSegments = input;
	}

	public ItemStack getContainer() {
		return container;
	}

	public void setContainer(ItemStack container) {
		this.container = container;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getDir() {
		return dir;
	}

	public void setDir(int dir) {
		this.dir = dir;
	}

	public int[][] getTimesProced() {
		return procs;
	}

	public void setTimesProced(int[][] procs) {
		this.procs = procs;
	}

	public Location getSpellLocation() {
		return spellLocation;
	}

	public void setSpellLocation(Location loc) {
		this.spellLocation = loc;
	}

	public SpellSlot getSpellSlot() {
		return spellSlot;
	}

	public void setSpellSlot(SpellSlot spellSlot) {
		this.spellSlot = spellSlot;
	}

	public LivingEntity getCaster() {
		return caster;
	}

	public void setCaster(LivingEntity caster) {
		this.caster = caster;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<LivingEntity> getTargetsCopy() {
		return (ArrayList<LivingEntity>) targets.clone();
	}

	public EntityStats getStats() {
		return stats;
	}

	public void setStats(EntityStats stats) {
		this.stats = stats;
	}

	public void setTargets(List<LivingEntity> targets) {
		clearTargets();
		if (targets != null)
			for (LivingEntity target : targets) {
				addTarget(target);
			}
	}

	public void setTarget(LivingEntity target) {
		setTargets((List<LivingEntity>) Arrays.asList(target));
	}

	public void addTargets(List<LivingEntity> targets) {
		if (targets != null)
			for (LivingEntity liv : targets) {
				addTarget(liv);
			}
	}

	public void addTarget(LivingEntity target) {
		if (!this.targets.contains(target)) {
			this.targets.add(target);

			ArrayList<LivingEntity> targets = new ArrayList<LivingEntity>();
			targets.add(getCaster());

			SpellProcess process = new SpellProcess(target, new ArrayList<LivingEntity>(Arrays.asList(getCaster())));
			process.getUserContainers().addAll(SpellProcess.getArmorContainers((LivingEntity) target));
			process.setProcessTags(Arrays.asList(EventSegment.TARGET_TRIGGER_TAG));
			process.start();

		}
	}

	public void clearTargets() {
		for (int i = targets.size() - 1; i >= 0; i--) {
			removeTarget(targets.get(i));
		}
	}

	public boolean removeTarget(LivingEntity target) {
		return targets.remove(target);
	}

	public double subtractMana(double cooldown) {
		return subtractMana(cooldown, true);
	}

	public double subtractMana(double cooldown, boolean takeMana) {
		return spellSlot.subtractMana(cooldown, this, takeMana);
	}

	public boolean shouldDeleteWhenSpellSlotIsNotEmpty() {
		return deleteWhenSpellSlotIsNotEmpty;
	}

	public void setDeleteWhenSpellSlotIsNotEmpty(boolean deleteWhenSpellSlotIsNotEmpty) {
		this.deleteWhenSpellSlotIsNotEmpty = deleteWhenSpellSlotIsNotEmpty;
	}

	public UUID getID() {
		return id;
	}

	public void setID(UUID id) {
		this.id = id;
	}

	public void incrementGeneration() {
		generation++;
	}

	public int getGeneration() {
		return generation;
	}

	public void setGeneration(int generation) {
		this.generation = generation;
	}

	public int getTimesCloned() {
		return timesCloned;
	}

	public void addTimesCloned(int timesCloned) {
		this.timesCloned += timesCloned;
	}

	public void setTimesCloned(int timesCloned) {
		this.timesCloned = timesCloned;
	}

	public int getCrit() {
		return crit;
	}

	public void setCrit(int crit) {
		this.crit = crit;
	}

	public ArrayList<EventSpellModifier> getProjectileModifiers() {
		return projectileModifiers;
	}

	public void setProjectileModifiers(ArrayList<EventSpellModifier> spellModifiers) {
		this.projectileModifiers = spellModifiers;
	}

	public int getTimesUpgradedBySnipersDream() {
		return timesUpgradedBySnipersDream;
	}

	public void setTimesUpgradedBySnipersDream(int timesUpgradedBySnipersDream) {
		this.timesUpgradedBySnipersDream = timesUpgradedBySnipersDream;
	}

	public boolean areTriggersAllowed() {
		return triggersAllowed;
	}

	public void setTriggersAllowed(boolean triggersAllowed) {
		this.triggersAllowed = triggersAllowed;
	}

	public boolean isProcessEnding() {
		return endProcess;
	}

	public void setEndProcess(boolean endProcess) {
		this.endProcess = endProcess;
	}

	public long getSeed() {
		return seed;
	}

	public void setSeed(long seed) {
		this.seed = seed;
	}

	public void generateNewSeed() {
		seed = (new Random()).nextLong();
	}

	public String getLastCast() {
		return lastCast;
	}

	public void setLastCast(String lastCast) {
		this.lastCast = lastCast;
	}

	public double getManaMultiplier() {
		return manaMultiplier;
	}

	public void setManaMultiplier(double manaMultiplier) {
		this.manaMultiplier = Math.max(manaMultiplier, minManaMultiplier);
	}

	public int getLastDir() {
		return lastDir;
	}

	public void setLastDir(int lastDir) {
		this.lastDir = lastDir;
	}

	public int getMaxCasts() {
		return maxCasts;
	}

	public void setMaxCasts(int maxCasts) {
		this.maxCasts = maxCasts;
	}

	public EntityEvent getEvent() {
		return event;
	}

	public void setEvent(EntityEvent event) {
		this.event = event;
	}

	public HashMap<ItemTrait, Double> getTraits() {
		return traits;
	}

	public void setTraits(HashMap<ItemTrait, Double> traits) {
		this.traits = traits;
	}

	public int getPureCrit() {
		return pureCrit;
	}

	public void setPureCrit(int purecrit) {
		this.pureCrit = purecrit;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<SegmentProcess> getFocusedSpellsCopy() {
		return (ArrayList<SegmentProcess>) focusedSpells.clone();
	}

	public void setFocusedSpells(List<SegmentProcess> focused) {
		clearFocusedSpells();
		if (focusedSpells != null)
			for (SegmentProcess focus : focused) {
				addFocusedSpell(focus);
			}
	}

	public void setFocusedSpell(SegmentProcess focus) {
		setFocusedSpells((List<SegmentProcess>) Arrays.asList(focus));
	}

	public void addFocusedSpells(List<SegmentProcess> focused) {
		if (focused != null)
			for (SegmentProcess focus : focused) {
				addFocusedSpell(focus);
			}
	}

	public void addFocusedSpell(SegmentProcess focused) {
		if (!this.focusedSpells.contains(focused)) {
			this.focusedSpells.add(focused);

			// If u want to make an event here
//			ArrayList<LivingEntity> targets = new ArrayList<LivingEntity>();
//			targets.add(getCaster());
//
//			SpellProcess process = new SpellProcess(focusedSpells, new ArrayList<LivingEntity>(Arrays.asList(getCaster())));
//			process.getUserContainers().addAll(SpellProcess.getArmorContainers((LivingEntity) focused));
//			process.setProcessTags(Arrays.asList(EventSegment.TARGET_TRIGGER_TAG));
//			process.start();

		}
	}

	public void clearFocusedSpells() {
		for (int i = focusedSpells.size() - 1; i >= 0; i--) {
			removeFocusedSpell(focusedSpells.get(i));
		}
	}

	public boolean removeFocusedSpell(SegmentProcess focus) {
		return focusedSpells.remove(focus);
	}

	public SpellProcess getMySpellProcess() {
		return mySpellProcess;
	}

	public void setMySpellProcess(SpellProcess mySpellProcess) {
		this.mySpellProcess = mySpellProcess;
	}

	public double getMinManaMultiplier() {
		return minManaMultiplier;
	}

	public void setMinManaMultiplier(double minManaMultiplier) {
		this.minManaMultiplier = minManaMultiplier;
	}

	/**
	 * returns true if segment is on cooldown.
	 **/
	public boolean addSegmentCooldown(Class<? extends EventSegment> segment, int cooldown) {
		cooldown = (int) Math.max(0, 20 * cooldown * ((100 + containerCDR) / 100.0));
		boolean b = stats.addSegmentCooldown(segment, cooldown);
		if (b) {
			try {
				boolean found = false;
				for (ItemStack item : caster.getEquipment().getArmorContents()) {
					if (item.equals(container)) {
						found = true;
						break;
					}
				}
				if (!found)
					caster.getWorld().playSound(caster.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1, 1);
//				EventSegment.alertCaster(caster,
//						((ItemStack) segment.getField("ITEM").get(null)).getItemMeta().getDisplayName(),
//						"is on cooldown!", false);
			} catch (Exception er) {
			}
		}
		return b;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object clone() throws CloneNotSupportedException {
		SegmentProcess clone = (SegmentProcess) super.clone();
		clone.setDeleteWhenSpellSlotIsNotEmpty(false);
		clone.setPresentSegments(presentSegments.clone());
		clone.incrementGeneration();

		// Apparently clone() doesn't work with 2D arrays
		int[][] procsClone = new int[procs.length][];
		for (int i = 0; i < procs.length; i++)
			procsClone[i] = procs[i].clone();

		clone.setTimesProced(procsClone);
		clone.setSpellLocation(spellLocation.clone());
		clone.targets = (ArrayList<LivingEntity>) targets.clone();

		getSpellSlot().getSegmentProcesses().add(clone);

		ArrayList<EventSpellModifier> clonedModifiers = new ArrayList<EventSpellModifier>();
		for (int i = 0; i < projectileModifiers.size(); i++) {
			clonedModifiers.add((EventSpellModifier) projectileModifiers.get(i).clone());
		}
		clone.setProjectileModifiers(clonedModifiers);
		getSpellSlot().removeWhenSpellSlotIsNotEmptyProcs();

		return clone;
	}

	public List<String> getProcessTags() {
		return processTags;
	}

	public void setProcessTags(List<String> processTags) {
		this.processTags = processTags;
	}

}
