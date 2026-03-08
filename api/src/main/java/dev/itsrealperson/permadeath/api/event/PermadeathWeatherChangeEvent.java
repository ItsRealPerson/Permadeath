package dev.itsrealperson.permadeath.api.event;

import org.bukkit.World;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Evento disparado cuando Permadeath intenta cambiar el clima (ej: activar tormenta).
 */
public class PermadeathWeatherChangeEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private final World world;
    private final WeatherType type;
    private long duration;
    private boolean cancelled;

    public PermadeathWeatherChangeEvent(World world, WeatherType type, long duration) {
        this.world = world;
        this.type = type;
        this.duration = duration;
    }

    public World getWorld() {
        return world;
    }

    public WeatherType getType() {
        return type;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
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

    public enum WeatherType {
        STORM,
        THUNDER,
        CLEAR
    }
}
