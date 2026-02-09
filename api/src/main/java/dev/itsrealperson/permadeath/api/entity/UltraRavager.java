package dev.itsrealperson.permadeath.api.entity;

import org.bukkit.entity.Ravager;

/**
 * Representa un Ultra Ravager con capacidades destructivas.
 */
public interface UltraRavager extends PermadeathEntity {

    @Override
    default String getInternalName() { return "UltraRavager"; }

    @Override
    Ravager getBukkitEntity();

    /**
     * Realiza la lógica de destrucción de bloques.
     */
    void performBlockDestruction();
}
