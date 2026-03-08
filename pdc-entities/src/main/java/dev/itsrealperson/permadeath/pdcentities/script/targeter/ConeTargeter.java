package dev.itsrealperson.permadeath.pdcentities.script.targeter;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Selecciona entidades en un cono frontal.
 * Argumentos: r (radio), a (ángulo en grados)
 */
public class ConeTargeter implements Targeter {
    private final double radius;
    private final double angle;

    public ConeTargeter(Map<String, String> args) {
        this.radius = Double.parseDouble(args.getOrDefault("r", "5"));
        this.angle = Double.parseDouble(args.getOrDefault("a", "45"));
    }

    @Override
    public Collection<Entity> getTargets(Entity caster) {
        Vector direction = caster.getLocation().getDirection();
        return caster.getNearbyEntities(radius, radius, radius).stream()
                .filter(e -> e instanceof LivingEntity && e != caster)
                .filter(e -> {
                    Vector toEntity = e.getLocation().toVector().subtract(caster.getLocation().toVector()).normalize();
                    double dot = direction.dot(toEntity);
                    double angleToEntity = Math.toDegrees(Math.acos(dot));
                    return angleToEntity <= angle / 2.0;
                })
                .collect(Collectors.toList());
    }
}
