package dev.itsrealperson.permadeath.pdcentities.script;

import dev.itsrealperson.permadeath.pdcentities.PdcEntitiesAddon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import java.util.HashMap;
import java.util.Map;

public class SkillContext {

    private final Entity caster;
    private final Entity target;
    private final Map<String, Double> variables = new HashMap<>();

    public SkillContext(Entity caster, Entity target) {
        this.caster = caster;
        this.target = target;
        
        variables.put("day", (double) PdcEntitiesAddon.getApi().getDay());
        
        if (caster instanceof LivingEntity living) {
            variables.put("caster.health", living.getHealth());
            variables.put("caster.max_health", living.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue());
        }
        
        if (target != null) {
            variables.put("distance", caster.getLocation().distance(target.getLocation()));
            if (target instanceof LivingEntity living) {
                variables.put("target.health", living.getHealth());
                variables.put("target.max_health", living.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue());
            }
        }
    }

    public Entity getCaster() { return caster; }
    public Entity getTarget() { return target; }
    public Map<String, Double> getVariables() { return variables; }
    
    public void setVariable(String name, double value) {
        variables.put(name, value);
    }
}
