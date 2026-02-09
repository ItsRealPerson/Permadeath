package dev.itsrealperson.permadeath.api.entity;

import org.bukkit.entity.Snowman;

/**
 * Representa un Golem de Nieve hostil.
 */
public interface AggressiveSnowGolem extends PermadeathEntity {

    @Override
    default String getInternalName() { return "AggressiveSnowGolem"; }

    @Override
    Snowman getBukkitEntity();
}
