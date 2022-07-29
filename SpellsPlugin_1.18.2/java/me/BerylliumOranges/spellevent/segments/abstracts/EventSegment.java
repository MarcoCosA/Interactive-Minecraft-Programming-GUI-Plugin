package me.BerylliumOranges.spellevent.segments.abstracts;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.BerylliumOranges.misc.DirectoryTools;
import me.BerylliumOranges.spellevent.entity_information.EntityStats;
import me.BerylliumOranges.spellevent.other.Guideline;
import me.BerylliumOranges.spellevent.other.OtherItems;
import me.BerylliumOranges.spellevent.processes.SegmentProcess;
import me.BerylliumOranges.spellevent.processes.SpellActions;
import me.BerylliumOranges.spellevent.segments.d_modifiers.InterceptorProjectile;
import me.BerylliumOranges.spellevent.segments.d_modifiers.SoundModifier;

public abstract class EventSegment {
	public final static ItemStack ITEM = getUpdatedItem(new int[2], 0);
	public static final String EVENT_ITEM_TEXT = "Event";
	public static final String SEGMENT_TYPE_RESULT = "EventResultSegment";
	public static final String SEGMENT_TYPE_TRIGGER = "EventTriggerSegment";
	public static final String SEGMENT_TYPE_SUMMON = "EventSummonSegment";
	public static final String SEGMENT_TYPE_LOGIC = "EventLogicSegment";
	public static final String SEGMENT_TYPE_UPGRADE = "EventUpgradeSegment";
	public static final String SEGMENT_TYPE_SOUND = "EventSoundSegment";

	public static final int CUSTOM_MODEL_LOGIC = 1;
	public static final int CUSTOM_MODEL_MODIFIER = CUSTOM_MODEL_LOGIC + 4;
	public static final int CUSTOM_MODEL_GREEN = CUSTOM_MODEL_MODIFIER + 4;
	public static final int CUSTOM_MODEL_UNFAIR = CUSTOM_MODEL_GREEN + 4;
	public static final int CUSTOM_MODEL_TRIGGER = CUSTOM_MODEL_UNFAIR + 4;
	public static final int CUSTOM_MODEL_SPELL_FOCUS = CUSTOM_MODEL_TRIGGER + 4;
	public static final int CUSTOM_MODEL_RESULT = 25;
	public static final int CUSTOM_MODEL_UNFAIR_RESULT = 26;
	public static final int CUSTOM_MODEL_SPELL_FOCUS_RESULT = 27;

	public static final String INDICATOR = "SegmentIndicatorTrigger";
	public static final String LOCKED_INDICATOR = "LockedIndicator";

	public static final String THIS_SEGMENT_TYPE = SEGMENT_TYPE_LOGIC;

	public final static String LOCAL_NAME = ITEM.getItemMeta().getLocalizedName();
	public static final double MANA_COST = 6;

	public static final int RARITY = 0;

	public static final String ARROW = "\u2192";

	public static final boolean IS_PROJECTILE = false;

	public final static ArrayList<Class<EventSegment>> ALL_SEGMENTS = loadSegmentClasses();
	public final static ArrayList<Class<EventSegment>> ALL_PROJECTILE_SEGMENTS = loadProjectileSegmentClasses();
	public final static ArrayList<Class<EventSegment>> ALL_RESULT_SEGMENTS = loadResultSegmentClasses();
	public final static ArrayList<Class<EventSegment>> ALL_MODIFIER_SEGMENTS = loadModifierSegmentClasses();
	public final static ArrayList<ItemStack> ALL_SEGMENT_ITEMS = loadSegmentItems();

	public static final String GENERAL_TRIGGER_TAG = "GeneralTriggerTag";
	public static final String MELEE_TRIGGER_TAG = "MeleeTriggerTag";
	public static final String TAKE_DAMAGE_TRIGGER_TAG = "ArmorTriggerTag";
	public static final String TARGET_TRIGGER_TAG = "TargetTriggerTag";
	public static final String NEARBY_DEATH_TRIGGER_TAG = "NearbyDeathTriggerTag";
	public static final String NEARBY_EAT_TRIGGER_TAG = "NearbyEatTriggerTag";
	public static final String NEARBY_PROJECTILE_TRIGGER_TAG = "NearbyProjectileTriggerTag";
	public static final String FALL_TRIGGER_TAG = "FallTriggerTag";

	public static final List<String> ACCEPTED_TAGS = Arrays.asList(GENERAL_TRIGGER_TAG);

