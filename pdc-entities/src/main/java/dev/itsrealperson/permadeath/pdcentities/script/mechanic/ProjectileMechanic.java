package dev.itsrealperson.permadeath.pdcentities.script.mechanic;

import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;
import java.util.Collection;
import java.util.Map;

public class ProjectileMechanic implements Mechanic {

    private final EntityType type;
    private final double velocityMultiplier;

    public ProjectileMechanic(Map<String, String> args) {
        this.type = EntityType.valueOf(args.getOrDefault("type", "ARROW").toUpperCase());
        this.velocityMultiplier = Double.parseDouble(args.getOrDefault("velocity", "1.0"));
    }

    @Override
    public void execute(SkillContext context, Collection<Entity> targets) {
        if (!(context.getCaster() instanceof LivingEntity living)) return;

        for (Entity target : targets) {
            org.bukkit.Location targetLoc;
            if (target instanceof LivingEntity lt) {
                targetLoc = lt.getEyeLocation();
            } else {
                targetLoc = target.getLocation().add(0, 0.5, 0);
            }

            Vector direction = targetLoc.toVector().subtract(living.getEyeLocation().toVector()).normalize();
            Entity projectile = living.getWorld().spawnEntity(living.getEyeLocation(), type);
            
            if (projectile instanceof Projectile p) {
                p.setShooter(living);
                p.setVelocity(direction.multiply(velocityMultiplier));
            } else {
                projectile.setVelocity(direction.multiply(velocityMultiplier));
            }
        }
    }
}
