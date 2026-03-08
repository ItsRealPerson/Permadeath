package dev.itsrealperson.permadeath.pdcentities.script.condition;

import dev.itsrealperson.permadeath.pdcentities.script.Condition;
import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import java.util.Map;

public class BiomeCondition implements Condition {
    private final String biomeName;

    public BiomeCondition(Map<String, String> args) {
        this.biomeName = args.getOrDefault("type", "PLAINS").toUpperCase();
    }

    @Override
    public boolean check(SkillContext context) {
        return context.getCaster().getLocation().getBlock().getBiome().name().equalsIgnoreCase(biomeName);
    }
}
