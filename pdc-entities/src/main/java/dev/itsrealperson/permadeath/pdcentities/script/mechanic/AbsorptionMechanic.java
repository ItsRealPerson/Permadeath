package dev.itsrealperson.permadeath.pdcentities.script.mechanic;

import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import java.util.Collection;
import java.util.Map;

public class AbsorptionMechanic implements Mechanic {
    private final double amount;

    public AbsorptionMechanic(Map<String, String> args) {
        this.amount = Double.parseDouble(args.getOrDefault("amount", "4.0"));
    }

    @Override
    public void execute(SkillContext context, Collection<Entity> targets) {
        for (Entity target : targets) {
            if (target instanceof LivingEntity living) {
                living.setAbsorptionAmount(amount);
            }
        }
    }
}
