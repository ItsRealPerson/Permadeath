package dev.itsrealperson.permadeath.pdcentities.script.mechanic;

import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import java.util.Collection;
import java.util.Map;

public class ParticleLineMechanic implements Mechanic {
    private final Particle type;
    private final double density;

    public ParticleLineMechanic(Map<String, String> args) {
        this.type = Particle.valueOf(args.getOrDefault("type", "FLAME").toUpperCase());
        this.density = Double.parseDouble(args.getOrDefault("density", "0.5"));
    }

    @Override
    public void execute(SkillContext context, Collection<Entity> targets) {
        Location start = context.getCaster().getLocation().add(0, 1, 0);
        for (Entity target : targets) {
            Location end = target.getLocation().add(0, 1, 0);
            drawValueLine(start, end);
        }
    }

    private void drawValueLine(Location start, Location end) {
        double distance = start.distance(end);
        Vector direction = end.toVector().subtract(start.toVector()).normalize();
        
        for (double d = 0; d <= distance; d += density) {
            start.add(direction.multiply(density));
            start.getWorld().spawnParticle(type, start, 1, 0, 0, 0, 0);
            direction.normalize();
        }
    }
}
