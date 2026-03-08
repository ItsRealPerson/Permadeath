package dev.itsrealperson.permadeath.pdcentities.script.condition;

import dev.itsrealperson.permadeath.pdcentities.script.Condition;
import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import org.bukkit.entity.Entity;
import java.util.Map;

public class OnGroundCondition implements Condition {
    public OnGroundCondition(Map<String, String> args) {}

    @Override
    public boolean check(SkillContext context) {
        return context.getCaster().isOnGround();
    }
}
