package dev.itsrealperson.permadeath.api.addons.providers;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;

/**
 * Permite a un addon interceptar y modificar el spawneo natural de criaturas.
 */
public interface SpawnProvider {

    /**
     * Se ejecuta cuando el servidor intenta spawnear una criatura.
     * @param type El tipo de entidad original.
     * @param location Ubicación del spawn.
     * @param reason Razón del spawn.
     * @return El resultado del spawn: 
     *         - null para dejar que el Core decida.
     *         - Un nuevo EntityType para transformarlo.
     *         - Un ID de entidad personalizada del addon.
     */
    SpawnResult handleSpawn(EntityType type, Location location, CreatureSpawnEvent.SpawnReason reason);

    /**
     * Representa la decisión del addon sobre un spawn.
     */
    record SpawnResult(Decision decision, String customMobId, EntityType fallbackType) {
        public enum Decision {
            CANCEL,      // Cancela el spawn por completo.
            REPLACE,     // Reemplaza por un EntityType de vainilla.
            CUSTOM,      // Reemplaza por un Mob personalizado del addon.
            IGNORE       // Deja que Permadeath Core siga con su lógica normal.
        }
    }
}
