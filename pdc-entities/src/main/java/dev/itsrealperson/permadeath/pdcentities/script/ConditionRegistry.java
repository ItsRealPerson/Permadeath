package dev.itsrealperson.permadeath.pdcentities.script;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Registro central de condiciones lógicas.
 */
public class ConditionRegistry {

    private static final Map<String, Function<Map<String, String>, Condition>> conditions = new HashMap<>();

    public static void register(String id, Function<Map<String, String>, Condition> factory) {
        conditions.put(id.toLowerCase(), factory);
    }

    public static Condition create(String id, Map<String, String> args) {
        Function<Map<String, String>, Condition> factory = conditions.get(id.toLowerCase());
        return factory != null ? factory.apply(args) : null;
    }
}