	public static boolean hasModifier(SegmentProcess proc, Class<? extends EventSpellModifier> class1) {
		for (EventSpellModifier mod : proc.getProjectileModifiers()) {
			if (mod.getClass().equals(class1)) {
				return true;
			}
		}
		return false;
	}

	public static String getAlertName(Entity ent) {
		if (ent.getCustomName() != null)
			return ent.getCustomName();
		return ent.getName();
	}

	public static boolean isEnemy(LivingEntity caster, LivingEntity other) {
		if (caster == other)
			return false;
		if (caster instanceof Player && other instanceof Player)
			return false;
		if (caster instanceof IronGolem && other instanceof IronGolem)
			return false;
		if (caster instanceof Mob && other instanceof Mob)
			return false;
		if (other instanceof Animals)
			return false;
		return true;
	}

	public static String getAlertName(ItemStack item) {
		if (item == null)
			return "?";
		if (item.hasItemMeta() && item.getItemMeta().hasDisplayName())
			return item.getItemMeta().getDisplayName();
		return StringUtils.capitaliseAllWords(StringUtils.replace(item.getType().name(), "_", " ").toLowerCase());
	}

	public static enum EditOption {
		// Underscores to decrease chance of name collisions
		FULLY("_Fully Editable"), ROTATABLE("_Rotatable Only"), NOTHING("_Unchangeable");

		private String text;

		private EditOption(String s) {
			text = s;
		}

		public String getText() {
			return text;
		}

		public String getDisplayText() {
			return ChatColor.BLUE + text.substring(1);
		}
	}

	public static boolean hasTag(SegmentProcess proc, Class<? extends EventSegment> clazz) {
		return hasTag(proc.getProcessTags(), clazz);
	}

	@SuppressWarnings("unchecked")
	public static boolean hasTag(List<String> processTags, Class<? extends EventSegment> clazz) {
		try {
			List<String> tags = ((List<String>) clazz.getField("ACCEPTED_TAGS").get(null));
			for (String tag : processTags) {
				if (tags.contains(tag))
					return true;
			}
		} catch (Exception er) {
			er.printStackTrace();
		}
		return false;
	}

	public static EditOption getEditOption(ItemMeta meta) {
		if (meta != null)
			return getEditOption(meta.getLocalizedName());
		return EditOption.FULLY;
	}

	public static EditOption getEditOption(String name) {
		for (EditOption edit : EditOption.values()) {
			if (name.contains(edit.getText())) {
				return edit;
			}
		}
		return EditOption.FULLY;
	}

	public static String changeEditOption(String name, EditOption input) {
		EditOption option = getEditOption(name);
		int index = name.indexOf(option.getText());
		if (index > -1) {
			name = name.substring(0, index) + input.getText() + name.substring(index + option.getText().length());
		} else if (!input.equals(EditOption.FULLY)) {
			name = name + "[" + input.getText() + "]";
		}
		return name;
	}

	public static ArrayList<String> getExtraSegmentLore(ItemMeta meta) {
		EditOption edit = getEditOption(meta);
		if ((edit.equals(EditOption.ROTATABLE)
				&& (meta.getLocalizedName().startsWith(SEGMENT_TYPE_RESULT) || meta.getLocalizedName().startsWith(SEGMENT_TYPE_SOUND)))
				|| !meta.getLocalizedName().startsWith(EVENT_ITEM_TEXT)) {
			edit = EditOption.NOTHING;
		}
		if (!edit.equals(EditOption.FULLY)) {
			return new ArrayList<String>(Arrays.asList(edit.getDisplayText()));
		}
		return new ArrayList<String>();
	}

	public static Class<? extends EventSegment> getSegmentClassFromLocalName(String localName) {
		for (Class<EventSegment> seg : ALL_SEGMENTS) {
			try {
				if (localName.startsWith(seg.getField("LOCAL_NAME").get(null).toString()))
					return seg;
			} catch (Exception er) {
				er.printStackTrace();
			}
		}
		return EventSegment.class;
	}

	public static int[] modifyUpgradesInformation(ItemStack item, int[] upgrades) {
		return upgrades;
	}

	public static Guideline processSegment(SegmentProcess proc) {
		return findAndCallNext(proc);
	}

	public static Guideline findAndCallNext(SegmentProcess proc) {

		if (proc.isProcessEnding()) {
			return new Guideline(proc, true, false);
		}

		proc.setX(proc.getX() + SegmentProcess.getXMod(proc.getDir()));
		proc.setY(proc.getY() + SegmentProcess.getYMod(proc.getDir()));
		if ((proc.getY() >= proc.getPresentSegments().length || proc.getY() < 0)
				|| (proc.getX() >= proc.getPresentSegments()[proc.getY()].length || proc.getX() < 0))
			return new Guideline(proc, true, false);

		return callNext(proc, proc.areTriggersAllowed());
	}

