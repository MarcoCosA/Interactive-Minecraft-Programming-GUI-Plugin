package me.BerylliumOranges.spellevent.other.custom_events;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.BerylliumOranges.main.SpellPluginMain;
import me.BerylliumOranges.spellevent.processes.SpellProcess;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;

public class EntityGroundPoundEvent extends EntityEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private LivingEntity entity;
	private float fallDistance;
	private boolean cancelled = false;
	private ArrayList<LivingEntity> targets = new ArrayList<LivingEntity>();

	public EntityGroundPoundEvent(LivingEntity entity, float fallDistance) {
		super(entity);
		this.entity = entity;
		this.fallDistance = fallDistance;

		new BukkitRunnable() {
			public void run() {
				if (!cancelled) {
					SpellProcess process = new SpellProcess(getEntity(), targets);
					process.getUserContainers().addAll(SpellProcess.getArmorContainers(entity));
					process.setProcessTags(Arrays.asList(EventSegment.FALL_TRIGGER_TAG));
					process.setEvent(getInstance());
					process.start();
				}
			}
		}.runTaskLater(SpellPluginMain.getInstance(), 0);
	}

	private EntityGroundPoundEvent getInstance() {
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

	public float getFallDistance() {
		return fallDistance;
	}

	public void setFallDistance(float fallDistance) {
		this.fallDistance = fallDistance;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		cancelled = cancel;

	}

}
