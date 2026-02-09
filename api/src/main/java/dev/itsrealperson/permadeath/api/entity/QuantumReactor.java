package dev.itsrealperson.permadeath.api.entity;

import org.bukkit.entity.ArmorStand;

/**
 * Representa un Quantum Reactor, una torreta defensiva avanzada.
 */
public interface QuantumReactor extends PermadeathEntity {

    @Override
    default String getInternalName() { return "QuantumReactor"; }

    @Override
    ArmorStand getBukkitEntity();
}
