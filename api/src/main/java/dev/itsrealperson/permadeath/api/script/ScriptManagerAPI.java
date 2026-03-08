package dev.itsrealperson.permadeath.api.script;

import java.util.Collection;

/**
 * Interfaz para gestionar el motor de scripts global de Permadeath.
 */
public interface ScriptManagerAPI {

    /**
     * @return true si el sistema de scripts está habilitado en el config.
     */
    boolean isEnabled();

    /**
     * Registra una nueva acción global que puede ser invocada desde YAML.
     * @param id El nombre de la acción (ej: "broadcast").
     * @param action La implementación de la acción.
     */
    void registerAction(String id, ScriptAction action);

    /**
     * Registra una nueva condición global para scripts YAML.
     * @param id El nombre de la condición (ej: "is_day").
     * @param condition La implementación de la condición.
     */
    void registerCondition(String id, ScriptCondition condition);

    /**
     * Comprueba si un módulo de lógica interna del Core ha sido sobrescrito por un script.
     * @param logicId El ID del módulo (ej: "CORE_DAY_20_MOBS").
     * @return true si debe ignorarse la lógica nativa.
     */
    boolean isLogicOverridden(String logicId);

    /**
     * Dispara un activador de script global.
     * @param trigger El tipo de evento (ej: ENTITY_SPAWN).
     * @param caster La entidad que origina el evento (opcional).
     * @param target La entidad objetivo (opcional).
     * @param context Mapa de variables adicionales de contexto (ej: {day=20}).
     */
    void fireTrigger(ScriptTrigger trigger, org.bukkit.entity.Entity caster, org.bukkit.entity.Entity target, java.util.Map<String, Object> context);

    /**
     * Recarga todos los scripts desde la carpeta de configuración.
     */
    void reloadScripts();

    /**
     * @return Los IDs de todas las acciones registradas actualmente.
     */
    Collection<String> getRegisteredActions();

    /**
     * @return Los IDs de todas las condiciones registradas actualmente.
     */
    Collection<String> getRegisteredConditions();
}
