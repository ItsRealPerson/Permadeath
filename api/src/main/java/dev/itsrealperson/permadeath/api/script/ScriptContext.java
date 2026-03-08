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

        // --- PLACEHOLDERS DE CASTER ---
        if (caster != null) {
            text = text.replace("%caster_name%", caster.getName());
            text = text.replace("%caster_uuid%", caster.getUniqueId().toString());
            text = text.replace("%caster_type%", caster.getType().name());
            text = text.replace("%caster_x%", String.valueOf(caster.getLocation().getBlockX()));
            text = text.replace("%caster_y%", String.valueOf(caster.getLocation().getBlockY()));
            text = text.replace("%caster_z%", String.valueOf(caster.getLocation().getBlockZ()));
            text = text.replace("%caster_biome%", caster.getLocation().getBlock().getBiome().name());
            
            if (caster instanceof Player p) {
                text = text.replace("%caster_hp%", String.valueOf((int) p.getHealth()));
                var attr = p.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH);
                if (attr != null) text = text.replace("%caster_max_hp%", String.valueOf((int) attr.getValue()));
                text = text.replace("%caster_food%", String.valueOf(p.getFoodLevel()));
                text = text.replace("%caster_exp%", String.valueOf(p.getLevel()));
            }
        }

        // --- PLACEHOLDERS DE TARGET ---
        if (target != null) {
            text = text.replace("%target_name%", target.getName());
            text = text.replace("%target_x%", String.valueOf(target.getLocation().getBlockX()));
            text = text.replace("%target_y%", String.valueOf(target.getLocation().getBlockY()));
            text = text.replace("%target_z%", String.valueOf(target.getLocation().getBlockZ()));
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
