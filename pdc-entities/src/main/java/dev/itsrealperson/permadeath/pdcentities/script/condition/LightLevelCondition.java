package dev.itsrealperson.permadeath.pdcentities.script.condition;

import dev.itsrealperson.permadeath.pdcentities.script.Condition;
import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import java.util.Map;

public class LightLevelCondition implements Condition {
    private final int min;
    private final int max;

    public LightLevelCondition(Map<String, String> args) {
        this.min = Integer.parseInt(args.getOrDefault("min", "0"));
        this.max = Integer.parseInt(args.getOrDefault("max", "15"));
    }

    @Override
    public boolean check(SkillContext context) {
        int light = context.getCaster().getLocation().getBlock().getLightLevel();
        return light >= min && light <= max;
    }
}