	public static boolean EquipEntity(LivingEntity liv, ItemStack item) {
		if (item == null)
			return false;
		String type = item.getType().toString().toLowerCase();
		if (type.contains("sword") || type.contains("bow")) {
			if (liv.getEquipment().getItemInMainHand() == null || !liv.getEquipment().getItemInMainHand().hasItemMeta()) {
				liv.getEquipment().setItemInMainHand(item);
				return true;
			}
		} else if (type.contains("helmet")) {
			if (liv.getEquipment().getHelmet() == null || !liv.getEquipment().getHelmet().hasItemMeta()) {
				liv.getEquipment().setHelmet(item);
				return true;
			}
		} else if (type.contains("chestplate")) {
			if (liv.getEquipment().getChestplate() == null || !liv.getEquipment().getChestplate().hasItemMeta()) {
				liv.getEquipment().setChestplate(item);
				return true;
			}
		} else if (type.contains("leggings")) {
			if (liv.getEquipment().getLeggings() == null || !liv.getEquipment().getLeggings().hasItemMeta()) {
				liv.getEquipment().setLeggings(item);
				return true;
			}
		} else if (type.contains("boots")) {
			if (liv.getEquipment().getBoots() == null || !liv.getEquipment().getBoots().hasItemMeta()) {
				liv.getEquipment().setBoots(item);
				return true;
			}
		}
		return false;
	}

	// public static final int MAX_CASTS = 3;

	public static Guideline callNext(SegmentProcess proc, boolean allowsTriggers) {
		ItemStack item = proc.getPresentSegments()[proc.getY()][proc.getX()];
		if (item != null && item.hasItemMeta()
				&& (allowsTriggers || !item.getItemMeta().getLocalizedName().startsWith(EventSegment.SEGMENT_TYPE_TRIGGER))) {
			if (item.getItemMeta().getLocalizedName().startsWith("EventItem")) {
				return findAndCallNext(proc);
			}
			if (item.getItemMeta().getLocalizedName().startsWith(SEGMENT_TYPE_SOUND)) {
				int[][] procs = proc.getTimesProced();
				procs[proc.getY()][proc.getX()]++;
				if (procs[proc.getY()][proc.getX()] <= proc.getMaxCasts()) {
					return SoundModifier.processSegment(proc);
				}
			} else {
				for (Class<EventSegment> ev : EventSegment.ALL_SEGMENTS) {
					try {

						if (item.getItemMeta().getLocalizedName().contains(ev.getField("LOCAL_NAME").get(null) + "")) {
							int[][] procs = proc.getTimesProced();
							procs[proc.getY()][proc.getX()]++;
							if (procs[proc.getY()][proc.getX()] <= proc.getMaxCasts()) {
								proc.setLastDir(proc.getDir());
								proc.setDir(SpellActions.getArrowDirectionFromCustomModel(item));
								Method m = ev.getMethod("processSegment", SegmentProcess.class);
								return (Guideline) m.invoke(null, proc);
							}
							break;
						}
					} catch (Exception er) {
						er.printStackTrace();
					}
				}
			}
		}
		return findAndCallNext(proc);
	}

	public static final int CRIT_MULTIPLIER = 3;

	public static int[] getUpgrades(SegmentProcess proc) {
		int[] upgrades = new int[2];
		for (int i = 0; i < 4; i++) {
			int x = SegmentProcess.getXMod(i) + proc.getX();
			int y = SegmentProcess.getYMod(i) + proc.getY();
			if (x >= 0 && x < proc.getPresentSegments()[0].length && y >= 0 && y < proc.getPresentSegments().length) {
				upgrades = getUpgradeFromItem(proc.getPresentSegments()[y][x], upgrades);
			}
		}
		upgrades[0] += proc.getCrit();
		return upgrades;
	}

	// Does not take crit into account. Delay segments needed this
	public static int[] getItemUpgrades(SegmentProcess proc) {
		int[] upgrades = new int[2];
		for (int i = 0; i < 4; i++) {
			int x = SegmentProcess.getXMod(i) + proc.getX();
			int y = SegmentProcess.getYMod(i) + proc.getY();
			if (x >= 0 && x < proc.getPresentSegments()[0].length && y >= 0 && y < proc.getPresentSegments().length) {
				upgrades = getUpgradeFromItem(proc.getPresentSegments()[y][x], upgrades);
			}
		}
		return upgrades;
	}

