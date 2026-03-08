package dev.itsrealperson.permadeath.pdcentities.script.condition;

import dev.itsrealperson.permadeath.pdcentities.script.Condition;
import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import java.util.Map;

public class TargetWithinCondition implements Condition {
    private final double radius;

    public TargetWithinCondition(Map<String, String> args) {
        this.radius = Double.parseDouble(args.getOrDefault("r", "5"));
    }

    @Override
    public boolean check(SkillContext context) {
        Entity caster = context.getCaster();
        if (caster instanceof Mob mob && mob.getTarget() != null) {
            return mob.getLocation().distanceSquared(mob.getTarget().getLocation()) <= (radius * radius);
        }
        return false;
    }
}
