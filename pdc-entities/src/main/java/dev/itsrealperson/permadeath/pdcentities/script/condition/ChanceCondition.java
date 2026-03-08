package dev.itsrealperson.permadeath.pdcentities.script.condition;

import dev.itsrealperson.permadeath.pdcentities.script.Condition;
import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import java.util.Map;
import java.util.Random;

public class ChanceCondition implements Condition {
    private final double chance;
    private final Random random = new Random();

    public ChanceCondition(Map<String, String> args) {
        this.chance = Double.parseDouble(args.getOrDefault("c", "50")) / 100.0;
    }

    @Override
    public boolean check(SkillContext context) {
        return random.nextDouble() <= chance;
    }
}
