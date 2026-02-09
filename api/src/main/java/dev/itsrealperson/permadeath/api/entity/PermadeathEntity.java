package dev.itsrealperson.permadeath.api.entity;

import org.bukkit.entity.LivingEntity;

/**
 * Interfaz base para todas las entidades personalizadas de Permadeath.
 */
public interface PermadeathEntity {

    /**
     * @return El nombre único identificador de esta entidad (ej: "UltraRavager").
     */
    String getInternalName();

    /**
     * @return La entidad de Bukkit subyacente.
     */
    LivingEntity getBukkitEntity();
}
