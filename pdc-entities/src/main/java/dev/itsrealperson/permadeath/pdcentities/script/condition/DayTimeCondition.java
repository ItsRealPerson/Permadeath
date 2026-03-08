package dev.itsrealperson.permadeath.pdcentities.script.condition;

import dev.itsrealperson.permadeath.pdcentities.script.Condition;
import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import java.util.Map;

public class DayTimeCondition implements Condition {
    private final boolean isDay;

    public DayTimeCondition(Map<String, String> args) {
        this.isDay = args.getOrDefault("type", "DAY").equalsIgnoreCase("DAY");
    }

    @Override
    public boolean check(SkillContext context) {
        long time = context.getCaster().getWorld().getTime();
        boolean currentIsDay = time < 12300 || time > 23850;
        return isDay == currentIsDay;
    }
}
