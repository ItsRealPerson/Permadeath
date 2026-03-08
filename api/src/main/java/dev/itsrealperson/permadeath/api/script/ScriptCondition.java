package dev.itsrealperson.permadeath.api.script;

import java.util.Map;

/**
 * Representa una condición que debe cumplirse en un script YAML con soporte de contexto.
 */
@FunctionalInterface
public interface ScriptCondition {
    /**
     * Evalúa si se cumple la condición.
     * @param context El contexto de ejecución.
     * @param args Los argumentos pasados a la condición.
     * @return true si se cumple, false en caso contrario.
     */
    boolean test(ScriptContext context, Map<String, String> args);
}
