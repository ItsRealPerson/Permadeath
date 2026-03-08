package dev.itsrealperson.permadeath.pdcentities.script.condition;

import dev.itsrealperson.permadeath.pdcentities.script.Condition;
import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import java.util.Map;

public class IsPlayerCondition implements Condition {
    public IsPlayerCondition(Map<String, String> args) {}

    @Override
    public boolean check(SkillContext context) {
        return context.getTarget() instanceof Player;
    }
}
