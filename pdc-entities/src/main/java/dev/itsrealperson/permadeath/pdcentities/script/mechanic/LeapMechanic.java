package dev.itsrealperson.permadeath.pdcentities.script.mechanic;

import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import java.util.Collection;
import java.util.Map;

public class LeapMechanic implements Mechanic {
    private final double power;
    private final double upward;

    public LeapMechanic(Map<String, String> args) {
        this.power = Double.parseDouble(args.getOrDefault("p", "1.5"));
        this.upward = Double.parseDouble(args.getOrDefault("u", "0.4"));
    }

    @Override
    public void execute(SkillContext context, Collection<Entity> targets) {
        Entity caster = context.getCaster();
        
        for (Entity target : targets) {
            Vector dir = target.getLocation().toVector().subtract(caster.getLocation().toVector()).normalize();
            dir.multiply(power).setY(upward);
            caster.setVelocity(dir);
            break; // Salta hacia el primer objetivo
        }
    }
}
