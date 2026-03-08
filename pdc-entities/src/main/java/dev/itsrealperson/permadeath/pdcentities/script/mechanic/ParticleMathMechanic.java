package dev.itsrealperson.permadeath.pdcentities.script.mechanic;

import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import dev.itsrealperson.permadeath.api.script.FormulaEvaluator;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ParticleMathMechanic implements Mechanic {
    private final Particle type;
    private final String formulaX, formulaY, formulaZ;
    private final int steps;

    public ParticleMathMechanic(Map<String, String> args) {
        this.type = Particle.valueOf(args.getOrDefault("type", "FLAME").toUpperCase());
        this.formulaX = args.getOrDefault("x", "0");
        this.formulaY = args.getOrDefault("y", "0");
        this.formulaZ = args.getOrDefault("z", "0");
        this.steps = Integer.parseInt(args.getOrDefault("steps", "10"));
    }

    @Override
    public void execute(SkillContext context, Collection<Entity> targets) {
        for (Entity target : targets) {
            Location base = target.getLocation();
            Map<String, Double> vars = new HashMap<>(context.getVariables());
            
            for (int i = 0; i < steps; i++) {
                double t = (2 * Math.PI * i) / steps;
                vars.put("t", t);
                vars.put("i", (double) i);
                
                double x = FormulaEvaluator.evaluate(formulaX, vars);
                double y = FormulaEvaluator.evaluate(formulaY, vars);
                double z = FormulaEvaluator.evaluate(formulaZ, vars);
                
                base.getWorld().spawnParticle(type, base.clone().add(x, y + 1, z), 1, 0, 0, 0, 0);
            }
        }
    }
}
