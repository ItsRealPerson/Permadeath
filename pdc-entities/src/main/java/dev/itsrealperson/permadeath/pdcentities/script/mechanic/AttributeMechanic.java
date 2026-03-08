package dev.itsrealperson.permadeath.pdcentities.script.mechanic;

import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import dev.itsrealperson.permadeath.api.script.FormulaEvaluator;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import java.util.Collection;
import java.util.Map;

/**
 * Modifica atributos base de la entidad.
 * Argumentos: attr (String), amount (fórmula), operation (ADD/MULTIPLY)
 */
public class AttributeMechanic implements Mechanic {

    private final Attribute attribute;
    private final String amountFormula;
    private final boolean multiply;

    public AttributeMechanic(Map<String, String> args) {
        String attrStr = args.getOrDefault("t", args.getOrDefault("attr", "MAX_HEALTH")).toUpperCase();
        
        Attribute found = null;
        try {
            found = Attribute.valueOf(attrStr);
        } catch (Exception e) {
            // Reintentar sin prefijo GENERIC_ si falla (Minecraft 1.21+ usa nombres limpios)
            if (attrStr.startsWith("GENERIC_")) {
                try {
                    found = Attribute.valueOf(attrStr.replace("GENERIC_", ""));
                } catch (Exception ignored) {}
            }
        }
        
        this.attribute = found;
        this.amountFormula = args.getOrDefault("a", args.getOrDefault("amount", "0"));
        this.multiply = args.getOrDefault("o", args.getOrDefault("operation", "ADD")).contains("MULTIPLY");
        
        if (this.attribute == null) {
            dev.itsrealperson.permadeath.pdcentities.PdcEntitiesAddon.log("§c[AttributeMechanic] Atributo no válido: " + attrStr);
        }
    }

    @Override
    public void execute(SkillContext context, Collection<Entity> targets) {
        double value = FormulaEvaluator.evaluate(amountFormula, context.getVariables());
        for (Entity target : targets) {
            if (target instanceof LivingEntity living) {
                AttributeInstance instance = living.getAttribute(attribute);
                if (instance != null) {
                    if (multiply) instance.setBaseValue(instance.getBaseValue() * value);
                    else instance.setBaseValue(instance.getBaseValue() + value);
                }
            }
        }
    }
}
