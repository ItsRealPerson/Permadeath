package dev.itsrealperson.permadeath.pdcentities.script.mechanic;

import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import java.util.Collection;
import java.util.Map;

public class SwapMechanic implements Mechanic {
    public SwapMechanic(Map<String, String> args) {}

    @Override
    public void execute(SkillContext context, Collection<Entity> targets) {
        Entity caster = context.getCaster();
        if (targets.isEmpty()) return;
        Entity target = targets.iterator().next();

        Location loc1 = caster.getLocation();
        Location loc2 = target.getLocation();

        caster.teleportAsync(loc2);
        target.teleportAsync(loc1);
        
        loc1.getWorld().playSound(loc1, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
        loc2.getWorld().playSound(loc2, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
    }
}
