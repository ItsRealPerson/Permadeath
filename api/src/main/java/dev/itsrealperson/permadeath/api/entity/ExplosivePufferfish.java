package dev.itsrealperson.permadeath.api.entity;

import org.bukkit.entity.PufferFish;

/**
 * Representa un Pez Globo explosivo.
 */
public interface ExplosivePufferfish extends PermadeathEntity {

    @Override
    default String getInternalName() { return "ExplosivePufferfish"; }

    @Override
    PufferFish getBukkitEntity();
}
