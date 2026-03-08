package dev.itsrealperson.permadeath.pdcentities.script.targeter;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class ForwardTargeter implements Targeter {
    private final double distance;

    public ForwardTargeter(Map<String, String> args) {
        this.distance = Double.parseDouble(args.getOrDefault("f", "5.0"));
    }

    @Override
    public Collection<Location> getTargetLocations(Entity caster) {
        if (!(caster instanceof LivingEntity living)) return Collections.emptyList();
        Location loc = living.getEyeLocation().add(living.getEyeLocation().getDirection().multiply(distance));
        return Collections.singletonList(loc);
    }
}