	public static int[] getUpgradeFromItem(ItemStack item, int[] upgrades) {
		if (item == null || !item.hasItemMeta())
			return upgrades;
		if (item.getItemMeta().getLocalizedName().startsWith(OtherItems.singleUpgrade().getItemMeta().getLocalizedName()))
			upgrades[0]++;
		else if (item.getItemMeta().getLocalizedName().startsWith(OtherItems.doubleUpgrade().getItemMeta().getLocalizedName()))
			upgrades[1] += 2;
		else if (item.getItemMeta().getLocalizedName().startsWith(OtherItems.quadrupleUpgrade().getItemMeta().getLocalizedName()))
			upgrades[0] += 4;
		else if (item.getItemMeta().getLocalizedName().startsWith(OtherItems.octupleUpgrade().getItemMeta().getLocalizedName()))
			upgrades[1] += 8;
		return upgrades;
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<Class<EventSegment>> loadSegmentClasses() {
		ArrayList<Class<EventSegment>> classesList = new ArrayList<Class<EventSegment>>();

		String path = "me.BerylliumOranges.spellevent.segments";
		for (Class<?> clazz : DirectoryTools.getClasses(path)) {
			if (EventSegment.class.isAssignableFrom(clazz))
				classesList.add((Class<EventSegment>) clazz);
		}
		return classesList;
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<Class<EventSegment>> loadProjectileSegmentClasses() {
		ArrayList<Class<EventSegment>> classesList = new ArrayList<Class<EventSegment>>();
		try {
			String path = "me.BerylliumOranges.spellevent.segments";
			for (Class<?> clazz : DirectoryTools.getClasses(path)) {
				if (EventSegment.class.isAssignableFrom(clazz) && ((boolean) clazz.getField("IS_PROJECTILE").get(null))) {
					classesList.add((Class<EventSegment>) clazz);
				}
			}
		} catch (Exception er) {
			er.printStackTrace();
		}
		return classesList;
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<Class<EventSegment>> loadResultSegmentClasses() {
		ArrayList<Class<EventSegment>> classesList = new ArrayList<Class<EventSegment>>();
		try {
			String path = "me.BerylliumOranges.spellevent.segments";
			for (Class<?> clazz : DirectoryTools.getClasses(path)) {
				if (EventSegment.class.isAssignableFrom(clazz)
						&& clazz.getField("THIS_SEGMENT_TYPE").get(null).equals(EventSegment.SEGMENT_TYPE_RESULT)) {
					int modelData = ((ItemStack) clazz.getField("ITEM").get(null)).getItemMeta().getCustomModelData();
					if ((modelData == CUSTOM_MODEL_RESULT || modelData == CUSTOM_MODEL_UNFAIR_RESULT) && (int) clazz.getField("RARITY").get(null) < 6)
						classesList.add((Class<EventSegment>) clazz);
				}
			}
		} catch (Exception er) {
			er.printStackTrace();
		}
		return classesList;
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<Class<EventSegment>> loadModifierSegmentClasses() {
		ArrayList<Class<EventSegment>> classesList = new ArrayList<Class<EventSegment>>();
		try {
			String path = "me.BerylliumOranges.spellevent.segments";
			for (Class<?> clazz : DirectoryTools.getClasses(path)) {
				if (EventSpellModifier.class.isAssignableFrom(clazz)) {
					if ((int) clazz.getField("RARITY").get(null) < 6)
						classesList.add((Class<EventSegment>) clazz);
				}
			}
		} catch (Exception er) {
			er.printStackTrace();
		}
		return classesList;
	}

	public static ArrayList<ItemStack> loadSegmentItems() {
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		for (Class<EventSegment> ev : EventSegment.ALL_SEGMENTS) {
			try {
				ItemStack m = (ItemStack) ev.getField("ITEM").get(null);
				items.add(m);
			} catch (Exception er) {
				er.printStackTrace();
			}
		}

		return items;
	}

	public static ItemStack getUpdatedItem(int[] s, int dir) {
		ItemStack item = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.GRAY + "Base Segment");
		meta.setLore(Arrays.asList(ChatColor.WHITE + "Can be used as to aim segments.",
				ChatColor.WHITE + "Otherwise this does nothing. " + ChatColor.GRAY + SpellActions.getArrowModelFromInt(dir), "",
				getCostText(0) + " " + SpellActions.getArrowModelFromInt(dir)));

		meta.setLocalizedName(THIS_SEGMENT_TYPE + " [" + ChatColor.stripColor(meta.getDisplayName()) + "]");
		meta.setCustomModelData(dir + CUSTOM_MODEL_LOGIC);
		item.setItemMeta(meta);
		return item;
	}

	public static String getPercentMaxManaCostText(double percent) {
		String message = ChatColor.WHITE + "Cost: " + ChatColor.AQUA;
		if (percent % 1 > 0.09)
			return message + percent + ChatColor.WHITE + "% of " + ChatColor.UNDERLINE + "max mana";
		return message + (int) percent + ChatColor.WHITE + "% of " + ChatColor.UNDERLINE + "max mana";

	}

	public static String getCostText(double cost) {

		String message = ChatColor.WHITE + "Cost: " + ChatColor.GOLD;
		if (cost % 1 > 0.09)
			return message + cost + ChatColor.WHITE + "";
		return message + (int) cost + ChatColor.WHITE + "";

	}

	public static String getCostPerBlock(double cost) {
		cost *= 10.0;
		String message = ChatColor.WHITE + "Cost " + ChatColor.GOLD;
		if (cost % 1 > 0.09)
			return (message + cost + ChatColor.WHITE + " Mana per " + ChatColor.GOLD + "10 " + ChatColor.WHITE + "blocks");
		return (message + (int) cost + ChatColor.WHITE + " Mana per " + ChatColor.GOLD + "10 " + ChatColor.WHITE + "blocks");

	}

	public static List<String> getMultiplierLore(double mod) {
		double val = 1 + Math.round(Math.abs(mod - 1) * 100) / 100.0;
		if (mod == 0)
			val = 0;
		if (val == 1)
			return Arrays.asList();
		String text = mod >= 1 ? ChatColor.RED + "multiplied" + ChatColor.WHITE + " by " + ChatColor.RED + val + ""
				: ChatColor.GREEN + "multiplied" + ChatColor.WHITE + " by " + ChatColor.GREEN + val + "";
		return Arrays.asList(ChatColor.WHITE + "Following segments have their", ChatColor.WHITE + "cooldowns " + text);
	}

	public static String getCooldownLore(int mod) {
		return ChatColor.WHITE + "Cooldown: " + EntityStats.calculateTime((long) mod, false);
	}

	public static String getFreeText() {
		return ChatColor.WHITE + "Spell is " + ChatColor.AQUA + "free";
	}

	public static String getCastText() {
		return ChatColor.WHITE + "Segments can only be cast " + ChatColor.YELLOW + "once";
	}

	public static ChatColor getColorFromRarity(int rarity) {
		switch (rarity) {
		case -1:
			return ChatColor.DARK_AQUA;
		case 0:
			return ChatColor.GRAY;
		case 1:
			return ChatColor.BLUE;
		case 2:
			return ChatColor.DARK_PURPLE;
		case 3:
			return ChatColor.GOLD;
		case 4:
			return ChatColor.RED;
		default:
			return ChatColor.DARK_RED;
		}
	}

	public static boolean alertCaster(LivingEntity caster, ItemStack item, String text, boolean forceAlert) {
		return alertCaster(caster, item.getItemMeta().getDisplayName(), text, forceAlert);
	}

	public static boolean alertCaster(LivingEntity caster, String name, String text, boolean forceAlert) {
		if (caster instanceof Player) {
			if (EntityStats.getEntityStats(caster).isVerbose() || forceAlert) {
				caster.sendMessage(ChatColor.WHITE + "[" + name + ChatColor.WHITE + "]" + ChatColor.WHITE + " " + text);
				return true;
			}
		}
		return false;
	}

	public static boolean alertTarget(LivingEntity target, LivingEntity caster, ItemStack item, String text, boolean forceAlert) {
		return alertTarget(target, caster, item.getItemMeta().getDisplayName(), text, forceAlert);
	}

	public static boolean alertTarget(LivingEntity target, LivingEntity caster, String name, String text, boolean forceAlert) {
		if (target instanceof Player) {
			if (EntityStats.getEntityStats(caster).isVerbose() || forceAlert) {
				ChatColor color = ChatColor.WHITE;
				if (caster instanceof Player) {
					color = ChatColor.BLUE;
				}

				target.sendMessage("[" + color + getAlertName(caster) + ChatColor.RESET + "->" + name + ChatColor.RESET + ChatColor.WHITE + "]"
						+ ChatColor.WHITE + " " + text);
				return true;
			}
		}
		return false;
	}
}
