package dev.itsrealperson.permadeath.pdcentities.script.mechanic;

import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import java.util.Collection;
import java.util.Map;

public class VelocityMechanic implements Mechanic {
    private final double x, y, z;

    public VelocityMechanic(Map<String, String> args) {
        this.x = Double.parseDouble(args.getOrDefault("x", "0"));
        this.y = Double.parseDouble(args.getOrDefault("y", "0"));
        this.z = Double.parseDouble(args.getOrDefault("z", "0"));
    }

    @Override
    public void execute(SkillContext context, Collection<Entity> targets) {
        Vector v = new Vector(x, y, z);
        for (Entity target : targets) {
            target.setVelocity(target.getVelocity().add(v));
        }
    }
}
