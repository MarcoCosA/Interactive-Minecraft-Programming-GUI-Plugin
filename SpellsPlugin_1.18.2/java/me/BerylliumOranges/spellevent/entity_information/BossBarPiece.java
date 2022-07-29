package me.BerylliumOranges.spellevent.entity_information;

import java.io.Serializable;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import me.BerylliumOranges.spellevent.entity_information.entityprefabs.bosses.AbstractBoss;

public class BossBarPiece implements Serializable {
	private static final long serialVersionUID = 5581689679449306544L;
	private String title = "";
	private BarColor color;
	private EntityStats stats;
	boolean triggerOnlyOnHurt;
	private transient BossBar bar = null;
	public static ArrayList<BossBarPiece> bossBars = new ArrayList<BossBarPiece>();

	public BossBarPiece(EntityStats input, String title, BarColor barColor, boolean triggerOnlyOnHurt) {
		stats = input;
		color = barColor;
		this.title = title;
		this.triggerOnlyOnHurt = triggerOnlyOnHurt;
		bossBars.add(this);
		this.updateBossBar();
	}

	@EventHandler
	public void updateBossBar() {
		LivingEntity liv = stats.getEntity();
		if (liv.getHealth() / liv.getMaxHealth() < 1)
			triggerOnlyOnHurt = false;

		if (!triggerOnlyOnHurt) {
			getBar().setProgress(liv.getHealth() / liv.getMaxHealth());
			int mod = 17;
			if (stats.getEntityPrefab() != null && stats.getEntityPrefab() instanceof AbstractBoss) {
				AbstractBoss boss = (AbstractBoss) stats.getEntityPrefab();
				mod += boss.getTier() * 20;
			}
			outerloop: for (Player p : Bukkit.getServer().getOnlinePlayers()) {
				if (stats.getEntityPrefab() != null) {
					for (BossBarPiece b : bossBars) {
						if (b.getBar().getPlayers().contains(p)) {
							if (b.getStats().getEntityPrefab() != null
									&& b.getStats().getEntityPrefab().getTier() > stats.getEntityPrefab().getTier()) {
								bar.removePlayer(p);
								break outerloop;
							}
						}
					}
				}
				if (liv.isDead()) {
					bar.removeAll();
				} else {

					if (p.getLocation().distanceSquared(stats.getEntity().getLocation()) < mod * mod) {
						if (!bar.getPlayers().contains(p)) {
							bar.addPlayer(p);
						}
					} else {
						bar.removePlayer(p);
					}
				}
			}
		}
	}

	public void deleteBar() {
		bar.removeAll();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public BarColor getColor() {
		return color;
	}

	public void setColor(BarColor color) {
		this.color = color;
	}

	public BossBar getBar() {
		if (bar != null)
			return bar;
		bar = Bukkit.createBossBar(title, color, getSegmentation(), BarFlag.DARKEN_SKY);
		return bar;
	}

	public void setBar(BossBar bar) {
		this.bar = bar;
	}

	public BarStyle getSegmentation() {
		LivingEntity liv = stats.getEntity();
		if (liv.getMaxHealth() > 40)
			return BarStyle.SEGMENTED_10;
		if (liv.getMaxHealth() > 80)
			return BarStyle.SEGMENTED_12;
		if (liv.getMaxHealth() > 200)
			return BarStyle.SEGMENTED_20;
		return BarStyle.SEGMENTED_6;
	}

	public EntityStats getStats() {
		return stats;
	}

	public void setStats(EntityStats stats) {
		this.stats = stats;
	}
}
