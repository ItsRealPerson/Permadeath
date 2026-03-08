package dev.itsrealperson.permadeath.pdcentities.script.mechanic;

import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import java.util.Collection;
import java.util.Map;

public class ParticleHelixMechanic implements Mechanic {
    private final Particle type;
    private final double radius;
    private final double height;

    public ParticleHelixMechanic(Map<String, String> args) {
        this.type = Particle.valueOf(args.getOrDefault("type", "SOUL_FIRE_FLAME").toUpperCase());
        this.radius = Double.parseDouble(args.getOrDefault("radius", "1.0"));
        this.height = Double.parseDouble(args.getOrDefault("height", "3.0"));
    }

    @Override
    public void execute(SkillContext context, Collection<Entity> targets) {
        for (Entity target : targets) {
            Location loc = target.getLocation();
            for (double y = 0; y <= height; y += 0.1) {
                double x = radius * Math.cos(y * 5);
                double z = radius * Math.sin(y * 5);
                loc.add(x, y, z);
                loc.getWorld().spawnParticle(type, loc, 1, 0, 0, 0, 0);
                loc.subtract(x, y, z);
            }
        }
    }
}
