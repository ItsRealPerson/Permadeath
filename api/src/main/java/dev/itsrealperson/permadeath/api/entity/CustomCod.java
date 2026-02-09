package dev.itsrealperson.permadeath.api.entity;

import org.bukkit.entity.Cod;

/**
 * Representa un Bacalao personalizado.
 */
public interface CustomCod extends PermadeathEntity {

    @Override
    default String getInternalName() { return "CustomCod"; }

    @Override
    Cod getBukkitEntity();
}
