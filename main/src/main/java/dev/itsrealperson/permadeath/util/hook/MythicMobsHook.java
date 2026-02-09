package dev.itsrealperson.permadeath.util.hook;

import io.lumine.mythic.bukkit.events.MythicMobSpawnEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import dev.itsrealperson.permadeath.Main;

/**
 * Hook para integrar MythicMobs con el escalado de Permadeath.
 */
public class MythicMobsHook implements Listener {

    private final Main plugin;

    public MythicMobsHook(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMythicSpawn(MythicMobSpawnEvent event) {
        if (event.getEntity() instanceof LivingEntity liv) {
            // Aplicar los efectos de escalado de día (Día 60+)
            // Esto permite que los jefes de MythicMobs también ganen vida y daño extra
            plugin.deathTrainEffects(liv);
        }
    }
}
