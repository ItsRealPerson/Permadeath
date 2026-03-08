package dev.itsrealperson.permadeath.pdcentities.script.condition;

import dev.itsrealperson.permadeath.pdcentities.script.Condition;
import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import org.bukkit.entity.Player;
import java.util.Map;

public class SneakingCondition implements Condition {
    private final boolean shouldSneak;

    public SneakingCondition(Map<String, String> args) {
        this.shouldSneak = Boolean.parseBoolean(args.getOrDefault("type", "true"));
    }

    @Override
    public boolean check(SkillContext context) {
        if (context.getTarget() instanceof Player p) {
            return p.isSneaking() == shouldSneak;
        }
        return false;
    }
}
