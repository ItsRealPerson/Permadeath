package dev.itsrealperson.permadeath.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Evento disparado cuando el día de Permadeath cambia.
 */
public class PermadeathDayChangeEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final long oldDay;
    private final long newDay;

    public PermadeathDayChangeEvent(long oldDay, long newDay) {
        this.oldDay = oldDay;
        this.newDay = newDay;
    }

    public long getOldDay() {
        return oldDay;
    }

    public long getNewDay() {
        return newDay;
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
