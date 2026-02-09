package dev.itsrealperson.permadeath.api.entity;

import org.bukkit.entity.Bat;

/**
 * Representa un Gloom Bat, un murciélago explorador del vacío.
 */
public interface GloomBat extends PermadeathEntity {

    @Override
    default String getInternalName() { return "GloomBat"; }

    @Override
    Bat getBukkitEntity();
}
