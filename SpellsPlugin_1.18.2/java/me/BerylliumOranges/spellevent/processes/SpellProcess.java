package me.BerylliumOranges.spellevent.processes;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import me.BerylliumOranges.main.SpellPluginMain;
import me.BerylliumOranges.misc.GachaItems;
import me.BerylliumOranges.misc.GachaItems.ItemTrait;
import me.BerylliumOranges.spellevent.entity_information.EntityStats;
import me.BerylliumOranges.spellevent.entity_information.SpellSlot;
import me.BerylliumOranges.spellevent.other.custom_events.SpellCastEvent;
import me.BerylliumOranges.spellevent.segments.abstracts.EventSegment;
import me.BerylliumOranges.spellevent.segments.b_triggers.armor_triggers.WhenNearbyProjectile;
import me.BerylliumOranges.spellevent.segments.g_spell_focus.Hijack;
import net.md_5.bungee.api.ChatColor;

public class SpellProcess implements Cloneable {
	public static ArrayList<LivingEntity> entityAttackCooldowns = new ArrayList<LivingEntity>();
	public static ArrayList<LivingEntity> entityArmorCooldowns = new ArrayList<LivingEntity>();
	public static SpellPluginMain plugin = SpellPluginMain.getInstance();
	private LivingEntity user;
	private ArrayList<LivingEntity> targets;
	private ItemStack[][][] userSegments;
	private ArrayList<ItemStack> userContainers;
	private Location spellLocation;
	private boolean useDurability = true;
	private double maximumAllocatedMana = Double.MAX_VALUE;
	private int minManaMultiplier = 0;
	private EntityEvent event = null;
	private boolean autoCasted = false;
	private ArrayList<Map.Entry<ItemStack, HashMap<Integer, ItemStack>>> loadedSegments = new ArrayList<>();
	private List<String> processTags = Arrays.asList(EventSegment.GENERAL_TRIGGER_TAG);

	public static ItemStack findItemByLocalName(LivingEntity ent, String localName) {
		ArrayList<ItemStack> possibilities = new ArrayList<ItemStack>();
		possibilities.add(ent.getEquipment().getItemInMainHand());
		possibilities.addAll(Arrays.asList(ent.getEquipment().getArmorContents()));
		if (ent.getType().equals(EntityType.PLAYER)) {
			Player p = (Player) ent;
			possibilities.addAll(Arrays.asList(p.getInventory().getStorageContents()));
		}

		for (ItemStack item : possibilities) {
			if (item != null && item.hasItemMeta() && item.getItemMeta().getLocalizedName().equals(localName)) {
				return item;
			}
		}
		return null;
	}

	public SpellProcess(LivingEntity caster, ArrayList<LivingEntity> targets) {
		user = (LivingEntity) caster;
		spellLocation = caster.getEyeLocation().clone().subtract(0, 0.3, 0).add(caster.getEyeLocation().getDirection());
		if (targets == null)
			this.targets = new ArrayList<LivingEntity>();
		else
			this.targets = targets;

		userContainers = getToolContainers(user);
	}

	public void start() {
		userSegments = getSegments(userContainers);
		process(userSegments, userContainers, user);
	}

	ChatColor[] colors = { ChatColor.DARK_PURPLE, ChatColor.GOLD, ChatColor.YELLOW, ChatColor.WHITE };

