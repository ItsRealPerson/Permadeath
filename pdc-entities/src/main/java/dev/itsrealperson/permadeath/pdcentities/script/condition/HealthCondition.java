package dev.itsrealperson.permadeath.pdcentities.script.condition;

import dev.itsrealperson.permadeath.pdcentities.script.Condition;
import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import java.util.Map;

public class HealthCondition implements Condition {
    private final double minPct;
    private final double maxPct;
    private final boolean useTarget;

    public HealthCondition(Map<String, String> args) {
        this.minPct = Double.parseDouble(args.getOrDefault("min", "0")) / 100.0;
        this.maxPct = Double.parseDouble(args.getOrDefault("max", "100")) / 100.0;
        this.useTarget = Boolean.parseBoolean(args.getOrDefault("target", "false"));
    }

    @Override
    public boolean check(SkillContext context) {
        String prefix = useTarget ? "target." : "caster.";
        double current = context.getVariables().getOrDefault(prefix + "health", 0.0);
        double max = context.getVariables().getOrDefault(prefix + "max_health", 20.0);
        
        double pct = current / max;
        return pct >= minPct && pct <= maxPct;
    }
}
