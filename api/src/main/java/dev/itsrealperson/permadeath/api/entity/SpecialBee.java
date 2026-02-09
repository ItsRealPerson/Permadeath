package dev.itsrealperson.permadeath.api.entity;

import org.bukkit.entity.Bee;

/**
 * Representa una Abeja especial de Permadeath.
 */
public interface SpecialBee extends PermadeathEntity {

    @Override
    default String getInternalName() { return "SpecialBee"; }

    @Override
    Bee getBukkitEntity();
}
