package dev.itsrealperson.permadeath.api.entity;

import org.bukkit.entity.Warden;

/**
 * Representa un Warden Retorcido (Twisted Warden) con habilidades de jefe.
 */
public interface CustomWarden extends PermadeathEntity {

    @Override
    default String getInternalName() { return "CustomWarden"; }

    @Override
    Warden getBukkitEntity();

    /**
     * Realiza el ataque de rugido sónico manual.
     */
    void performSonicBoom();

    /**
     * Invoca secuaces (minions) personalizados.
     */
    void summonMinions();
}
