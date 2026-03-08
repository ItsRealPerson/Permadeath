package dev.itsrealperson.permadeath.pdcentities.script.mechanic;

import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import java.util.Collection;
import java.util.Map;

public class SpinMechanic implements Mechanic {
    private final float angle;

    public SpinMechanic(Map<String, String> args) {
        this.angle = Float.parseFloat(args.getOrDefault("a", "45"));
    }

    @Override
    public void execute(SkillContext context, Collection<Entity> targets) {
        for (Entity target : targets) {
            org.bukkit.Location loc = target.getLocation();
            loc.setYaw(loc.getYaw() + angle);
            
            // Folia requiere teleportAsync para cualquier movimiento de entidades
            target.teleportAsync(loc);
        }
    }
}
