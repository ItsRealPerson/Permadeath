package dev.itsrealperson.permadeath.pdcentities.script.targeter;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import java.util.Collection;
import java.util.Collections;

public class CurrentTargetTargeter implements Targeter {
    @Override
    public Collection<Entity> getTargets(Entity caster) {
        if (caster instanceof Mob mob && mob.getTarget() != null) {
            return Collections.singletonList(mob.getTarget());
        }
        return Collections.emptyList();
    }
}
