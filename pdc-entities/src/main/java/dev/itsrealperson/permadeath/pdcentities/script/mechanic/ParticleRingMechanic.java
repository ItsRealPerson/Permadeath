package dev.itsrealperson.permadeath.pdcentities.script.mechanic;

import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import java.util.Collection;
import java.util.Map;

public class ParticleRingMechanic implements Mechanic {
    private final Particle type;
    private final double radius;
    private final int points;

    public ParticleRingMechanic(Map<String, String> args) {
        this.type = Particle.valueOf(args.getOrDefault("type", "FLAME").toUpperCase());
        this.radius = Double.parseDouble(args.getOrDefault("radius", "2.0"));
        this.points = Integer.parseInt(args.getOrDefault("points", "20"));
    }

    @Override
    public void execute(SkillContext context, Collection<Entity> targets) {
        for (Entity target : targets) {
            Location loc = target.getLocation().add(0, 0.1, 0);
            for (int i = 0; i < points; i++) {
                double angle = 2 * Math.PI * i / points;
                double x = radius * Math.cos(angle);
                double z = radius * Math.sin(angle);
                loc.add(x, 0, z);
                loc.getWorld().spawnParticle(type, loc, 1, 0, 0, 0, 0);
                loc.subtract(x, 0, z);
            }
        }
    }
}
