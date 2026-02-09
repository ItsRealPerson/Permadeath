package dev.itsrealperson.permadeath.api.entity;

import org.bukkit.entity.Stray;

/**
 * Representa un Echo Archer, un arquero que dispara ráfagas sónicas.
 */
public interface EchoArcher extends PermadeathEntity {

    @Override
    default String getInternalName() { return "EchoArcher"; }

    @Override
    Stray getBukkitEntity();
}
