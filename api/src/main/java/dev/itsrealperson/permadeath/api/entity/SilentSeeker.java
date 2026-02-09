package dev.itsrealperson.permadeath.api.entity;

import org.bukkit.entity.Creeper;

/**
 * Representa un Silent Seeker, una entidad sigilosa con explosión sónica.
 */
public interface SilentSeeker extends PermadeathEntity {

    @Override
    default String getInternalName() { return "SilentSeeker"; }

    @Override
    Creeper getBukkitEntity();
}
