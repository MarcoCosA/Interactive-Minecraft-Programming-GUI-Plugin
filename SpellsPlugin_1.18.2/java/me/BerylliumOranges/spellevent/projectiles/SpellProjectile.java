package me.BerylliumOranges.spellevent.projectiles;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustTransition;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import me.BerylliumOranges.main.SpellPluginMain;
import me.BerylliumOranges.spellevent.processes.SegmentProcess;
import me.BerylliumOranges.spellevent.processes.SpellProcess;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSpellModifier;
import me.BerylliumOranges.spellevent.segments.b_triggers.armor_triggers.WhenNearbyProjectile;
import me.BerylliumOranges.spellevent.segments.c_results.Mirror;
import me.BerylliumOranges.spellevent.segments.d_modifiers.InterceptorProjectile;

public class SpellProjectile implements Cloneable {
	public static final double BASE_PROJECTILE_SPEED = 0.65;
	public static final double BASE_HITBOX = 0.5;
	public static final double BASE_COST = 1 / 10.0;
	public static final double DISTANCE_BETWEEN_CHECKS = 0.25;

	protected SegmentProcess process;
	protected Location location;
	protected Vector velocity = new Vector(0, 0, 0);
	protected int ticksLived = 0;
	protected int lifespan = 100;
	protected double hitbox = BASE_HITBOX;
	protected int timesToTarget = 1;
	protected Particle particleHead = null;
	protected Particle particleTrail = null;
	protected DustTransition dustColor = null;

	protected boolean interactWithBlocks = true;
	protected boolean interactWithEntities = true;
	protected boolean bounceOffBlocks = false;
	protected Vector acceleration = new Vector(0, 0, 0);
	protected boolean forceParticle = false;
	protected boolean spellMirrored = false;
	protected boolean ignoresMirror = false;
	protected boolean cancelOnTarget = true;
	protected double distanceUnchecked = 0.0;
	protected int ticksToNotBounce = 0;
	protected boolean invisible = false;
	protected boolean hasHadSpeedModified = false;
	protected double cost = BASE_COST;

	public ArrayList<Entity> entitiesTargetted = new ArrayList<Entity>();

	public boolean projectileAlive = true;
	public static ArrayList<SpellProjectile> allProjectiles = new ArrayList<SpellProjectile>();
	public static ArrayList<EventSpellModifier> modifiersConsumed = new ArrayList<EventSpellModifier>();

	// NOTE: input direction, NOT velocity
	public SpellProjectile(SegmentProcess proc, Location loc, Vector direction) {
		proc.incrementGeneration();
		process = proc;
		location = loc;
		velocity = direction.multiply(BASE_PROJECTILE_SPEED);
		allProjectiles.add(this);
		callProjectileModifiers(false);
		run();
	}

	public SpellProjectile getInstance() {
		return this;
	}

	public void callProjectileModifiers(boolean finalCall) {
		for (int i = process.getProjectileModifiers().size() - 1; i >= 0; i--) {
			if (process.getProjectileModifiers().get(i).applyProjectileModifier(getInstance(), finalCall)) {
				modifiersConsumed.add(process.getProjectileModifiers().get(i));
				process.getProjectileModifiers().remove(i);
				continue;
			}
		}
	}

	public BukkitTask projectileTask;

	public void run() {
		process.getSpellSlot().getTasks().add(new BukkitRunnable() {
			@Override
			public void run() {
				if (ticksToNotBounce > 0)
					ticksToNotBounce--;
				// This is only here for Sniper's Dream segment, otherwise it
				// should be outside
				// the loop
				ticksLived++;
				double tempDistanceTravelled = 0;
				double velLength = velocity.length();
				Vector l = null;
				for (double i = 0; (i + DISTANCE_BETWEEN_CHECKS) < velLength + distanceUnchecked; i += DISTANCE_BETWEEN_CHECKS) {
					callProjectileModifiers(false);
					if (process.ticksNotToReflect == 0 && !ignoresMirror) {
						for (Mirror mirror : Mirror.mirrors) {
							mirror.checkIfProjectileReflect(getInstance());
						}
					} else {
						process.ticksNotToReflect--;
					}

					if (i == 0)
						l = calculateDistanceToTravel();
					if (!invisible) {

						displayProjectileParticle(process, location, hitbox, dustColor);
						if (particleTrail != null) {
							location.getWorld().spawnParticle(particleTrail, location.getX(), location.getY(), location.getZ(), 0, 0, 0, 0, 0, null,
									true);
						}
					}
					location.add(l);
					if (!invisible && particleHead != null)
						location.getWorld().spawnParticle(particleHead, location.getX(), location.getY(), location.getZ(), 0, 0, 0, 0, 0, null, true);

					if (ticksLived > lifespan || checkCollide()) {
						projectileAlive = false;
						process.setSpellLocation(location);
						callProjectileModifiers(true);
						process.getProjectileModifiers().clear();
						allProjectiles.remove(getInstance());
						EventSegment.findAndCallNext(process);
						this.cancel();
						break;
					}
					tempDistanceTravelled = i;
					if (process.getSpellSlot().subtractMana(DISTANCE_BETWEEN_CHECKS * cost, process) < 1) {
						this.cancel();
						break;
					}
				}
				distanceUnchecked += velLength - tempDistanceTravelled;
				velocity.add(acceleration);
			}

			@Override
			public void cancel() {
				projectileAlive = false;
				process.setSpellLocation(location);
				// process.getProjectileModifiers().clear();
				allProjectiles.remove(getInstance());
				super.cancel();

			}
		}.runTaskTimer(SpellPluginMain.getInstance(), 0, 0));
	}

