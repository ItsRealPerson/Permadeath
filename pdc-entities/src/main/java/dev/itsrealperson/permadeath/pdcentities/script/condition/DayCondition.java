package dev.itsrealperson.permadeath.pdcentities.script.condition;

import dev.itsrealperson.permadeath.pdcentities.script.Condition;
import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import java.util.Map;

public class DayCondition implements Condition {
    private final long min;
    private final long max;

    public DayCondition(Map<String, String> args) {
        this.min = Long.parseLong(args.getOrDefault("min", "0"));
        this.max = Long.parseLong(args.getOrDefault("max", "999999"));
    }

    @Override
    public boolean check(SkillContext context) {
        double day = context.getVariables().getOrDefault("day", 0.0);
        return day >= min && day <= max;
    }
}
