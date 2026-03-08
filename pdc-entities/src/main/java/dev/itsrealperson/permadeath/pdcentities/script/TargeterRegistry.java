package dev.itsrealperson.permadeath.pdcentities.script;

import dev.itsrealperson.permadeath.pdcentities.script.targeter.Targeter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Registro de selectores de objetivos.
 */
public class TargeterRegistry {

    private static final Map<String, Function<Map<String, String>, Targeter>> targeters = new HashMap<>();

    public static void register(String id, Function<Map<String, String>, Targeter> factory) {
        targeters.put(id.toLowerCase(), factory);
    }

    public static Targeter create(String id, Map<String, String> args) {
        Function<Map<String, String>, Targeter> factory = targeters.get(id.toLowerCase());
        if (factory == null) {
            dev.itsrealperson.permadeath.pdcentities.PdcEntitiesAddon.debug("§c[Registry] Selector no encontrado: " + id);
            return null;
        }
        return factory.apply(args);
    }
}