	public Vector calculateDistanceToTravel() {
		return velocity.clone().normalize().multiply(DISTANCE_BETWEEN_CHECKS);

	}

	/**
	 * @param colorOption
	 *            0 = Green, 1 = Blue, 2 = Red
	 **/
	public static int getColorOption(Entity caster, Entity ent) {
		if (ent instanceof Player) {
			if (caster.equals(ent)) {
				// Green
				return 0;
			} else {
				// Blue
				return 1;
			}
		} else {
			// Red
			return 2;
		}
	}

	public static void displayProjectileParticle(SegmentProcess process, Location location, double hitbox) {
		displayProjectileParticle(process, location, hitbox, null);
	}

	public static void displayProjectileParticle(SegmentProcess process, Location location, double hitbox, DustTransition dustColor) {
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			DustTransition t = dustColor;
			if (t == null)
				t = getDustParticle(process.getCrit(), getColorOption(process.getCaster(), p), hitbox);
			p.spawnParticle(Particle.DUST_COLOR_TRANSITION, location.getX(), location.getY(), location.getZ(), 0, 0, 0, 0, 0, t);
		}
	}

	public static float calculateDustSize(double hitbox) {
		return (float) (Math.pow(hitbox - 0.1, 2) * 8.0);
	}

	/**
	 * @param crit
	 * @param colorOption
	 *            0 = Green, 1 = Blue, 2 = Red
	 * @param hitbox
	 *            size
	 * @return
	 */
	public static DustTransition getDustParticle(int crit, int colorOption, double hitbox) {
		int c = crit * 40;
		float size = calculateDustSize(hitbox);
		switch (colorOption) {
		// Green
		case 0: {
			c *= 2.8;
			// int red = Math.min(255, (int) (c * 1.5));
			// int blue = 0;
			// int green = Math.max(0, 255 - (int) (c * 2));
			// if (red > 250)
			// blue = Math.min(255, c);
			//
			// if (blue > 250) {
			// red = Math.max(255 - c, 0);
			// green = Math.min(255, c);
			// }

			// if (Math.random() > crit / 5.0)
			// return new DustTransition(Color.fromRGB(red, green, blue),
			// Color.fromRGB(red, green, blue), size);
			return new DustTransition(Color.fromRGB(0, Math.max(0, 255 - c), 0), Color.fromRGB(0, 255, 0), size);
			// return new DustTransition(Color.fromRGB(255, 0, 0),
			// Color.fromRGB(255, 0, 0), size);
		}
		// case 0:
		// return new DustTransition(
		// Color.fromRGB(Math.min(150, 40 + (int) (c * 2)), Math.max(30, 255 - c
		// * 2), Math.min(c * 2, 255)),
		// Color.fromRGB(50, 255, 50), size);
		// Blue
		case 1:
			return new DustTransition(Color.fromRGB(Math.min(200, 20 + (int) (c)), 40, 255), Color.fromRGB(100, 100, 255), size);
		// Red
		default:
			return new DustTransition(Color.fromRGB(Math.max(0, 255 - c), 0, 0), Color.fromRGB(255, 0, 0), size);

		}
	}

	public boolean checkCollide() {
		for (Entity ent : SpellPluginMain.getNearbyEntities(location, WhenNearbyProjectile.DISTANCE)) {

			if (ent instanceof LivingEntity && EventSegment.isEnemy(process.getCaster(), (LivingEntity) ent)
					&& !EventSegment.hasModifier(process, InterceptorProjectile.class)) {
				SpellProcess process = new SpellProcess((LivingEntity) ent, new ArrayList<LivingEntity>());
				process.getUserContainers().addAll(SpellProcess.getArmorContainers((LivingEntity) ent));
				process.setProcessTags(Arrays.asList(EventSegment.NEARBY_PROJECTILE_TRIGGER_TAG));
				process.start();
			}
		}

		if (interactWithEntities)
			for (Entity ent : location.getWorld().getNearbyEntities(location, hitbox, hitbox, hitbox)) {
				if (ent instanceof LivingEntity && ent != process.getCaster() && !process.getTargetsCopy().contains(ent) && !ent.isDead()
						&& !entitiesTargetted.contains(ent)) {
					process.getTargetsCopy().clear();
					// Duplicate targets is still not a thing
					// for (int i = 0; i < timesToTarget; i++) {

					if (!cancelOnTarget) {
						try {
							entitiesTargetted.add(ent);
							SegmentProcess proc = (SegmentProcess) process.clone();
							proc.setTarget((LivingEntity) ent);
							EventSegment.findAndCallNext(proc);
						} catch (Exception er) {
							er.printStackTrace();
						}
					} else {
						process.addTarget((LivingEntity) ent);
						projectileAlive = false;
						return true;
					}
				}
			}
		if (location.getBlock().getType()
				.isSolid() /*
							 * &&
							 * !location.getBlock().getType().isInteractable()
							 */) {
			if (interactWithBlocks && !bounceOffBlocks) {
				process.setTargets(null);
				projectileAlive = false;
				return true;
			}
		} else if (bounceOffBlocks) {
			if (ticksToNotBounce == 0) {
				Location nextLocation = location.clone().add(calculateDistanceToTravel());
				Block b = nextLocation.getBlock();
				if (b.getType().isSolid()) {
					velocity = Mirror.CalculateReflection(velocity,
							b.getLocation().add(0.5, 0.5, 0.5).toVector().clone().subtract(location.toVector()).normalize());
					// ticksToNotBounce += 3;
				}
			}
			return false;
		}
		return false;
	}

	public double getDistanceUnchecked() {
		return distanceUnchecked;
	}

	public boolean isAlive() {
		return projectileAlive;
	}

	public SegmentProcess getProcess() {
		return process;
	}

	public void setProcess(SegmentProcess process) {
		this.process = process;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Vector getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector velocity) {
		this.velocity = velocity;
	}

	public int getTicksLived() {
		return ticksLived;
	}

	public void setTicksLived(int ticksLived) {
		this.ticksLived = ticksLived;
	}

	public int getLifespan() {
		return lifespan;
	}

	public void setLifespan(int lifespan) {
		this.lifespan = lifespan;
	}

	public Particle getParticleHead() {
		return particleHead;
	}

	public void setParticleHead(Particle particleHead) {
		this.particleHead = particleHead;
	}

	public Particle getParticleTrail() {
		return particleTrail;
	}

	public void setParticleTrail(Particle particleTrail) {
		this.particleTrail = particleTrail;
	}

	public boolean isInteractWithBlocks() {
		return interactWithBlocks;
	}

	public void setInteractWithBlocks(boolean interactWithBlocks) {
		this.interactWithBlocks = interactWithBlocks;
	}

	public boolean isInteractWithEntities() {
		return interactWithEntities;
	}

	public void setInteractWithEntities(boolean interactWithEntities) {
		this.interactWithEntities = interactWithEntities;
	}

	public double getHitbox() {
		return hitbox;
	}

	public void setHitbox(double hitbox) {
		this.hitbox = hitbox;
	}

	public int getTimesToTarget() {
		return timesToTarget;
	}

	public void setTimesToTarget(int timesToTarget) {
		this.timesToTarget = timesToTarget;
	}

	public Vector getAcceleration() {
		return acceleration;
	}

	public void setAcceleration(Vector acceleration) {
		this.acceleration = acceleration;
	}

	public void setReflected(boolean input) {
		spellMirrored = input;
	}

	// Has the particle been reflected by a spell mirror?
	public boolean hasReflected() {
		return spellMirrored;
	}

	public boolean isForceParticle() {
		return forceParticle;
	}

	public void setForceParticle(boolean forceParticle) {
		this.forceParticle = forceParticle;
	}

	public boolean isIgnoresMirror() {
		return ignoresMirror;
	}

	public void setIgnoresMirror(boolean ignoresMirror) {
		this.ignoresMirror = ignoresMirror;
	}

	public boolean isBounceOffBlocks() {
		return bounceOffBlocks;
	}

	public void setBounceOffBlocks(boolean bounceOffBlocks) {
		this.bounceOffBlocks = bounceOffBlocks;
	}

	public boolean isHasHadSpeedModified() {
		return hasHadSpeedModified;
	}

	public void setHasHadSpeedModified(boolean hasHadSpeedModified) {
		this.hasHadSpeedModified = hasHadSpeedModified;
	}

	public static ArrayList<EventSpellModifier> getModifiersConsumed() {
		return modifiersConsumed;
	}

	public static void setModifiersConsumed(ArrayList<EventSpellModifier> modifiersConsumed) {
		SpellProjectile.modifiersConsumed = modifiersConsumed;
	}

	public boolean isInvisible() {
		return invisible;
	}

	public void setInvisible(boolean invisible) {
		this.invisible = invisible;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public boolean isCancelOnTarget() {
		return cancelOnTarget;
	}

	public void setCancelOnTarget(boolean cancelOnTarget) {
		this.cancelOnTarget = cancelOnTarget;
	}

	public DustTransition getDustColor() {
		return dustColor;
	}

	public void setDustColor(DustTransition dustColor) {
		this.dustColor = dustColor;
	}

}
