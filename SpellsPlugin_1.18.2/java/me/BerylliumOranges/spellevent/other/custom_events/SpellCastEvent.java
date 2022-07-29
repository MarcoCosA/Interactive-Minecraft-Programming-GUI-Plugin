package me.BerylliumOranges.spellevent.other.custom_events;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

import me.BerylliumOranges.spellevent.processes.SegmentProcess;
import me.BerylliumOranges.spellevent.processes.SpellProcess;

public class SpellCastEvent extends EntityEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private LivingEntity entity;
	private boolean cancelled = false;
	private SpellProcess spellProcess;
	private SegmentProcess process;
	private boolean autoCast = false;

	public SpellCastEvent(LivingEntity entity, SpellProcess spellProcess, SegmentProcess process) {
		super(entity);
		this.entity = entity;
		this.process = process;
		try {
			this.spellProcess = (SpellProcess) spellProcess.clone();
		} catch (CloneNotSupportedException e) {

			e.printStackTrace();
		}
	}

	public SpellCastEvent getInstance() {
		return this;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public LivingEntity getEntity() {
		return entity;
	}

	public void setEntity(LivingEntity entity) {
		this.entity = entity;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		cancelled = cancel;

	}

	public SpellProcess getSpellProcess() {
		return spellProcess;
	}

	public void setSpellProcess(SpellProcess spellProcess) {
		this.spellProcess = spellProcess;
	}

	public SegmentProcess getProcess() {
		return process;
	}

	public void setProcess(SegmentProcess process) {
		this.process = process;
	}

	public boolean isAutoCast() {
		return autoCast;
	}

	public void setAutoCast(boolean autoCast) {
		this.autoCast = autoCast;
	}

}
