package dev.itsrealperson.permadeath.api.entity;

import org.bukkit.entity.IronGolem;

/**
 * Representa un Pale Paragon, un golem de hielo con ataques a distancia.
 */
public interface PaleParagon extends PermadeathEntity {

    @Override
    default String getInternalName() { return "PaleParagon"; }

    @Override
    IronGolem getBukkitEntity();
}
