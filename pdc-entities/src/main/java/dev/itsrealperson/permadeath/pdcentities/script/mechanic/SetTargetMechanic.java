package dev.itsrealperson.permadeath.pdcentities.script.mechanic;

import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import java.util.Collection;
import java.util.Map;

public class SetTargetMechanic implements Mechanic {

    public SetTargetMechanic(java.util.Map<String, String> args) {}

    @Override
    public void execute(SkillContext context, Collection<Entity> targets) {
        if (!(context.getCaster() instanceof Mob mob)) return;

        for (Entity target : targets) {
            if (target instanceof LivingEntity lt) {
                mob.setTarget(lt);
                break; // Solo puede tener un target a la vez
            }
        }
    }
}
