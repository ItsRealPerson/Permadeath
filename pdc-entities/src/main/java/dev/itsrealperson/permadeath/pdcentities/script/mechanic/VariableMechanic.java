package dev.itsrealperson.permadeath.pdcentities.script.mechanic;

import dev.itsrealperson.permadeath.pdcentities.script.MobManager;
import dev.itsrealperson.permadeath.pdcentities.script.PdcMobInstance;
import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import dev.itsrealperson.permadeath.api.script.FormulaEvaluator;
import org.bukkit.entity.Entity;
import java.util.Collection;
import java.util.Map;

public class VariableMechanic implements Mechanic {
    private final String key;
    private final String formula;
    private final String operation;

    public VariableMechanic(Map<String, String> args) {
        this.key = args.getOrDefault("key", "var");
        this.formula = args.getOrDefault("val", "0");
        this.operation = args.getOrDefault("op", "SET").toUpperCase();
    }

    @Override
    public void execute(SkillContext context, Collection<Entity> targets) {
        PdcMobInstance instance = MobManager.getInstance(context.getCaster());
        if (instance == null) return;

        double current = instance.getPersistentVariables().getOrDefault(key, 0.0);
        double input = FormulaEvaluator.evaluate(formula, context.getVariables());
        
        double result = switch (operation) {
            case "ADD" -> current + input;
            case "SUB" -> current - input;
            case "MUL" -> current * input;
            case "DIV" -> input != 0 ? current / input : current;
            default -> input;
        };

        instance.setVariable(key, result);
    }
}
