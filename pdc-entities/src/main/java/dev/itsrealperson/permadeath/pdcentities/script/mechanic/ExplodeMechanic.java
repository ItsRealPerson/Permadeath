package dev.itsrealperson.permadeath.pdcentities.script.mechanic;

import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import org.bukkit.entity.Entity;
import java.util.Collection;
import java.util.Map;

public class ExplodeMechanic implements Mechanic {
    private final float power;
    private final boolean fire;
    private final boolean breakBlocks;

    public ExplodeMechanic(Map<String, String> args) {
        this.power = Float.parseFloat(args.getOrDefault("power", "3.0"));
        this.fire = Boolean.parseBoolean(args.getOrDefault("fire", "false"));
        this.breakBlocks = Boolean.parseBoolean(args.getOrDefault("break", "false"));
    }

    @Override
    public void execute(SkillContext context, Collection<Entity> targets) {
        for (Entity target : targets) {
            target.getWorld().createExplosion(target.getLocation(), power, fire, breakBlocks, context.getCaster());
        }
    }

    @Override
    public void executeAtLocations(SkillContext context, Collection<org.bukkit.Location> locations) {
        for (org.bukkit.Location loc : locations) {
            loc.getWorld().createExplosion(loc, power, fire, breakBlocks, context.getCaster());
        }
    }
}
