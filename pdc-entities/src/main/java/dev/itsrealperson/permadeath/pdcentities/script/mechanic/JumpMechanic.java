package dev.itsrealperson.permadeath.pdcentities.script.mechanic;

import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import java.util.Collection;
import java.util.Map;

public class JumpMechanic implements Mechanic {
    private final double power;

    public JumpMechanic(Map<String, String> args) {
        this.power = Double.parseDouble(args.getOrDefault("p", "0.5"));
    }

    @Override
    public void execute(SkillContext context, Collection<Entity> targets) {
        for (Entity target : targets) {
            target.setVelocity(target.getVelocity().add(new org.bukkit.util.Vector(0, power, 0)));
        }
    }
}
