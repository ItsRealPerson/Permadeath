package dev.itsrealperson.permadeath.pdcentities.script.condition;

import dev.itsrealperson.permadeath.pdcentities.script.Condition;
import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import java.util.Map;

public class BlockingCondition implements Condition {
    public BlockingCondition(Map<String, String> args) {}

    @Override
    public boolean check(SkillContext context) {
        Entity target = context.getTarget();
        if (target instanceof Player p) {
            return p.isBlocking();
        }
        return false;
    }
}
