package dev.itsrealperson.permadeath.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Evento disparado cuando el nivel de presión de un jugador en el Abismo cambia.
 * Puede ser cancelado para evitar que la presión cambie.
 */
public class AbyssPressureLevelEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled = false;
    private final double oldLevel;
    private double newLevel;

    public AbyssPressureLevelEvent(@NotNull Player who, double oldLevel, double newLevel) {
        super(who);
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
    }

    public double getOldLevel() {
        return oldLevel;
    }

    public double getNewLevel() {
        return newLevel;
    }

    public void setNewLevel(double newLevel) {
        this.newLevel = newLevel;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
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
