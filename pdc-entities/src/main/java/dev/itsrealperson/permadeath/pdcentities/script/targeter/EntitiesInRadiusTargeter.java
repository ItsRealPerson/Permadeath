package dev.itsrealperson.permadeath.pdcentities.script.targeter;

import org.bukkit.entity.Entity;
import java.util.Collection;
import java.util.Map;

public class EntitiesInRadiusTargeter implements Targeter {
    private final double radius;

    public EntitiesInRadiusTargeter(Map<String, String> args) {
        this.radius = Double.parseDouble(args.getOrDefault("r", "5.0"));
    }

    @Override
    public Collection<Entity> getTargets(Entity caster) {
        return caster.getNearbyEntities(radius, radius, radius);
    }
}
