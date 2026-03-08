package dev.itsrealperson.permadeath.pdcentities.script.mechanic;

import dev.itsrealperson.permadeath.pdcentities.script.MobManager;
import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import dev.itsrealperson.permadeath.api.script.FormulaEvaluator;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import java.util.Collection;
import java.util.Map;
import java.util.Random;

public class SummonMechanic implements Mechanic {
    private final String mobType;
    private final String amountFormula;
    private final double radius;
    private final Random random = new Random();

    public SummonMechanic(Map<String, String> args) {
        this.mobType = args.getOrDefault("type", "ZOMBIE").toUpperCase();
        this.amountFormula = args.getOrDefault("amount", "1");
        this.radius = Double.parseDouble(args.getOrDefault("radius", "2"));
    }

    @Override
    public void execute(SkillContext context, Collection<Entity> targets) {
        int amount = (int) FormulaEvaluator.evaluate(amountFormula, context.getVariables());
        for (Entity target : targets) {
            Location baseLoc = target.getLocation();
            for (int i = 0; i < amount; i++) {
                Location spawnLoc = baseLoc.clone().add(
                    (random.nextDouble() - 0.5) * radius * 2,
                    0,
                    (random.nextDouble() - 0.5) * radius * 2
                );
                
                if (MobManager.spawnMob(mobType, spawnLoc, (long) context.getVariables().getOrDefault("day", 1.0).doubleValue()) == null) {
                    try {
                        EntityType vanillaType = EntityType.valueOf(mobType);
                        spawnLoc.getWorld().spawnEntity(spawnLoc, vanillaType);
                    } catch (Exception ignored) {}
                }
            }
        }
    }
}
