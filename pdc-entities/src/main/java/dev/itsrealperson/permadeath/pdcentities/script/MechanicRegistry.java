package dev.itsrealperson.permadeath.pdcentities.script;

import dev.itsrealperson.permadeath.pdcentities.script.mechanic.Mechanic;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Registro central de mecánicas disponibles en el sistema de scripting.
 */
public class MechanicRegistry {

    private static final Map<String, Function<Map<String, String>, Mechanic>> mechanics = new HashMap<>();

    /**
     * Registra una nueva mecánica que puede ser usada en los YAMLs.
     * @param id El nombre de la mecánica (ej: "damage").
     * @param factory Función que crea la mecánica a partir de los argumentos del YAML.
     */
    public static void register(String id, Function<Map<String, String>, Mechanic> factory) {
        mechanics.put(id.toLowerCase(), factory);
    }

    public static Mechanic create(String id, Map<String, String> args) {
        Function<Map<String, String>, Mechanic> factory = mechanics.get(id.toLowerCase());
        if (factory == null) {
            dev.itsrealperson.permadeath.pdcentities.PdcEntitiesAddon.debug("§c[Registry] Mecánica no encontrada: " + id);
            return null;
        }
        return factory.apply(args);
    }
}
