package dev.itsrealperson.permadeath.api.script;

import java.util.Map;

/**
 * Representa una acción ejecutable mediante scripts YAML con soporte de contexto.
 */
@FunctionalInterface
public interface ScriptAction {
    /**
     * Ejecuta la acción.
     * @param context El contexto de ejecución (caster, target, variables).
     * @param args Los argumentos pasados a la acción desde el YAML (sin procesar).
     */
    void execute(ScriptContext context, Map<String, String> args);
}
