package dev.itsrealperson.permadeath.pdcentities.script.condition;

import dev.itsrealperson.permadeath.pdcentities.script.Condition;
import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import org.bukkit.entity.Entity;
import java.util.Map;

public class MovementCondition implements Condition {
    private final boolean moving;

    public MovementCondition(Map<String, String> args) {
        this.moving = Boolean.parseBoolean(args.getOrDefault("type", "true"));
    }

    @Override
    public boolean check(SkillContext context) {
        Entity t = context.getTarget();
        if (t == null) return false;
        
        boolean isMoving = t.getVelocity().lengthSquared() > 0.01;
        return isMoving == moving;
    }
}
