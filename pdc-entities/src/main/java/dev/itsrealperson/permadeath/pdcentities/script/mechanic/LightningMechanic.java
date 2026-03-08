package dev.itsrealperson.permadeath.pdcentities.script.mechanic;

import dev.itsrealperson.permadeath.pdcentities.script.SkillContext;
import org.bukkit.entity.Entity;
import java.util.Collection;
import java.util.Map;

public class LightningMechanic implements Mechanic {

    private final boolean doDamage;

    public LightningMechanic(Map<String, String> args) {
        this.doDamage = Boolean.parseBoolean(args.getOrDefault("damage", "true"));
    }

    @Override
    public void execute(SkillContext context, Collection<Entity> targets) {
        for (Entity target : targets) {
            spawnLightning(target.getLocation());
        }
    }

    @Override
    public void executeAtLocations(SkillContext context, Collection<org.bukkit.Location> locations) {
        for (org.bukkit.Location loc : locations) {
            spawnLightning(loc);
        }
    }

    private void spawnLightning(org.bukkit.Location loc) {
        if (doDamage) {
            loc.getWorld().strikeLightning(loc);
        } else {
            loc.getWorld().strikeLightningEffect(loc);
        }
    }
}
