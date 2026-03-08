package dev.itsrealperson.permadeath.pdcentities.script.condition;

import dev.itsrealperson.permadeath.pdcentities.script.Condition;
import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import java.util.Map;

public class LineOfSightCondition implements Condition {
    public LineOfSightCondition(Map<String, String> args) {}

    @Override
    public boolean check(SkillContext context) {
        Entity caster = context.getCaster();
        Entity target = context.getTarget();
        
        if (caster instanceof LivingEntity lc && target != null) {
            return lc.hasLineOfSight(target);
        }
        return true; // Si no hay target o no se puede comprobar, asumimos true para no romper skills
    }
}