	@SuppressWarnings("unchecked")
	public void process(ItemStack[][][] segments, ArrayList<ItemStack> containers, LivingEntity owner) {
		EntityStats stat = EntityStats.getEntityStats(owner);
		if (stat.isMuted()) {
			stat.sendSpellCooldowns(true);
			Location loc = stat.getEntity().getEyeLocation().add(stat.getEntity().getLocation().getDirection().clone().multiply(0.5));
			loc.getWorld().spawnParticle(Particle.SMOKE_NORMAL, loc, 0);
			loc.getWorld().playSound(loc, Sound.BLOCK_FIRE_EXTINGUISH, 0.1F, 2F);
			return;
		}
		for (int i = 0; i < containers.size(); i++) {
			ItemStack originalContainer = containers.get(i);
			containers.set(i, (ItemStack) containers.get(i).clone());
			// if(
			boolean delete = false;
			LivingEntity ent = user;
			int invLoc = -1;
			int[] pos = null;
			if (containers.get(i) != null) {
				pos = findTrigger(segments[i], invLoc + 1);
				// Huge bug:
				// containers.get(i).setAmount(1);
			}
			if (pos == null)
				continue;

			int y = pos[0];
			int x = pos[1];
			invLoc = (y * 9) + x;

			SegmentProcess proc = new SegmentProcess(segments[i], containers.get(i), y, x,
					SpellActions.getArrowDirectionFromCustomModel(segments[i][y][x]), owner, targets, spellLocation, stat, this);
			try {
				proc.setEvent(event);
				proc.setProcessTags(processTags);
				proc.setMinManaMultiplier(minManaMultiplier);

				boolean cancel = false;
				if (!autoCasted) {
					SpellCastEvent event = new SpellCastEvent(owner, (SpellProcess) this.clone(), proc);
					Bukkit.getServer().getPluginManager().callEvent(event);
					cancel = event.isCancelled();
				}

				if (processTags.contains(EventSegment.NEARBY_PROJECTILE_TRIGGER_TAG)) {
					if (proc.getStats().segmentCooldowns.containsKey(WhenNearbyProjectile.class)) {
						cancel = true;
					}
				}

				if (!cancel && EventSegment.hasTag(proc,
						EventSegment.getSegmentClassFromLocalName(proc.getPresentSegments()[y][x].getItemMeta().getLocalizedName()))) {
					if (originalContainer.getItemMeta() instanceof Damageable) {
						Damageable d = (Damageable) originalContainer.getItemMeta();
						if (d.getDamage() == originalContainer.getType().getMaxDurability()) {
							if (EventItemStorage.getRarityOfItem(d.getLocalizedName()) < 2)
								delete = true;
							else
								continue;
						} else {

							if (1 / (double) (d.getEnchantLevel(Enchantment.DURABILITY) + 1) > Math.random()) {
								int damage = 3;
								if (GachaItems.isArmor(originalContainer.getType()))
									damage = 1;
								d.setDamage(Math.min(d.getDamage() + damage, originalContainer.getType().getMaxDurability()));
							}
							originalContainer.setItemMeta(d);
						}
					} else {
						delete = true;
					}

					SpellSlot slot = stat.addSpell(proc);

					if (slot != null) {
						slot.setAllocatedMana(maximumAllocatedMana);
						proc.setSpellSlot(slot);

						HashMap<ItemTrait, Double> traits = GachaItems.getAllTraits(proc.getCaster());
						proc.setTraits(traits);
						int crit = GachaItems.doesCrit(proc.getCaster(), GachaItems.getSpecificTrait(traits, ItemTrait.CRIT_CHANCE))
								* EventSegment.CRIT_MULTIPLIER;

						if (crit > 0) {
							String msg = ChatColor.DARK_PURPLE + "Critical Strike";
							if (crit > EventSegment.CRIT_MULTIPLIER)
								msg = colors[(int) (Math.min(colors.length - 1, crit / EventSegment.CRIT_MULTIPLIER))] + "Critical Strike x"
										+ (1 + (crit / EventSegment.CRIT_MULTIPLIER));
							EventSegment.alertCaster(user, msg, "Spell was upgraded " + crit + " times", false);
						}
						proc.setPureCrit(crit);
						proc.setCrit(crit);
						EventSegment.callNext(proc, true);
						if (i == 0 && ent.getEquipment().getItemInMainHand().equals(containers.get(0))) {
							entityAttackCooldowns.add(ent);
							Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
								public void run() {
									entityAttackCooldowns.remove(ent);
								}
							}, 1L);
						}
						stat.setLastCast(proc);
					}
				}
			} catch (Exception er) {
				er.printStackTrace();
			}
			if (delete) {
				originalContainer.setAmount(originalContainer.getAmount() - 1);
				if (owner instanceof Player) {
					((Player) owner).updateInventory();
				}
			}
		}
	}

	public static int[] findTrigger(ItemStack[][] input, int invLoc) {
		for (int i = invLoc; i < input.length * input[0].length; i++) {
			int x = i % 9;
			int y = i / 9;
			if (input[y][x] != null && input[y][x].hasItemMeta()) {
				if (input[y][x].getItemMeta().getLocalizedName().startsWith(EventSegment.SEGMENT_TYPE_TRIGGER)) {
					return new int[] { y, x };
				}
			}
		}
		return null;
	}

	public static ArrayList<ItemStack> getToolContainers(LivingEntity ent) {
		ArrayList<ItemStack> tools = new ArrayList<ItemStack>();
		if (!entityAttackCooldowns.contains(ent))
			tools.add(ent.getEquipment().getItemInMainHand());
		return tools;
	}

	public static ArrayList<ItemStack> getArmorContainers(LivingEntity ent) {
		ArrayList<ItemStack> armors = new ArrayList<ItemStack>();
		if (!entityArmorCooldowns.contains(ent)) {
			for (ItemStack item : ent.getEquipment().getArmorContents()) {
				if (item != null && item.hasItemMeta())
					armors.add(item);
			}
		}
		return armors;
	}

	public ItemStack[][][] getSegments(ArrayList<ItemStack> containers) {

		ItemStack[][][] segments = new ItemStack[containers.size()][6][9];

		for (int i = 0; i < containers.size(); i++) {
			ItemStack item = containers.get(i).clone();
			try {
				if (item != null && item.hasItemMeta() && item.getItemMeta().getLocalizedName().startsWith("EventItem")) {
					for (Hijack.HijackedItem h : Hijack.hijackedSpells) {
						if (h.getItem().getItemMeta().getLocalizedName().equals(item.getItemMeta().getLocalizedName())) {
							ItemMeta meta = item.getItemMeta();
							meta.setLocalizedName(h.getLocalName());
							item.setItemMeta(meta);
							maximumAllocatedMana = h.getMaxManaUse();
							minManaMultiplier = 1;
							break;
						}
					}

					HashMap<Integer, ItemStack> info = EventItemStorage.loadData(item.getItemMeta().getLocalizedName(), false).getItemSnapshot();// crashes
					loadedSegments.add(new AbstractMap.SimpleEntry<ItemStack, HashMap<Integer, ItemStack>>(item.clone(), info));// here
					for (Map.Entry<Integer, ItemStack> entry : info.entrySet()) {
						segments[i][entry.getKey() / 9][entry.getKey() % 9] = entry.getValue();
					}

				}
			} catch (Exception er) {
			}
		}
		return segments;
	}

	public static ArrayList<LivingEntity> getEntityAttackCooldowns() {
		return entityAttackCooldowns;
	}

	public static void setEntityAttackCooldowns(ArrayList<LivingEntity> entityAttackCooldowns) {
		SpellProcess.entityAttackCooldowns = entityAttackCooldowns;
	}

	public static ArrayList<LivingEntity> getEntityArmorCooldowns() {
		return entityArmorCooldowns;
	}

	public static void setEntityArmorCooldowns(ArrayList<LivingEntity> entityArmorCooldowns) {
		SpellProcess.entityArmorCooldowns = entityArmorCooldowns;
	}

	public ItemStack[][][] getUserSegments() {
		return userSegments;
	}

	public void setUserSegments(ItemStack[][][] userSegments) {
		this.userSegments = userSegments;
	}

	public LivingEntity getUser() {
		return user;
	}

	public void setUser(LivingEntity user) {
		this.user = user;
	}

	public ArrayList<ItemStack> getUserContainers() {
		return userContainers;
	}

	public void setUserContainers(ArrayList<ItemStack> userContainers) {
		this.userContainers = userContainers;
	}

	public Location getSpellLocation() {
		return spellLocation;
	}

	public void setSpellLocation(Location spellLocation) {
		this.spellLocation = spellLocation;
	}

	public EntityEvent getEvent() {
		return event;
	}

	public void setEvent(EntityEvent event) {
		this.event = event;
	}

	public List<String> getProcessTags() {
		return processTags;
	}

	public void setProcessTags(List<String> processTags) {
		this.processTags = processTags;
	}

	public ArrayList<Map.Entry<ItemStack, HashMap<Integer, ItemStack>>> getLoadedSegments() {
		return loadedSegments;
	}

	public void setLoadedSegments(ArrayList<Map.Entry<ItemStack, HashMap<Integer, ItemStack>>> loadedSegments) {
		this.loadedSegments = loadedSegments;
	}

	public int getMinManaMultiplier() {
		return minManaMultiplier;
	}

	public void setMinManaMultiplier(int minManaMultiplier) {
		this.minManaMultiplier = minManaMultiplier;
	}

	public boolean isAutoCasted() {
		return autoCasted;
	}

	public void setAutoCasted(boolean autoCasted) {
		this.autoCasted = autoCasted;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object clone() throws CloneNotSupportedException {
		SpellProcess clone = (SpellProcess) super.clone();
		clone.targets = (ArrayList<LivingEntity>) targets.clone();
		clone.spellLocation = (Location) spellLocation.clone();
		return clone;
	}
}
