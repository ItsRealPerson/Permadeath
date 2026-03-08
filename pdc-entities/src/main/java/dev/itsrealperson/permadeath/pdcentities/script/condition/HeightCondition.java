package dev.itsrealperson.permadeath.pdcentities.script.condition;

import dev.itsrealperson.permadeath.pdcentities.script.Condition;
import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import java.util.Map;

public class HeightCondition implements Condition {
    private final double min;
    private final double max;

    public HeightCondition(Map<String, String> args) {
        this.min = Double.parseDouble(args.getOrDefault("min", "-64"));
        this.max = Double.parseDouble(args.getOrDefault("max", "320"));
    }

    @Override
    public boolean check(SkillContext context) {
        double y = context.getCaster().getLocation().getY();
        return y >= min && y <= max;
    }
}
