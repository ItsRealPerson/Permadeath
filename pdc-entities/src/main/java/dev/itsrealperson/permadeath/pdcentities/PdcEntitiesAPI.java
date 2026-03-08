package dev.itsrealperson.permadeath.pdcentities;

import dev.itsrealperson.permadeath.pdcentities.script.Condition;
import dev.itsrealperson.permadeath.pdcentities.script.ConditionRegistry;
import dev.itsrealperson.permadeath.pdcentities.script.MechanicRegistry;
import dev.itsrealperson.permadeath.pdcentities.script.TargeterRegistry;
import dev.itsrealperson.permadeath.pdcentities.script.TriggerRegistry;
import dev.itsrealperson.permadeath.pdcentities.script.mechanic.Mechanic;
import dev.itsrealperson.permadeath.pdcentities.script.targeter.Targeter;

import java.util.Map;
import java.util.function.Function;

/**
 * Clase principal para que otros addons extiendan PDC-Entities.
 */
public class PdcEntitiesAPI {

    /**
     * Registra una nueva mecánica para usar en los archivos YAML.
     */
    public static void registerMechanic(String id, Function<Map<String, String>, Mechanic> factory) {
        MechanicRegistry.register(id, factory);
    }

    /**
     * Registra un nuevo selector de objetivos (Targeter).
     */
    public static void registerTargeter(String id, Function<Map<String, String>, Targeter> factory) {
        TargeterRegistry.register(id, factory);
    }

    /**
     * Registra una nueva condición.
     */
    public static void registerCondition(String id, Function<Map<String, String>, Condition> factory) {
        ConditionRegistry.register(id, factory);
    }

    /**
     * Registra un trigger personalizado (ej: onMagicAttack).
     */
    public static void registerTrigger(String triggerName) {
        TriggerRegistry.register(triggerName);
    }
}
