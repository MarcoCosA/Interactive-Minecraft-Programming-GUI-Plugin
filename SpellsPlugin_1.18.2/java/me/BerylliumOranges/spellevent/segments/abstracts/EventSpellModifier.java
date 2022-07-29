package me.BerylliumOranges.spellevent.segments.abstracts;

import me.BerylliumOranges.spellevent.processes.SegmentProcess;
import me.BerylliumOranges.spellevent.projectiles.SpellProjectile;

public abstract class EventSpellModifier extends EventSegment implements Cloneable {

	public static final String THIS_SEGMENT_TYPE = SEGMENT_TYPE_TRIGGER;

	public int[] upgrades;

	public EventSpellModifier(SegmentProcess proc) {
		upgrades = getUpgrades(proc);
	}

	// Returns true to consume the modifier
	public boolean applyModifier() {
		return true;
	}

	public boolean applyProjectileModifier(SpellProjectile proj, boolean finalCall) {
		return true;
	}


	@Override
	public Object clone() throws CloneNotSupportedException {
		EventSpellModifier clone = (EventSpellModifier) super.clone();
		return clone;
	}
}
