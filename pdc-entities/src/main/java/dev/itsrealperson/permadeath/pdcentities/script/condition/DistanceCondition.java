package dev.itsrealperson.permadeath.pdcentities.script.condition;

import dev.itsrealperson.permadeath.pdcentities.script.Condition;
import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import java.util.Map;

public class DistanceCondition implements Condition {
    private final double min;
    private final double max;

    public DistanceCondition(Map<String, String> args) {
        this.min = Double.parseDouble(args.getOrDefault("min", "0"));
        this.max = Double.parseDouble(args.getOrDefault("max", "999"));
    }

    @Override
    public boolean check(SkillContext context) {
        double dist = context.getVariables().getOrDefault("distance", -1.0);
        if (dist == -1.0) return true; // Si no hay target, ignoramos la condición
        return dist >= min && dist <= max;
    }
}
