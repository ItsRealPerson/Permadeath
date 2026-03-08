package dev.itsrealperson.permadeath.pdcentities.script.condition;

import dev.itsrealperson.permadeath.pdcentities.script.Condition;
import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import java.util.Map;

public class WorldCondition implements Condition {
    private final String worldName;

    public WorldCondition(Map<String, String> args) {
        this.worldName = args.getOrDefault("name", "world");
    }

    @Override
    public boolean check(SkillContext context) {
        return context.getCaster().getWorld().getName().equalsIgnoreCase(worldName);
    }
}
