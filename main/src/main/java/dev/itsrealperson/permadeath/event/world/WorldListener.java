package dev.itsrealperson.permadeath.event.world;

import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.api.script.ScriptTrigger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.LightningStrikeEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Escucha eventos del mundo y del clima para el motor de scripts.
 */
public class WorldListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLightning(LightningStrikeEvent event) {
        Map<String, Object> context = new HashMap<>();
        context.put("loc_x", event.getLightning().getLocation().getX());
        context.put("loc_y", event.getLightning().getLocation().getY());
        context.put("loc_z", event.getLightning().getLocation().getZ());
        
        Main.getInstance().getScriptManager().fireTrigger(ScriptTrigger.LIGHTNING_STRIKE, null, event.getLightning(), context);
    }
}
