package dev.itsrealperson.permadeath.api.entity;

import org.bukkit.entity.Giant;

/**
 * Representa un Gigante personalizado con IA de Zombie.
 */
public interface CustomGiant extends PermadeathEntity {

    @Override
    default String getInternalName() { return "CustomGiant"; }

    @Override
    Giant getBukkitEntity();
}
