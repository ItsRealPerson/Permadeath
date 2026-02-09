package dev.itsrealperson.permadeath.api.event;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Evento disparado cuando un mob está a punto de ser escalado por Permadeath.
 * Permite a los addons modificar o añadir atributos adicionales.
 */
public class PermadeathMobScaleEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final LivingEntity entity;
    private final long currentDay;
    private double healthMultiplier;
    private double damageMultiplier;

    public PermadeathMobScaleEvent(LivingEntity entity, long currentDay, double healthMultiplier, double damageMultiplier) {
        this.entity = entity;
        this.currentDay = currentDay;
        this.healthMultiplier = healthMultiplier;
        this.damageMultiplier = damageMultiplier;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public long getCurrentDay() {
        return currentDay;
    }

    public double getHealthMultiplier() {
        return healthMultiplier;
    }

    public void setHealthMultiplier(double healthMultiplier) {
        this.healthMultiplier = healthMultiplier;
    }

    public double getDamageMultiplier() {
        return damageMultiplier;
    }

    public void setDamageMultiplier(double damageMultiplier) {
        this.damageMultiplier = damageMultiplier;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
