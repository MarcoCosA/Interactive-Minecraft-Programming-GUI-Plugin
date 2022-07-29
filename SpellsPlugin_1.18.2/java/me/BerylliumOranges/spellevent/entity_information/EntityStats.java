package me.BerylliumOranges.spellevent.entity_information;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import me.BerylliumOranges.misc.ConfigInfo;
import me.BerylliumOranges.misc.Directories;
import me.BerylliumOranges.misc.GachaItems;
import me.BerylliumOranges.misc.GachaItems.ItemTrait;
import me.BerylliumOranges.spellevent.entity_information.RPGClasses.AbstractRPGClass;
import me.BerylliumOranges.spellevent.entity_information.entityprefabs.AbstractEntityPrefab;
import me.BerylliumOranges.spellevent.entity_information.spellprefabs.AbstractSpellItem;
import me.BerylliumOranges.spellevent.processes.SegmentProcess;
import me.BerylliumOranges.spellevent.processes.SpellProcess;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;
import me.BerylliumOranges.spellevent.segments.abstracts.EventTriggerSegment;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class EntityStats implements Serializable {

	public AbstractEntityPrefab entityPrefab = null;

	static final long serialVersionUID = 1914885200773577844L;
	public static final String SEPARATOR = System.lineSeparator();
	private static int XP = 0;
	private static int level = 21;

	private double critChance = 0;
	private double lifesteal = 0;
	private double spellCDR = 0;
	private double bonusHealth = 0;
	private double movementSpeed = 0;

	public HashMap<Class<? extends EventSegment>, Integer> segmentCooldowns = new HashMap<Class<? extends EventSegment>, Integer>();
	public static ArrayList<EntityStats> playerStats = new ArrayList<EntityStats>();
	public static transient ArrayList<EntityStats> mobStats = new ArrayList<EntityStats>();

	private UUID uniqueID = UUID.randomUUID();
	private UUID entityID;
	private transient LivingEntity entity = null;
	private BossBarPiece bar = null;

	private ArrayList<SpellSlot> spellSlots = new ArrayList<SpellSlot>();

	// private int maxSpellSlots;
	private double maxMana;
	private double mana;
	private double manaRegen = 1.0;

	private boolean isPlayer;
	private boolean muted = false;
	private boolean immobilized = false;
	private boolean verbose = false;
	private int displayOption = 0;

	public int dontSendTitleTime = 0;

	private ArrayList<SpellThread> spellThreads = new ArrayList<>();
	private transient SegmentProcess lastCast = null;

	private ArrayList<String> skills = new ArrayList<String>();
	private ArrayList<String> classes = new ArrayList<String>();
	private int availableSkills = 0;
	private int availableClasses = 0;

	private boolean enemy = false;

	protected EntityStats(LivingEntity ent) {
		this.entityID = ent.getUniqueId();
		if (ent instanceof Player) {
			playerStats.add(this);
			manaRegen = ConfigInfo.getDefaultManaRegen();
			maxMana = ConfigInfo.getDefaultMaxMana();
			mana = maxMana;
			isPlayer = true;
		} else {
			enemy = true;
			mobStats.add(this);
			maxMana = 2000;
			mana = maxMana;
			isPlayer = false;
		}
	}

	public static int maxXPThisLevel() {
		return (int) ((level + 1) * 350 + 200 + Math.pow(level * 20, 1.7));
	}

	public static void tickEntityStats() {

		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			EntityStats stat = EntityStats.getEntityStats(p);
			for (int i = stat.getSpellSlots().size() - 1; i >= 0; i--) {
				SpellSlot slot = stat.getSpellSlots().get(i);
				slot.incrementCooldown();
			}
			stat.tick();
			if (stat.dontSendTitleTime > 0)
				stat.dontSendTitleTime--;
		}

		for (int i = mobStats.size() - 1; i >= 0; i--) {
			EntityStats stat = mobStats.get(i);
			if (stat.getEntity() == null || stat.getEntity().isDead()) {
				mobStats.remove(i);
				continue;
			}
			if (stat.getBar() != null) {
				stat.getBar().updateBossBar();
			}

			if (!stat.getSpellThreads().isEmpty()) {
				stat.tickSpellRotation();
			}
			stat.tick();
		}
	}

	public void castSpell(ItemStack container, boolean force) {
		if (getEntity() instanceof Mob) {

			Mob m = (Mob) getEntity();
			if (!force) {
				try {
					Class<AbstractSpellItem> spell = AbstractSpellItem.getSpellItemClass(container);
					if (!(boolean) spell.getMethod("meetsRequirements", LivingEntity.class).invoke(null, m)) {
						return;
					}
				} catch (Exception er) {
					er.printStackTrace();
				}
			}
			if (m.getTarget() != null)
				m.teleport(getEntity().getLocation().setDirection(m.getTarget().getEyeLocation().subtract(m.getEyeLocation()).toVector()));
			Bukkit.broadcastMessage("casting " + container.getItemMeta().getDisplayName());
			SpellProcess process = new SpellProcess(getEntity(), new ArrayList<LivingEntity>());
			process.setUserContainers(new ArrayList<ItemStack>(Arrays.asList(container)));
			process.start();
		}
	}

	public void tickSpellRotation() {
		for (SpellThread spellThread : spellThreads) {
			if (spellThread.getLastCastTime() <= 0) {
				castSpell(spellThread.getNextSpell().getKey(), false);
				spellThread.setTimesCasted(spellThread.getTimesCasted() + 1);
				spellThread.updateTimesCasted();
			}
			spellThread.setLastCastTime(spellThread.getLastCastTime() - 1);
		}
	}

	public transient int ticksSinceSent = 0;

	public static void giveTotems(Player p) {
		EntityStats stat = getEntityStats(p);
		if (p.getInventory().firstEmpty() > -1 && stat.getAvailableClasses() > 0) {
			int count = 0;
			for (int i = 0; i < stat.getAvailableClasses(); i++) {
				if (p.getInventory().firstEmpty() > -1) {
					// p.getInventory().addItem(AbstractRPGClass.CLASS_TOTEM);
					count++;
				}
			}
			stat.setAvailableClasses(stat.getAvailableClasses() - count);
			String s = "";
			if (stat.getAvailableClasses() > 1)
				s = "s";
			p.sendMessage(ChatColor.GREEN + " - Given you " + count + ChatColor.GOLD + " class " + ChatColor.GREEN + "totem" + s + "!");
		}
		if (p.getInventory().firstEmpty() > -1 && stat.getAvailableSkills() > 0) {
			int count = 0;
			for (int i = 0; i < stat.getAvailableSkills(); i++) {
				if (p.getInventory().firstEmpty() > -1) {
					// p.getInventory().addItem(AbstractRPGClass.SKILL_TOTEM);
					count++;
				}
			}
			stat.setAvailableSkills(stat.getAvailableSkills() - count);
			String s = "";
			if (stat.getAvailableSkills() > 1)
				s = "s";
			p.sendMessage(ChatColor.GREEN + " - Given you " + count + ChatColor.RED + " skill " + ChatColor.GREEN + "totem" + s + "!");
		}
		if (p.getInventory().firstEmpty() == -1 && (stat.getAvailableClasses() > 0 || stat.getAvailableSkills() > 0)) {
			p.sendMessage(ChatColor.RED + "Your inventory is full, you did not recieve the rest of your rewards. " + ChatColor.GRAY
					+ "(Re-log to get them)");
		}
	}

	public void tick() {
		// ticks++;
		if (mana < maxMana) {
			mana += manaRegen;
			if (mana > maxMana)
				mana = maxMana;
		} else if (mana > maxMana) {
			mana -= 0.1 + Math.max(0, mana - maxMana) / 50.0;
			if (mana < maxMana)
				mana = maxMana;
		}
		// if (ticks % 2 == 0) {
		if (mana != maxMana) {
			sendSpellCooldowns(false);
			ticksSinceSent = 0;
		} else if (ticksSinceSent < 20) {
			sendSpellCooldowns(false);
			ticksSinceSent++;
		}
		if (ticksSinceSent >= 20)
			hideSpellCooldowns(false);
		// }

		if (immobilized)
			getEntity().setVelocity(getEntity().getVelocity().multiply(0));

		segmentCooldowns.entrySet().forEach(e -> e.setValue(e.getValue() - 1));
		segmentCooldowns.entrySet().removeIf(e -> e.getValue() <= 0);
	}

	public void makeBossBar(BarColor color, String title, boolean triggerOnlyOnHurt) {
		bar = new BossBarPiece(this, title, color, triggerOnlyOnHurt);
	}

	public void triggerDeathRattle(EntityDeathEvent event) {
		if (entityPrefab != null) {
			entityPrefab.triggerDeathEvent(event);
		}
		if (bar != null) {
			bar.deleteBar();
			bar = null;
		}
	}

	public void sendSpellCooldowns(boolean forceUpdate) {
		if (!isPlayer || (dontSendTitleTime > 0 && !forceUpdate))
			return;
		Player p = (Player) getEntity();
		try {
			if (displayOption == 0)
				p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(getSpellDisplay()));
			else if (displayOption == 1)
				p.sendTitle(" ", getSpellDisplay(), 0, 15, 0);
			else {
				p.sendTitle(" ", getAnalogueDisplay(), 0, 15, 0);
			}
		} catch (Exception er) {

		}
	}

	public String getAnalogueDisplay() {
		String text = "";
		int increment = 20;
		ChatColor color = ChatColor.LIGHT_PURPLE;
		if (maxMana >= 500) {
			increment = 100;
			color = ChatColor.DARK_PURPLE;
		}
		for (int i = 0; i < maxMana; i += increment) {
			if (i < mana) {
				text += color + ".";
			} else {
				text += ChatColor.GRAY + ".";
			}
		}
		return text;
	}

	public void hideSpellCooldowns(boolean forceUpdate) {
		if (!isPlayer || (dontSendTitleTime > 0 && !forceUpdate))
			return;
		Player p = (Player) getEntity();

		p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(""));

	}

	public Player getPlayerFromID() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.getUniqueId().equals(entityID))
				return p;
		}
		return null;
	}

	public LivingEntity getEntity() {
		if (entity != null)
			return entity;
		for (World w : Bukkit.getServer().getWorlds()) {
			for (Entity ent : w.getEntities()) {
				if (entityID.equals(ent.getUniqueId())) {
					entity = (LivingEntity) ent;
					return (LivingEntity) ent;
				}
			}
		}
		return null;
	}

	public static void displayAllSpellCooldowns(boolean forceUpdate) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			getEntityStats(p).ticksSinceSent = 0;
			getEntityStats(p).sendSpellCooldowns(forceUpdate);
		}
	}

	public String getSpellDisplay() {
		ChatColor color = ChatColor.DARK_PURPLE;
		ChatColor colorOfMax = ChatColor.DARK_PURPLE;
		if (verbose) {
			color = ChatColor.GREEN;
			colorOfMax = ChatColor.GREEN;
			if (mana / maxMana < 0.25) {
				color = ChatColor.RED;
			} else if (mana / maxMana < 0.50) {
				color = ChatColor.GOLD;
			} else if (mana / maxMana < 0.75) {
				color = ChatColor.YELLOW;
			} else
				color = colorOfMax;
		} else if (mana < maxMana)
			color = ChatColor.LIGHT_PURPLE;
		return ChatColor.WHITE + "" + color + "" + (int) mana + ChatColor.WHITE + "/" + colorOfMax + (int) maxMana + ChatColor.WHITE + "";
	}

	public static void updateAllPlayers() {
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			updatePlayer(p);
		}
	}

	public static void updatePlayer(Player p) {
		EntityStats stat = EntityStats.getEntityStats(p);
		stat.updateEntityTraits(p);

		double maxHP = 16 + level;
		maxHP *= 1 + (stat.getBonusHealth() / 100.0);
		p.setMaxHealth(maxHP);
		p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.1 * (1 + (stat.getMovementSpeed() / 100.0)));
		stat.setMaxMana(ConfigInfo.getDefaultMaxMana() + ConfigInfo.getDefaultMaxMana() * stat.getBonusMaxManaMultiplier() * 0.01);
		p.setLevel(level);
		stat.updateEntityTraits(p);
		float percent = Math.max(XP / (float) (maxXPThisLevel()), 0);
		p.setExp(percent);

		// try {
		// for (Class<AbstractRPGClass> classRPG :
		// AbstractRPGClass.ALL_RPG_CLASSES) {
		// if (stat.getRPGClasses().contains(
		// ((ItemStack)
		// classRPG.getField("MENU_ITEM").get(null)).getItemMeta().getDisplayName()))
		// {
		// maxHP += (int) classRPG.getField("HEALTH_BONUS").get(null);
		// }
		// }
		// } catch (Exception er) {
		// er.printStackTrace();
		// }
		maxHP *= 1 + (stat.getBonusHealth() / 100.0);

		if (p.getMaxHealth() == p.getHealth()) {
			p.setMaxHealth(maxHP);
			p.setHealth(maxHP);
		} else {
			p.setMaxHealth(maxHP);
		}
		p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.1 * (1 + (stat.getMovementSpeed() / 100.0)));
		giveTotems(p);
	}

	public static EntityStats getEntityStats(LivingEntity ent) {
		for (EntityStats stat : playerStats) {
			if (stat.getID().equals(ent.getUniqueId())) {
				return stat;
			}
		}
		for (EntityStats stat : mobStats) {
			if (stat.getID().equals(ent.getUniqueId())) {
				return stat;
			}
		}
		return new EntityStats(ent);
	}

	public static ArrayList<EntityStats> getEntityStats(Collection<LivingEntity> ents) {
		ArrayList<EntityStats> stats = new ArrayList<EntityStats>();
		for (LivingEntity ent : ents) {
			stats.add(getEntityStats(ent));
		}
		return stats;
	}

	public static void loadPlayerStats() {
		ObjectInputStream objectinputstream = null;
		try {
			for (File f : Directories.PLAYERS_FILE.listFiles()) {
				if (!f.getName().endsWith(".txt")) {
					FileInputStream streamIn = new FileInputStream(f);
					objectinputstream = new ObjectInputStream(streamIn);
					EntityStats stat = (EntityStats) objectinputstream.readObject();
					stat.setMuted(false);
					stat.setImmobilized(false);
					playerStats.add(stat);
				} else if (f.getName().equals("info.txt")) {
					Scanner in = new Scanner(f);
					if (in.hasNextInt()) {
						level = in.nextInt();
						XP = in.nextInt();
					}
					in.close();
				}
			}
		} catch (Exception e) {
			// e.printStackTrace();
		} finally {
			if (objectinputstream != null) {
				try {
					objectinputstream.close();
				} catch (IOException e) {
					// e.printStackTrace();
				}
			}
		}
	}

	public static void writePlayerStats() {
		Directories.clearFolder(Directories.PLAYERS_FILE);
		for (int i = 0; i < EntityStats.getPlayerStats().size(); i++) {
			EntityStats p = EntityStats.getPlayerStats().get(i);
			p.writePlayerData();
		}
		try {
			FileWriter w = new FileWriter(Directories.PLAYERS_PATH + "info.txt");
			w.write(level + SEPARATOR);
			w.write(XP + "");
			w.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writePlayerData() {
		try {
			FileOutputStream fileOut = new FileOutputStream(Directories.PLAYERS_PATH + getUniqueID());
			ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
			objectOut.writeObject(this);
			objectOut.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	//
	// public static ArrayList<EntityStats> getAllStats() {
	// return playerStats;
	// }
	//
	// public static void setAllStats(ArrayList<EntityStats> stats) {
	// EntityStats.playerStats = stats;
	// }

	public void addMana(int input) {
		this.ticksSinceSent = 0;
		mana += input;
	}

	public static ArrayList<EntityStats> getPlayerStats() {
		return playerStats;
	}

	public static void setPlayerStats(ArrayList<EntityStats> playerStats) {
		EntityStats.playerStats = playerStats;
	}

	public static ArrayList<EntityStats> getMobStats() {
		return mobStats;
	}

	public static void setMobStats(ArrayList<EntityStats> mobStats) {
		EntityStats.mobStats = mobStats;
	}

	public UUID getUniqueID() {
		return uniqueID;
	}

	public void setUniqueID(UUID uniqueID) {
		this.uniqueID = uniqueID;
	}

	public UUID getID() {
		return entityID;
	}

	public static int getLevel() {
		return level;
	}

	public static void setLevel(int level) {
		EntityStats.level = level;
	}

	public ArrayList<SpellSlot> getSpellSlots() {
		return spellSlots;
	}

	public void setSpellSlots(ArrayList<SpellSlot> spellSlots) {
		this.spellSlots = spellSlots;
	}

	public UUID getEntityID() {
		return entityID;
	}

	public void setEntityID(UUID entityID) {
		this.entityID = entityID;
	}

	public double getCritChance() {
		return critChance;
	}

	public void setCritChance(double critChance) {
		this.critChance = critChance;
	}

	public double getLifesteal() {
		return lifesteal;
	}

	public void setLifesteal(double lifesteal) {
		this.lifesteal = lifesteal;
	}

	public double getBonusMaxManaMultiplier() {
		return spellCDR;
	}

	public void setManaRegen(double spellCDR) {
		this.spellCDR = spellCDR;
	}

	public double getManaRegen() {
		return manaRegen;
	}

	public double getBonusHealth() {
		return bonusHealth;
	}

	public void setBonusHealth(double maxHealth) {
		this.bonusHealth = maxHealth;
	}

	public double getMovementSpeed() {
		return movementSpeed;
	}

	public void setMovementSpeed(double movementSpeed) {
		this.movementSpeed = movementSpeed;
	}

	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	public boolean isMuted() {
		return muted;
	}

	public void setMuted(boolean muted) {
		this.muted = muted;
	}

	public boolean isImmobilized() {
		return immobilized;
	}

	public void setImmobilized(boolean immobilized) {
		this.immobilized = immobilized;
	}

	public boolean purchase(ItemStack item) {
		if (!item.hasItemMeta())
			return false;
		return purchase(item.getItemMeta().getLocalizedName());
	}

	public boolean purchase(String name) {
		for (AbstractRPGClass clazz : AbstractRPGClass.ALL_RPG_CLASSES) {
			try {
				for (ItemStack item : clazz.getSkillItems(-1)) {
					if (item.getItemMeta().getLocalizedName().equals(name)) {
						skills.add(name);
						clazz.onPurchaseSkill(item, getPlayerFromID());
						return true;
					}
				}
				ItemStack menu = clazz.getMenuItem();
				if (name.equals(menu.getItemMeta().getLocalizedName())) {
					classes.add(name);
					clazz.onPurchaseClass(getPlayerFromID());
					return true;
				}
			} catch (Exception er) {
				er.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * returns true if segment is on cooldown.
	 **/
	public boolean addSegmentCooldown(Class<? extends EventSegment> segment, int cooldown) {
		try {
			boolean isArmor = false;
			if (segment.isAssignableFrom(EventTriggerSegment.class) && segment.getField("ARMOR_TRIGGER").getBoolean(null)) {
				isArmor = true;
			}
			for (Map.Entry<Class<? extends EventSegment>, Integer> entry : segmentCooldowns.entrySet()) {
				if (isArmor && entry.getKey().isAssignableFrom(EventTriggerSegment.class)
						&& entry.getKey().getField("ARMOR_TRIGGER").getBoolean(null)) {
					return true;
				} else if (entry.getKey().equals(segment)) {
					return true;
				}
			}
		} catch (Exception er) {
			return true;
		}

		segmentCooldowns.put(segment, cooldown);
		return false;
	}

	public static String calculateTime(long time, boolean showZeroes) {
		return calculateTime(time, showZeroes, ChatColor.YELLOW, ChatColor.GOLD);
	}

	public static String calculateTime(long time, boolean showZeroes, ChatColor color1, ChatColor color2) {
		int days = (int) TimeUnit.SECONDS.toDays(time);
		long hours = TimeUnit.SECONDS.toHours(time) - (days * 24);
		long minutes = TimeUnit.SECONDS.toMinutes(time) - (TimeUnit.SECONDS.toHours(time) * 60);
		long seconds = TimeUnit.SECONDS.toSeconds(time) - (TimeUnit.SECONDS.toMinutes(time) * 60);

		String message = "";
		if (days > 0) {
			String s = "";
			if (days != 1)
				s = "s";
			message = color1 + "" + days + "" + color2 + " day" + s + " ";
		}
		if ((days > 0 && showZeroes) || hours > 0) {
			String s = "";
			if (hours != 1)
				s = "s";
			message = message + color1 + "" + hours + "" + color2 + " hour" + s + " ";
		}
		if (((days > 0 || hours > 0) && showZeroes) || minutes > 0) {
			String s = "";
			if (minutes != 1)
				s = "s";
			message = message + color1 + "" + minutes + "" + color2 + " minute" + s + " ";
		}
		String s = "";
		if (seconds != 1)
			s = "s";
		if ((days > 0 || hours > 0 || minutes > 0) && seconds == 0 && !showZeroes)
			return message.trim();

		message = message + color1 + "" + seconds + "" + color2 + " second" + s;
		return message;
	}

	public SpellSlot addSpell(SegmentProcess proc) {
		if (mana < 6)
			return null;
		double buff = 1;

		SpellSlot slot = new SpellSlot(proc, buff, this);
		spellSlots.add(slot);
		return slot;
	}

	public void updateEntityTraits(LivingEntity ent) {
		HashMap<ItemTrait, Double> traits = GachaItems.getAllTraits(ent);
		setLifesteal(GachaItems.getSpecificTrait(traits, ItemTrait.LIFESTEAL));
		setCritChance(GachaItems.getSpecificTrait(traits, ItemTrait.CRIT_CHANCE));
		setManaRegen(GachaItems.getSpecificTrait(traits, ItemTrait.MAX_MANA));
		setMovementSpeed(GachaItems.getSpecificTrait(traits, ItemTrait.MOVEMENT_SPEED));
		setBonusHealth(GachaItems.getSpecificTrait(traits, ItemTrait.MAX_HEALTH));
	}

	public BossBarPiece getBar() {
		return bar;
	}

	public void setBar(BossBarPiece bar) {
		this.bar = bar;
	}

	public double getMaxMana() {
		return maxMana;
	}

	public void setMaxMana(double maxMana) {
		this.maxMana = maxMana;
	}

	public double subtractMana(double input) {
		// You shouldn't change the amount of mana it subtracts here without
		// changing
		// subtractMana in SpellSlot
		this.mana = Math.max(0, mana - input);
		sendSpellCooldowns(false);
		ticksSinceSent = 0;
		return mana;
	}

	public double getMana() {
		return mana;
	}

	public void setMana(double mana) {
		this.mana = mana;
	}

	public boolean isCasterMob() {
		return !spellThreads.isEmpty();
	}

	public int isActionBar() {
		return displayOption;
	}

	public ArrayList<SpellThread> getSpellThreads() {
		return spellThreads;
	}

	public void setSpellThreads(ArrayList<SpellThread> spellThreads) {
		this.spellThreads = spellThreads;
	}

	public AbstractEntityPrefab getEntityPrefab() {
		return entityPrefab;
	}

	public void setEntityPrefab(AbstractEntityPrefab entityPrefab) {
		this.entityPrefab = entityPrefab;
	}

	public SegmentProcess getLastCast() {
		return lastCast;
	}

	public void setLastCast(SegmentProcess lastCast) {
		this.lastCast = lastCast;
	}

	public int getAvailableSkills() {
		return availableSkills;
	}

	public void setAvailableSkills(int availableSkills) {
		this.availableSkills = availableSkills;
	}

	public int getAvailableClasses() {
		return availableClasses;
	}

	public void setAvailableClasses(int availableClasses) {
		this.availableClasses = availableClasses;
	}

	public ArrayList<String> getSkills() {
		return skills;
	}

	public void setSkills(ArrayList<String> skills) {
		this.skills = skills;
	}

	public ArrayList<String> getRPGClasses() {
		return classes;
	}

	public void setClasses(ArrayList<String> classes) {
		this.classes = classes;
	}

	public boolean isEnemy() {
		return enemy;
	}

	public void setEnemy(boolean enemy) {
		this.enemy = enemy;
	}

	public String displayOption(int input) {
		ticksSinceSent = 0;
		hideSpellCooldowns(true);
		displayOption = input;

		if (displayOption > 2 || displayOption < 1) {
			displayOption = 0;
		}

		switch (displayOption) {
		case 0:
			return "action bar";
		case 1:
			return "subtitle";
		default:
			return "analogue";
		}
	}

	public String toggleDisplayOption() {
		if (displayOption > 1) {
			return displayOption(0);
		} else if (displayOption == 0)
			return displayOption(1);
		return displayOption(2);
	}

	public static class SpellThread {
		private ArrayList<Map.Entry<ItemStack, Integer>> spells = new ArrayList<>();
		private int lastCastTime = 0;
		private int timesCasted = 0;
		private double randomModifier = 0.3;

		public SpellThread() {

		}

		public int updateTimesCasted() {
			if (Math.random() > 0.5) {
				lastCastTime = (int) (getNextSpell().getValue() + (Math.random() * randomModifier));
			} else {
				lastCastTime = (int) (getNextSpell().getValue() - (Math.random() * randomModifier));
			}
			return lastCastTime;
		}

		public Map.Entry<ItemStack, Integer> getNextSpell() {
			if (spells.isEmpty())
				return null;
			return spells.get(lastCastTime % spells.size());
		}

		public ArrayList<Map.Entry<ItemStack, Integer>> getSpells() {
			return spells;
		}

		public void setSpells(ArrayList<Map.Entry<ItemStack, Integer>> spells) {
			this.spells = spells;
		}

		public int getLastCastTime() {
			return lastCastTime;
		}

		public void setLastCastTime(int lastCastTime) {
			this.lastCastTime = lastCastTime;
		}

		public double getRandomModifier() {
			return randomModifier;
		}

		public void setRandomModifier(double randomModifier) {
			this.randomModifier = randomModifier;
		}

		public int getTimesCasted() {
			return timesCasted;
		}

		public void setTimesCasted(int timesCasted) {
			this.timesCasted = timesCasted;
		}

	}
}
