package dev.itsrealperson.permadeath.api.entity;

import org.bukkit.entity.Pig;

/**
 * Representa un Cerdo especial de Permadeath.
 */
public interface SpecialPig extends PermadeathEntity {

    @Override
    default String getInternalName() { return "SpecialPig"; }

    @Override
    Pig getBukkitEntity();
}
