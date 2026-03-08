package dev.itsrealperson.permadeath.pdcentities.script.condition;

import dev.itsrealperson.permadeath.pdcentities.script.Condition;
import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import java.util.Map;

public class WeatherCondition implements Condition {
    private final String type;

    public WeatherCondition(Map<String, String> args) {
        this.type = args.getOrDefault("type", "CLEAR").toUpperCase();
    }

    @Override
    public boolean check(SkillContext context) {
        org.bukkit.World world = context.getCaster().getWorld();
        return switch (type) {
            case "STORM" -> world.hasStorm();
            case "THUNDER" -> world.isThundering();
            default -> !world.hasStorm();
        };
    }
}
