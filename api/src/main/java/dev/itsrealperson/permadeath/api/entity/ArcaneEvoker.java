package dev.itsrealperson.permadeath.api.entity;

import org.bukkit.entity.Evoker;

/**
 * Representa un Arcane Evoker con magias avanzadas.
 */
public interface ArcaneEvoker extends PermadeathEntity {

    @Override
    default String getInternalName() { return "ArcaneEvoker"; }

    @Override
    Evoker getBukkitEntity();
}
