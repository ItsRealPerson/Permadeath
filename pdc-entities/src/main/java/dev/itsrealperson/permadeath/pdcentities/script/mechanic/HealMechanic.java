package dev.itsrealperson.permadeath.pdcentities.script.mechanic;

import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import dev.itsrealperson.permadeath.api.script.FormulaEvaluator;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import java.util.Collection;
import java.util.Map;

public class HealMechanic implements Mechanic {
    private final String amountFormula;

    public HealMechanic(Map<String, String> args) {
        this.amountFormula = args.getOrDefault("amount", "1");
    }

    @Override
    public void execute(SkillContext context, Collection<Entity> targets) {
        double amount = FormulaEvaluator.evaluate(amountFormula, context.getVariables());
        for (Entity target : targets) {
            if (target instanceof LivingEntity living) {
                double max = living.getAttribute(Attribute.MAX_HEALTH).getValue();
                living.setHealth(Math.min(max, living.getHealth() + amount));
            }
        }
    }
}
