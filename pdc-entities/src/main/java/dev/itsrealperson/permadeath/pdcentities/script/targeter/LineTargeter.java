package dev.itsrealperson.permadeath.pdcentities.script.targeter;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.RayTraceResult;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class LineTargeter implements Targeter {
    private final double distance;

    public LineTargeter(Map<String, String> args) {
        this.distance = Double.parseDouble(args.getOrDefault("d", "10.0"));
    }

    @Override
    public Collection<Entity> getTargets(Entity caster) {
        List<Entity> targets = new ArrayList<>();
        if (!(caster instanceof LivingEntity living)) return targets;

        RayTraceResult result = caster.getWorld().rayTraceEntities(
            living.getEyeLocation(), 
            living.getEyeLocation().getDirection(), 
            distance, 
            e -> e != caster
        );

        if (result != null && result.getHitEntity() != null) {
            targets.add(result.getHitEntity());
        }
        return targets;
    }
}
