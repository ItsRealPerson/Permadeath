package dev.itsrealperson.permadeath.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Evento disparado antes de procesar el permaban de un jugador.
 * Permite a los addons proteger al jugador o cambiar las consecuencias de la muerte.
 */
public class PermadeathPlayerDeathEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final long day;
    private boolean banned;
    private boolean cancelled;

    public PermadeathPlayerDeathEvent(Player player, long day, boolean banned) {
        this.player = player;
        this.day = day;
        this.banned = banned;
    }

    public Player getPlayer() {
        return player;
    }

    public long getDay() {
        return day;
    }

    /**
     * @return true si el jugador será baneado por el Core.
     */
    public boolean isBanned() {
        return banned;
    }

    /**
     * Define si el jugador será baneado.
     */
    public void setBanned(boolean banned) {
        this.banned = banned;
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
