package dev.itsrealperson.permadeath.api.entity;

import org.bukkit.entity.Silverfish;

/**
 * Representa un Sculk Parasite, un organismo que debilita a sus víctimas.
 */
public interface SculkParasite extends PermadeathEntity {

    @Override
    default String getInternalName() { return "SculkParasite"; }

    @Override
    Silverfish getBukkitEntity();
}
