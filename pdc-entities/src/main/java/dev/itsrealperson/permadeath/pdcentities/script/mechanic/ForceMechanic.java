package dev.itsrealperson.permadeath.pdcentities.script.mechanic;

import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import java.util.Collection;
import java.util.Map;

public class ForceMechanic implements Mechanic {
    private final double power;
    private final boolean pull;

    public ForceMechanic(Map<String, String> args, boolean pull) {
        this.power = Double.parseDouble(args.getOrDefault("power", "1.0"));
        this.pull = pull;
    }

    @Override
    public void execute(SkillContext context, Collection<Entity> targets) {
        Entity caster = context.getCaster();
        for (Entity target : targets) {
            Vector v = pull ? 
                caster.getLocation().toVector().subtract(target.getLocation().toVector()) :
                target.getLocation().toVector().subtract(caster.getLocation().toVector());
            
            if (v.lengthSquared() > 0) {
                target.setVelocity(v.normalize().multiply(power));
            }
        }
    }
}
