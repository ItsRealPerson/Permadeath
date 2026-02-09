package dev.itsrealperson.permadeath.api.entity;

import org.bukkit.entity.Husk;

/**
 * Representa un Hollow Guard, un guerrero pesado del vacío.
 */
public interface HollowGuard extends PermadeathEntity {

    @Override
    default String getInternalName() { return "HollowGuard"; }

    @Override
    Husk getBukkitEntity();
}
