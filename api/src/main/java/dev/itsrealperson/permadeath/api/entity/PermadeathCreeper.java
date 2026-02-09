package dev.itsrealperson.permadeath.api.entity;

import org.bukkit.entity.Creeper;

/**
 * Representa un Creeper personalizado de Permadeath (Ender, Quantum, etc).
 */
public interface PermadeathCreeper extends PermadeathEntity {

    @Override
    default String getInternalName() { return "CustomCreeper"; }

    @Override
    Creeper getBukkitEntity();
}
