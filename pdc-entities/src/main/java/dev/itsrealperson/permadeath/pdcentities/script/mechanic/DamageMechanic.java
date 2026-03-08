package dev.itsrealperson.permadeath.pdcentities.script.mechanic;

import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import dev.itsrealperson.permadeath.api.script.FormulaEvaluator;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import java.util.Collection;
import java.util.Map;

public class DamageMechanic implements Mechanic {
    private final String amountFormula;

    public DamageMechanic(Map<String, String> args) {
        this.amountFormula = args.getOrDefault("amount", "1");
    }

    @Override
    public void execute(SkillContext context, Collection<Entity> targets) {
        double damage = FormulaEvaluator.evaluate(amountFormula, context.getVariables());
        for (Entity target : targets) {
            if (target instanceof LivingEntity living) {
                living.damage(damage, context.getCaster());
            }
        }
    }
}
