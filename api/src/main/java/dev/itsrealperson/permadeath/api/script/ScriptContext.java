package dev.itsrealperson.permadeath.api.script;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;

/**
 * Mantiene el contexto de ejecución de un script.
 * Permite acceder a variables y placeholders durante la ejecución.
 */
public class ScriptContext {

    private final Entity caster;
    private final Entity target;
    private final Map<String, Object> metadata;

    public ScriptContext(Entity caster, Entity target, Map<String, Object> metadata) {
        this.caster = caster;
        this.target = target;
        this.metadata = (metadata != null ? metadata : new HashMap<>());
    }

    /**
     * Reemplaza placeholders en una cadena basándose en el contexto actual.
     * Ejemplo: "Hola %player_name%" -> "Hola ItsRealPerson"
     */
    public String replacePlaceholders(String text) {
        if (text == null) return null;

        // --- PLACEHOLDERS DE CASTER (EL QUE ORIGINA) ---
        if (caster != null) {
            text = text.replace("%caster_name%", caster.getName());
            text = text.replace("%caster_uuid%", caster.getUniqueId().toString());
            text = text.replace("%caster_type%", caster.getType().name());
            if (caster instanceof Player p) {
                text = text.replace("%caster_hp%", String.valueOf((int) p.getHealth()));
                var attr = p.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH);
                if (attr != null) {
                    text = text.replace("%caster_max_hp%", String.valueOf((int) attr.getValue()));
                }
            }
        }

        // --- PLACEHOLDERS DE TARGET (EL OBJETIVO) ---
        if (target != null) {
            text = text.replace("%target_name%", target.getName());
            text = text.replace("%target_uuid%", target.getUniqueId().toString());
            text = text.replace("%target_type%", target.getType().name());
            if (target instanceof Player p) {
                text = text.replace("%target_hp%", String.valueOf((int) p.getHealth()));
            }
        }

        // --- PLACEHOLDERS GLOBALES ---
        text = text.replace("%pdc_day%", String.valueOf(dev.itsrealperson.permadeath.api.PermadeathAPI.getDay()));
        
        // Placeholders de Metadata (del evento)
        for (Map.Entry<String, Object> entry : metadata.entrySet()) {
            text = text.replace("%" + entry.getKey() + "%", entry.getValue().toString());
        }

        return text;
    }

    public Entity getCaster() { return caster; }
    public Entity getTarget() { return target; }
    public Map<String, Object> getMetadata() { return metadata; }
}
