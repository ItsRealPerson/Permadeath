package dev.itsrealperson.permadeath.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Evento disparado cuando un Death Train comienza o termina.
 */
public class DeathTrainEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final int version;
    private final boolean starting;

    public DeathTrainEvent(int version, boolean starting) {
        this.version = version;
        this.starting = starting;
    }

    /**
     * @return La versión (contador) del Death Train.
     */
    public int getVersion() {
        return version;
    }

    /**
     * @return true si el evento está comenzando, false si está terminando.
     */
    public boolean isStarting() {
        return starting;
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
