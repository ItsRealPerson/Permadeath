package dev.itsrealperson.permadeath.pdcentities.script.condition;

import dev.itsrealperson.permadeath.pdcentities.script.Condition;
import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import java.util.Map;

public class VariableCondition implements Condition {
    private final String key;
    private final double min, max;

    public VariableCondition(Map<String, String> args) {
        this.key = args.getOrDefault("key", "var");
        this.min = Double.parseDouble(args.getOrDefault("min", "-999999"));
        this.max = Double.parseDouble(args.getOrDefault("max", "999999"));
    }

    @Override
    public boolean check(SkillContext context) {
        double val = context.getVariables().getOrDefault(key, 0.0);
        return val >= min && val <= max;
    }
}
