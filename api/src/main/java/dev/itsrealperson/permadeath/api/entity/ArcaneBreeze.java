package dev.itsrealperson.permadeath.api.entity;

import org.bukkit.entity.Breeze;

/**
 * Representa un Arcane Breeze.
 */
public interface ArcaneBreeze extends PermadeathEntity {

    @Override
    default String getInternalName() { return "ArcaneBreeze"; }

    @Override
    Breeze getBukkitEntity();
}
