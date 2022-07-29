package me.BerylliumOranges.spellevent.entity_information.entityprefabs.ads;

import org.bukkit.event.entity.EntityDeathEvent;

import me.BerylliumOranges.spellevent.entity_information.entityprefabs.AbstractEntityPrefab;
import me.BerylliumOranges.spellevent.entity_information.entityprefabs.bosses.AbstractBoss;

public abstract class AbstractAds extends AbstractEntityPrefab {
	public static final Class<AbstractBoss> PARENT_BOSS = null;

	@Override
	public void triggerDeathEvent(EntityDeathEvent event) {
		event.setDroppedExp(0);
		event.getDrops().clear();
	}

}
