package dev.itsrealperson.permadeath.api.entity;

import org.bukkit.entity.Creeper;

/**
 * Representa un Abyssal Creeper.
 */
public interface AbyssalCreeper extends PermadeathEntity {

    @Override
    default String getInternalName() { return "AbyssalCreeper"; }

    @Override
    Creeper getBukkitEntity();
}
