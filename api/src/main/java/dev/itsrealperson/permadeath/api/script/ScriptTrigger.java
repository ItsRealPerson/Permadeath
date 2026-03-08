package dev.itsrealperson.permadeath.api.script;

/**
 * Lista de disparadores globales para el sistema de scripts.
 * Representan momentos clave donde la lógica del Core puede ser intervenida.
 */
public enum ScriptTrigger {
    // --- EVENTOS DE ENTIDAD ---
    ENTITY_SPAWN,      // Al aparecer cualquier entidad
    ENTITY_DEATH,      // Al morir
    ENTITY_DAMAGE,     // Al recibir daño
    ENTITY_INTERACT,   // Al interactuar (clic derecho)
    
    // --- EVENTOS DE JUGADOR ---
    PLAYER_JOIN,       // Al entrar al servidor
    PLAYER_DEATH,      // Al morir (Baneo/Mensaje)
    PLAYER_PORTAL,     // Al usar portales (Nether/End/Abismo)
    PLAYER_TICK,       // Cada segundo por cada jugador
    
    // --- EVENTOS DE MUNDO ---
    DAY_CHANGE,        // Cada vez que cambia el día de Permadeath
    STORM_START,       // Al empezar una tormenta
    RAID_START,        // Al empezar una raid
    ITEM_CONSUME,      // Al comer o beber algo
    LIGHTNING_STRIKE,  // Al impactar un rayo
    
    // --- EVENTOS DE MUNDO ---
    BLOCK_BREAK,       // Al romper un bloque
    BLOCK_PLACE,       // Al colocar un bloque
    BUCKET_FILL,       // Al llenar un cubo (agua/lava)
    FURNACE_SMELT,     // Al fundir un ítem en horno
    
    // --- SISTEMA ---
    TICK_1S,           // Cada segundo
    TICK_1M,           // Cada minuto
    CUSTOM             // Para disparadores manuales de otros addons
}
