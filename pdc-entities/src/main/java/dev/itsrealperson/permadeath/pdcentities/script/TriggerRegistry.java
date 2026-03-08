package dev.itsrealperson.permadeath.pdcentities.script;

import java.util.HashSet;
import java.util.Set;

/**
 * Permite registrar nombres de disparadores personalizados.
 */
public class TriggerRegistry {
    private static final Set<String> customTriggers = new HashSet<>();

    public static void register(String triggerName) {
        customTriggers.add(triggerName.toLowerCase());
    }

    public static Set<String> getCustomTriggers() {
        return customTriggers;
    }
}
