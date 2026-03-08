package dev.itsrealperson.permadeath.pdcentities.script.mechanic;

import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import java.util.Collection;
import java.util.Map;
import java.util.Random;

public class TeleportMechanic implements Mechanic {

    private final double spread;
    private final boolean toTarget;
    private final Random random = new Random();

    public TeleportMechanic(Map<String, String> args) {
        this.spread = Double.parseDouble(args.getOrDefault("spread", "0"));
        this.toTarget = Boolean.parseBoolean(args.getOrDefault("totarget", "true"));
    }

    @Override
    public void execute(SkillContext context, Collection<Entity> targets) {
        if (targets.isEmpty()) return;
        Entity firstTarget = targets.iterator().next();

        if (toTarget) {
            Location loc = firstTarget.getLocation().clone().add(
                (random.nextDouble() - 0.5) * spread,
                0,
                (random.nextDouble() - 0.5) * spread
            );
            context.getCaster().teleportAsync(loc);
        } else {
            for (Entity target : targets) {
                target.teleportAsync(context.getCaster().getLocation());
            }
        }
    }
}
