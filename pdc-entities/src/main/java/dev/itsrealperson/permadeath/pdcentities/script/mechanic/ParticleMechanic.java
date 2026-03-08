package dev.itsrealperson.permadeath.pdcentities.script.mechanic;

import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import java.util.Collection;
import java.util.Map;

public class ParticleMechanic implements Mechanic {
    private final Particle type;
    private final int amount;
    private final double speed;

    public ParticleMechanic(Map<String, String> args) {
        this.type = Particle.valueOf(args.getOrDefault("type", "FLAME").toUpperCase());
        this.amount = Integer.parseInt(args.getOrDefault("amount", "10"));
        this.speed = Double.parseDouble(args.getOrDefault("speed", "0.1"));
    }

    @Override
    public void execute(SkillContext context, Collection<Entity> targets) {
        for (Entity target : targets) {
            target.getWorld().spawnParticle(type, target.getLocation().add(0, 1, 0), amount, 0.5, 0.5, 0.5, speed);
        }
    }
}
