package me.BerylliumOranges.spellevent.entity_information;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitTask;

import me.BerylliumOranges.spellevent.processes.SegmentProcess;

public class SpellSlot implements Serializable {
	private static final long serialVersionUID = -1774672807433727162L;
	private boolean available = true;
	private double manaUsed = 0;
	private double manaUseMultiplier = 1;
	private double buff = 1;
	private transient ArrayList<SegmentProcess> segmentProcesses = new ArrayList<SegmentProcess>();
	private EntityStats stat;
	private HashMap<UUID, Integer> alreadyCounted = new HashMap<UUID, Integer>();
	private boolean stopped = false;
	private transient ArrayList<BukkitTask> tasks = new ArrayList<BukkitTask>();
	private double allocatedMana = Double.MAX_VALUE;

	public void stopSpell() {
		stopped = true;
		if (segmentProcesses != null) {
			for (SegmentProcess proc : segmentProcesses) {
				int[][] matrix = new int[proc.getTimesProced().length][proc.getTimesProced()[0].length];
				for (int[] row : matrix)
					Arrays.fill(row, 3);
				proc.setTimesProced(matrix);
			}
			for (BukkitTask task : tasks) {
				Bukkit.broadcastMessage("cancelling task");
				task.cancel();
			}
		}
		segmentProcesses.clear();
	}

	public SpellSlot(SegmentProcess proc, double manaMultiplierBuff, EntityStats stat) {
		segmentProcesses.add(proc);
		this.stat = stat;
		this.buff = manaMultiplierBuff;
	}

	public void removeSegmentProcess(SegmentProcess proc) {
		segmentProcesses.remove(proc);
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	String lastCast = null;

	public double subtractMana(double cooldown, SegmentProcess proc) {
		return subtractMana(cooldown, proc, true);
	}

	public double subtractMana(double cooldown, SegmentProcess proc, boolean takeMana) {
		if (proc.getLastCast() != null && (lastCast == null || !lastCast.equals(proc.getLastCast()))) {
			lastCast = proc.getLastCast();
			proc.getCaster().sendMessage("Cast " + proc.getLastCast());
			proc.setLastCast(null);
		}

		allocatedMana -= cooldown;

		double manaUse = cooldown * manaUseMultiplier * proc.getManaMultiplier();
		manaUsed += manaUse;
		if (manaUse > stat.getMana()) {
			for (SegmentProcess procs : segmentProcesses) {
				procs.getSpellLocation().getWorld().playSound(procs.getSpellLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 0.7F, 2F);
				EntityStats.getEntityStats(procs.getCaster()).ticksSinceSent = 0;
			}
			// stopSpell();
			return 0;
		}

		if (!takeMana) {
			if (allocatedMana < 0)
				return -1;
			return stat.getMana() - manaUse;
		}
		if (stat.subtractMana(manaUse) < 1 || allocatedMana < 0 || stopped) {
			return -1;
			// stopSpell();
		}

		return stat.getMana();
	}

	public void removeWhenSpellSlotIsNotEmptyProcs() {
		if (segmentProcesses == null)
			return;
		boolean foundActiveSegment = false;
		ArrayList<SegmentProcess> toDelete = new ArrayList<SegmentProcess>();
		for (int i = segmentProcesses.size() - 1; i >= 0; i--) {
			SegmentProcess proc = segmentProcesses.get(i);
			if (proc.shouldDeleteWhenSpellSlotIsNotEmpty() && tasks.isEmpty()) {
				toDelete.add(proc);
			} else {
				foundActiveSegment = true;
			}
		}
		if (foundActiveSegment && !toDelete.isEmpty()) {
			segmentProcesses.removeAll(toDelete);
		}
	}

	public void incrementCooldown() {
		removeWhenSpellSlotIsNotEmptyProcs();

		if (segmentProcesses == null || segmentProcesses.isEmpty()) {
			stat.getSpellSlots().remove(this);
		}

	}

	public static final int MAX_MANA_COST = 60 * 60 * 20 * 10;

	// Cancel spell if this returns true
	public boolean multiplyAndCheck(SegmentProcess proc, double input) {
		proc.setManaMultiplier(proc.getManaMultiplier() * input);
		return false;

	}

	public ArrayList<SegmentProcess> getSegmentProcesses() {
		return segmentProcesses;
	}

	public void setSegmentProcesses(ArrayList<SegmentProcess> segmentProcesses) {
		this.segmentProcesses = segmentProcesses;
	}

	public EntityStats getStat() {
		return stat;
	}

	public void setStat(EntityStats stat) {
		this.stat = stat;
	}

	public boolean isStopped() {
		return stopped;
	}

	public void setStopped(boolean stopped) {
		this.stopped = stopped;
	}

	public ArrayList<BukkitTask> getTasks() {
		return tasks;
	}

	public void setTasks(ArrayList<BukkitTask> tasks) {
		this.tasks = tasks;
	}

	public double getBuff() {
		return buff;
	}

	public void setBuff(double buff) {
		this.buff = buff;
	}

	public double getManaUsed() {
		return manaUsed;
	}

	public void setManaUsed(double manaUsed) {
		this.manaUsed = manaUsed;
	}

	public double getManaUseMultiplier() {
		return manaUseMultiplier;
	}

	public void setManaUseMultiplier(double manaUseMultiplier) {
		this.manaUseMultiplier = manaUseMultiplier;
	}

	public double getAllocatedMana() {
		return allocatedMana;
	}

	public void setAllocatedMana(double allocatedMana) {
		this.allocatedMana = allocatedMana;
	}
}
